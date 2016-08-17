package com.gooddata.dataload.processes;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.PollResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.account.AccountService;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageableList;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import com.gooddata.util.ZipHelper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang.Validate.isTrue;

/**
 * Service to manage dataload processes and process executions.
 */
public class ProcessService extends AbstractService {

    private static final MediaType MEDIA_TYPE_ZIP = MediaType.parseMediaType("application/zip");
    private static final long MAX_MULTIPART_SIZE = 1024 * 1024;

    private final AccountService accountService;
    private final DataStoreService dataStoreService;

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     * @param restTemplate RESTful HTTP Spring template
     * @param accountService service to access accounts
     * @param dataStoreService service for upload process data
     */
    public ProcessService(RestTemplate restTemplate, AccountService accountService, DataStoreService dataStoreService) {
        super(restTemplate);
        this.dataStoreService = dataStoreService;
        this.accountService = notNull(accountService, "accountService");
    }

    /**
     * Create new process with given data by given project.
     *
     * @param project project to which the process belongs
     * @param process to create
     * @param processData process data to upload
     * @return created process
     */
    public DataloadProcess createProcess(Project project, DataloadProcess process, File processData) {
        notNull(process, "process");
        notNull(processData, "processData");
        notNull(project, "project");

        return postProcess(process, processData, getProcessesUri(project));
    }

    /**
     * Create new process without data.
     * Only some specific types of processes can be created without data.
     *
     * @param project project to which the process belongs
     * @param process to create
     * @return created process
     */
    public DataloadProcess createProcess(Project project, DataloadProcess process) {
        notNull(project, "project");
        notNull(process, "process");

        return postProcess(process, getProcessesUri(project));
    }

    /**
     * Create new ruby process from ruby appstore.
     * Processes can be deployed only from predefined repos, which are substituted by placeholders
     *
     * @param project project to which the process belongs
     * @param process to create
     * @param repoPlaceholderName name of repo placeholder, (e.g. PUBLIC_APPSTORE)
     * @param commitIdentifier string in format commit/COMMIT_HASH or tag/TAG or branch/BRANCH
     * @param pathInRepo string representing path in git repo to application (e.g. /test/rubyHello)
     * @return created process
     */
    public FutureResult<DataloadProcess> createProcess(Project project, DataloadProcess process, String repoPlaceholderName, String commitIdentifier,
                                                       String pathInRepo) {
        notNull(project, "project");
        notNull(process, "process");
        notNull(repoPlaceholderName, "placeholderName");
        notNull(commitIdentifier, "commitIdentifier");
        notNull(pathInRepo, "pathInRepo");
        isTrue(process.getType().equals(ProcessType.RUBY.toString()), "only ruby processes can be deployed from appstore");
        String path = getAppstorePath(repoPlaceholderName, commitIdentifier, pathInRepo);
        return postProcess(process, path, getProcessesUri(project), HttpMethod.POST);
    }

    private String getAppstorePath(String repoPlaceholderName, String commitIdentifier, String pathInRepo) {
        return String.format("${%s}:%s:%s", repoPlaceholderName, commitIdentifier, pathInRepo);
    }

    /**
     * Update process with given data by given project.
     *
     * @param project project to which the process belongs
     * @param process to create
     * @param processData process data to upload
     * @return updated process
     */
    public DataloadProcess updateProcess(Project project, DataloadProcess process, File processData) {
        notNull(process, "process");
        notNull(processData, "processData");
        notNull(project, "project");

        return postProcess(process, processData, getProcessUri(project, process.getId()));
    }

    /**
     * Update process with data from appstore by given project.
     *
     * @param project project to which the process belongs
     * @param process to update
     * @param repoPlaceholderName name of repo placeholder, (e.g. PUBLIC_APPSTORE)
     * @param commitIdentifier string in format commit/COMMIT_HASH or tag/TAG or branch/BRANCH
     * @param pathInRepo string representing path in git repo to application (e.g. /test/rubyHello)
     * @return updated process
     */
    public FutureResult<DataloadProcess> updateProcess(Project project, DataloadProcess process, String repoPlaceholderName, String commitIdentifier,
                                                       String pathInRepo) {
        notNull(project, "project");
        notNull(process, "process");
        notNull(repoPlaceholderName, "placeholderName");
        notNull(commitIdentifier, "commitIdentifier");
        notNull(pathInRepo, "pathInRepo");
        String path = getAppstorePath(repoPlaceholderName, commitIdentifier, pathInRepo);
        return postProcess(process, path, getProcessUri(project, process.getId()), HttpMethod.PUT);
    }

    /**
     * Get process by given URI.
     * @param uri process uri
     * @return found process
     * @throws com.gooddata.dataload.processes.ProcessNotFoundException when the process doesn't exist
     */
    public DataloadProcess getProcessByUri(String uri) {
        notEmpty(uri, "uri");
        try {
            return restTemplate.getForObject(uri, DataloadProcess.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ProcessNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get process " + uri, e);
        }
    }

    /**
     * Get process by given id and project.
     * @param project project to which the process belongs
     * @param id process id
     * @return found process
     * @throws com.gooddata.dataload.processes.ProcessNotFoundException when the process doesn't exist
     */
    public DataloadProcess getProcessById(Project project, String id) {
        notEmpty(id, "id");
        notNull(project, "project");
        return getProcessByUri(getProcessUri(project, id).toString());
    }

    /**
     * Get list of processes by given project.
     * @param project project of processes
     * @return list of found processes or empty list
     */
    public Collection<DataloadProcess> listProcesses(Project project) {
        notNull(project, "project");
        return listProcesses(getProcessesUri(project));
    }

    /**
     * Get list of current user processes by given user account.
     * @return list of found processes or empty list
     */
    public Collection<DataloadProcess> listUserProcesses() {
        return listProcesses(DataloadProcesses.USER_PROCESSES_TEMPLATE.expand(accountService.getCurrent().getId()));
    }

    /**
     * Delete given process
     * @param process to delete
     */
    public void removeProcess(DataloadProcess process) {
        notNull(process, "process");
        try {
            restTemplate.delete(process.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to remove process " + process.getUri(), e);
        }
    }

    /**
     * Get process source data. Source data are fetched as zip and written to given stream.
     *
     * @param process process to fetch data of
     * @param outputStream stream where to write fetched data
     */
    public void getProcessSource(DataloadProcess process, OutputStream outputStream) {
        notNull(process, "process");
        notNull(outputStream, "outputStream");
        try {
            restTemplate.execute(process.getSourceLink(), HttpMethod.GET,
                    null, new OutputStreamResponseExtractor(outputStream));
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get process source " + process.getSourceLink(), e);
        }
    }

    /**
     * Get process execution log
     * @param executionDetail execution to log of
     * @param outputStream stream to write the log to
     */
    public void getExecutionLog(ProcessExecutionDetail executionDetail, OutputStream outputStream) {
        notNull(executionDetail, "executionDetail");
        notNull(outputStream, "outputStream");
        try {
            restTemplate.execute(executionDetail.getLogLink(), HttpMethod.GET,
                    null, new OutputStreamResponseExtractor(outputStream));
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get process execution log " + executionDetail.getLogLink(), e);
        }
    }

    /**
     * Run given execution under given process
     *
     * @param execution to run
     * @return result of the execution
     * @throws com.gooddata.dataload.processes.ProcessExecutionException in case process can't be executed
     */
    public FutureResult<ProcessExecutionDetail> executeProcess(ProcessExecution execution) {
        notNull(execution, "execution");
        ProcessExecutionTask executionTask;
        try {
            executionTask = restTemplate.postForObject(execution.getExecutionsUri(), execution, ProcessExecutionTask.class);
        } catch (GoodDataException | RestClientException e) {
            throw new ProcessExecutionException("Cannot execute process", e);
        }

        if (executionTask == null) {
            throw new ProcessExecutionException("Cannot find started execution.");
        }

        final String detailLink = executionTask.getDetailLink();

        return new PollResult<>(this, new AbstractPollHandler<Void, ProcessExecutionDetail>(executionTask.getPollLink(), Void.class, ProcessExecutionDetail.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
            }

            @Override
            public void handlePollResult(Void pollResult) {
                final ProcessExecutionDetail executionDetail = getProcessExecutionDetailByUri(detailLink);
                if (!executionDetail.isSuccess()) {
                    throw new ProcessExecutionException("Execution was not successful", executionDetail);
                } else {
                    setResult(executionDetail);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                ProcessExecutionDetail detail = null;
                try {
                    detail = getProcessExecutionDetailByUri(detailLink);
                } catch (GoodDataException ignored) { }
                throw new ProcessExecutionException("Can't execute " + e.getText(), detail, e);
            }

            private ProcessExecutionDetail getProcessExecutionDetailByUri(final String uri) {
                try {
                    return restTemplate.getForObject(uri, ProcessExecutionDetail.class);
                } catch (GoodDataException | RestClientException e) {
                    throw new ProcessExecutionException("Execution finished, but cannot get its result.", e, uri);
                }
            }

        });
    }

    /**
     * Create new schedule with given data by given project.
     *
     * @param project  project to which the process belongs
     * @param schedule to create
     * @return created schedule
     */
    public Schedule createSchedule(Project project, Schedule schedule) {
        notNull(schedule, "schedule");
        notNull(project, "project");

        return postSchedule(schedule, getSchedulesUri(project));
    }

    /**
     * Update connector integration
     *
     * @param project  project
     * @param schedule to update
     * @return updated Schedule
     * @throws ScheduleNotFoundException when the schedule doesn't exist
     */
    public Schedule updateSchedule(final Project project, Schedule schedule) {
        notNull(schedule, "schedule");
        notNull(project, "project");

        final String uri = getScheduleUri(project, schedule.getId()).toString();
        try {
            final ResponseEntity<Schedule> response = restTemplate
                    .exchange(uri, HttpMethod.PUT, new HttpEntity<>(schedule), Schedule.class);
            if (response == null) {
                throw new GoodDataException("Unable to update schedule. No response returned from API.");
            }
            return response.getBody();
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ScheduleNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get schedule " + uri, e);
        }
    }

    /**
     * Get schedule by given URI.
     *
     * @param uri schedule uri
     * @return found schedule
     * @throws ScheduleNotFoundException when the schedule doesn't exist
     */
    public Schedule getScheduleByUri(String uri) {
        notEmpty(uri, "uri");
        try {
            return restTemplate.getForObject(uri, Schedule.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ScheduleNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get schedule " + uri, e);
        }
    }

    /**
     * Get schedule by given id and project.
     *
     * @param project project to which the schedule belongs
     * @param id      schedule id
     * @return found schedule
     * @throws ScheduleNotFoundException when the process doesn't exist
     */
    public Schedule getScheduleById(Project project, String id) {
        notEmpty(id, "id");
        notNull(project, "project");
        return getScheduleByUri(getScheduleUri(project, id).toString());
    }

    /**
     * Get first page of paged list of schedules by given project.
     *
     * @param project project of schedules
     * @return list of found schedules or empty list
     */
    public PageableList<Schedule> listSchedules(Project project) {
        notNull(project, "project");
        return listSchedules(getSchedulesUri(project));
    }

    /**
     * Get defined page of paged list of schedules by given project.
     *
     * @param project project of schedules
     * @param page    page to be retrieved
     * @return list of found schedules or empty list
     */
    public PageableList<Schedule> listSchedules(Project project, Page page) {
        notNull(project, "project");
        notNull(page, "page");
        return listSchedules(page.getPageUri(UriComponentsBuilder.fromUri(getSchedulesUri(project))));
    }

    /**
     * Delete given schedule
     *
     * @param schedule to delete
     */
    public void removeSchedule(Schedule schedule) {
        notNull(schedule, "schedule");
        try {
            restTemplate.delete(schedule.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to remove schedule " + schedule.getUri(), e);
        }
    }

    private PageableList<Schedule> listSchedules(URI uri) {
        try {
            final Schedules schedules = restTemplate.getForObject(uri, Schedules.class);
            if (schedules == null) {
                return new PageableList<>();
            }
            return schedules;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list schedules", e);
        }
    }

    private static URI getScheduleUri(Project project, String id) {
        return Schedule.TEMPLATE.expand(project.getId(), id);
    }

    private static URI getSchedulesUri(Project project) {
        return Schedules.TEMPLATE.expand(project.getId());
    }

    private Schedule postSchedule(Schedule schedule, URI postUri) {
        try {
            return restTemplate.postForObject(postUri, schedule, Schedule.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to post schedule.", e);
        }
    }

    private Collection<DataloadProcess> listProcesses(URI uri) {
        try {
            final DataloadProcesses processes = restTemplate.getForObject(uri, DataloadProcesses.class);
            if (processes == null) {
                throw new GoodDataException("empty response from API call");
            } else if (processes.getItems() == null) {
                return emptyList();
            }
            return processes.getItems();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list processes", e);
        }
    }

    private static URI getProcessUri(Project project, String id) {
        return DataloadProcess.TEMPLATE.expand(project.getId(), id);
    }

    private static URI getProcessesUri(Project project) {
        return DataloadProcesses.TEMPLATE.expand(project.getId());
    }

    private DataloadProcess postProcess(DataloadProcess process, File processData, URI postUri) {
        File tempFile = createTempFile("process", ".zip");

        try (FileOutputStream output = new FileOutputStream(tempFile)) {
            ZipHelper.zip(processData, output);
        } catch (IOException e) {
            throw new GoodDataException("Unable to zip process data", e);
        }

        Object processToSend;
        HttpMethod method = HttpMethod.POST;
        if (tempFile.length() > MAX_MULTIPART_SIZE) {
            try {
                process.setPath(dataStoreService.getUri(tempFile.getName()).getPath());
                dataStoreService.upload(tempFile.getName(), new FileInputStream(tempFile));
                processToSend = process;
                if (DataloadProcess.TEMPLATE.matches(postUri.toString())) {
                    method = HttpMethod.PUT;
                }
            } catch (FileNotFoundException e) {
                throw new GoodDataException("Unable to access zipped process data at "
                        + tempFile.getAbsolutePath(), e);
            }
        } else {
            final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>(2);
            parts.add("process", process);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MEDIA_TYPE_ZIP);
            parts.add("data", new HttpEntity<>(new FileSystemResource(tempFile), headers));
            processToSend = parts;
        }

        try {
            final ResponseEntity<DataloadProcess> response = restTemplate
                    .exchange(postUri, method, new HttpEntity<>(processToSend), DataloadProcess.class);
            if (response == null) {
                throw new GoodDataException("Unable to post dataload process. No response returned from API.");
            }
            return response.getBody();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to post dataload process.", e);
        } finally {
            deleteTempFile(tempFile);
        }
    }

    private FutureResult<DataloadProcess> postProcess(DataloadProcess process, String appstorePath, URI postUri, HttpMethod method) {
        try {
            process.setPath(appstorePath);
            ResponseEntity<String> exchange = restTemplate.exchange(postUri, method, new HttpEntity<>(process), String.class);
            if (exchange.getStatusCode() == HttpStatus.ACCEPTED) { //deployment worker will create process
                String uri = mapper.readTree(exchange.getBody()).findPath("poll").asText();
                return new PollResult<>(this, new SimplePollHandler<DataloadProcess>(uri, DataloadProcess.class) {

                    @Override
                    public void handlePollException(GoodDataRestException e) {
                        throw new GoodDataException("Creating process failed", e);
                    }
                });
            } else if (exchange.getStatusCode() == HttpStatus.OK) { //object has been found in package registry, deployment worker is not triggered
                final DataloadProcess dataloadProcess = mapper.readValue(exchange.getBody(), DataloadProcess.class);
                return new PollResult<>(this, new SimplePollHandler<DataloadProcess>(dataloadProcess.getUri(), DataloadProcess.class) {

                    @Override
                    public void handlePollException(GoodDataRestException e) {
                        throw new GoodDataException("Creating process failed", e);
                    }
                });
            } else {
                throw new IllegalStateException("Unexpected status code from resource: " + exchange.getStatusCode());
            }
        } catch (RestClientException | IOException e) {
            throw new GoodDataException("Creating process failed", e);
        }
    }

    private DataloadProcess postProcess(DataloadProcess process, URI postUri) {
        try {
            return restTemplate.postForObject(postUri, process, DataloadProcess.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create dataload process.", e);
        }
    }

    private File createTempFile(String prefix, String suffix) {
        File tempFile;
        try {
            tempFile = File.createTempFile(prefix, suffix);
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new GoodDataException("Unable to create temporary file", e);
        }
        return tempFile;
    }

    private void deleteTempFile(File file) {
        notNull(file, "file");
        file.delete();
    }

}
