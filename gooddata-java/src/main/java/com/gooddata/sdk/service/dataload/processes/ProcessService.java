/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.collections.MultiPageList;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import com.gooddata.sdk.model.dataload.processes.*;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.util.ZipHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collection;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Service to manage dataload processes and process executions.
 */
public class ProcessService extends AbstractService {

    protected static final UriTemplate SCHEDULE_TEMPLATE = new UriTemplate(Schedule.URI);
    protected static final UriTemplate PROCESS_TEMPLATE = new UriTemplate(DataloadProcess.URI);
    protected static final UriTemplate SCHEDULES_TEMPLATE = new UriTemplate(Schedules.URI);
    protected static final UriTemplate PROCESSES_TEMPLATE = new UriTemplate(DataloadProcesses.URI);
    protected static final UriTemplate USER_PROCESSES_TEMPLATE = new UriTemplate(DataloadProcesses.USER_PROCESSES_URI);
    private static final MediaType MEDIA_TYPE_ZIP = MediaType.parseMediaType("application/zip");
    private static final long MAX_MULTIPART_SIZE = 1024 * 1024;

    private final AccountService accountService;
    private final DataStoreService dataStoreService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     * @param restTemplate RESTful HTTP Spring template
     * @param accountService service to access accounts
     * @param dataStoreService service for upload process data
     * @param settings settings
     */
    public ProcessService(final RestTemplate restTemplate, final AccountService accountService,
                          final DataStoreService dataStoreService, final GoodDataSettings settings) {
        super(restTemplate, settings);
        this.dataStoreService = dataStoreService;
        this.accountService = notNull(accountService, "accountService");
    }

    /**
     * Create new process with given data by given project.
     * Process must have null path to prevent clashes with deploying from appstore.
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
        isTrue(process.getPath() == null, "Process path has to be null, use processData argument. If you want to create process from appstore, use method createProcessFromAppstore()");
        return postProcess(process, processData, getProcessesUri(project));
    }

    /**
     * Create new process without data.
     * Only some specific types of processes can be created without data.
     * Process must have null path to prevent clashes with deploying from appstore.
     *
     * @param project project to which the process belongs
     * @param process to create
     * @return created process
     */
    public DataloadProcess createProcess(Project project, DataloadProcess process) {
        notNull(project, "project");
        notNull(process, "process");
        isTrue(process.getPath() == null, "Process path has to be null. If you want to create process from appstore, use method createProcessFromAppstore()");
        return postProcess(process, getProcessesUri(project));
    }

    /**
     * Create new process from appstore.
     * Process must have set path field to valid appstore path in order to deploy from appstore.
     * This method is asynchronous, because when deploying from appstore, deployment worker can be triggered.
     *
     * @param project project to which the process belongs
     * @param process to create
     * @return created process
     */
    public FutureResult<DataloadProcess> createProcessFromAppstore(Project project, DataloadProcess process) {
        notNull(project, "project");
        notNull(process, "process");
        notEmpty(process.getPath(), "process path");
        return postProcess(process, getProcessesUri(project), HttpMethod.POST);
    }

    /**
     * Update process with given data by given project.
     * Process must have null path to prevent clashes with deploying from appstore.
     *
     * @param process to create
     * @param processData process data to upload
     * @return updated process
     */
    public DataloadProcess updateProcess(DataloadProcess process, File processData) {
        notNull(process, "process");
        notNull(process.getUri(), "process.uri");
        notNull(processData, "processData");
        isTrue(process.getPath() == null, "Process path has to be null, use processData argument. If you want to update process from appstore, use method updateProcessFromAppstore()");
        return postProcess(process, processData, URI.create(process.getUri()));
    }

    /**
     * Update process with data from appstore by given project.
     * Process must have set path field to valid appstore path in order to deploy from appstore.
     * This method is asynchronous, because when deploying from appstore, deployment worker can be triggered.
     *
     * @param process to update
     * @return updated process
     */
    public FutureResult<DataloadProcess> updateProcessFromAppstore(DataloadProcess process) {
        notNull(process, "process");
        notNull(process.getUri(), "process.uri");
        notEmpty(process.getPath(), "process path must not be empty");
        return postProcess(process, URI.create(process.getUri()), HttpMethod.PUT);
    }

    /**
     * Get process by given URI.
     * @param uri process uri
     * @return found process
     * @throws ProcessNotFoundException when the process doesn't exist
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
     * @throws ProcessNotFoundException when the process doesn't exist
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
        return listProcesses(USER_PROCESSES_TEMPLATE.expand(accountService.getCurrent().getId()));
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
            restTemplate.execute(process.getSourceUri(), HttpMethod.GET,
                    null, new OutputStreamResponseExtractor(outputStream));
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get process source " + process.getSourceUri(), e);
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
            restTemplate.execute(executionDetail.getLogUri(), HttpMethod.GET,
                    null, new OutputStreamResponseExtractor(outputStream));
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get process execution log " + executionDetail.getLogUri(), e);
        }
    }

    /**
     * Run given execution under given process
     *
     * @param execution to run
     * @return result of the execution
     * @throws ProcessExecutionException in case process can't be executed
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

        final String detailLink = executionTask.getDetailUri();

        return new PollResult<>(this, new AbstractPollHandler<Void, ProcessExecutionDetail>(executionTask.getPollUri(), Void.class, ProcessExecutionDetail.class) {
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
     * Update the given schedule
     *
     * @param schedule to update
     * @return updated Schedule
     * @throws ScheduleNotFoundException when the schedule doesn't exist
     */
    public Schedule updateSchedule(Schedule schedule) {
        notNull(schedule, "schedule");
        notNull(schedule.getUri(), "schedule.uri");

        final String uri = schedule.getUri();
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
     * @return MultiPageList list of found schedules or empty list
     */
    public PageableList<Schedule> listSchedules(final Project project) {
        return listSchedules(project, new PageRequest());
    }

    /**
     * Get defined page of paged list of schedules by given project.
     *
     * @param project   project of schedules
     * @param startPage page to be retrieved
     * @return MultiPageList list of found schedules or empty list
     */
    public PageableList<Schedule> listSchedules(final Project project,
                                                final Page startPage) {
        notNull(project, "project");
        notNull(startPage, "startPage");
        return new MultiPageList<>(startPage, page -> listSchedules(getSchedulesUri(project, page)));
    }

    /**
     * Delete given schedule
     *
     * @param schedule to delete
     */
    public void removeSchedule(final Schedule schedule) {
        notNull(schedule, "schedule");
        notNull(schedule.getUri(), "schedule.uri");

        try {
            restTemplate.delete(schedule.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to remove schedule " + schedule.getUri(), e);
        }
    }

    /**
     * Executes given schedule
     *
     * @param schedule to execute
     * @return schedule execution
     */
    public FutureResult<ScheduleExecution> executeSchedule(final Schedule schedule) {
        notNull(schedule, "schedule");
        notNull(schedule.getExecutionsUri(), "schedule.executionsUri");

        ScheduleExecution scheduleExecution;
        try {
            scheduleExecution = restTemplate.postForObject(schedule.getExecutionsUri(), new ScheduleExecution(), ScheduleExecution.class);
        } catch (GoodDataException | RestClientException e) {
            throw new ScheduleExecutionException("Cannot execute schedule", e);
        }

        return new PollResult<>(this, new AbstractPollHandler<ScheduleExecution, ScheduleExecution>(scheduleExecution.getUri(), ScheduleExecution.class, ScheduleExecution.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                final ScheduleExecution pollResult = extractData(response, ScheduleExecution.class);
                return pollResult.isFinished();
            }

            @Override
            public void handlePollResult(final ScheduleExecution pollResult) {
                setResult(pollResult);
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ScheduleExecutionException("Cannot execute schedule", e);
            }
        });
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
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notEmpty(id, "id");

        return SCHEDULE_TEMPLATE.expand(project.getId(), id);
    }

    private static URI getSchedulesUri(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        return SCHEDULES_TEMPLATE.expand(project.getId());
    }

    private static URI getSchedulesUri(final Project project, final Page page) {
        return page.getPageUri(UriComponentsBuilder.fromUri(getSchedulesUri(project)));
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
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notEmpty(id, "id");

        return PROCESS_TEMPLATE.expand(project.getId(), id);
    }

    private static URI getProcessesUri(Project project) {
        return PROCESSES_TEMPLATE.expand(project.getId());
    }

    private DataloadProcess postProcess(DataloadProcess process, File processData, URI postUri) {
        File tempFile = createTempFile("process", ".zip");

        try (OutputStream output = Files.newOutputStream(tempFile.toPath())) {
            ZipHelper.zip(processData, output);
        } catch (IOException e) {
            throw new GoodDataException("Unable to zip process data", e);
        }

        Object processToSend;
        HttpMethod method = HttpMethod.POST;
        if (dataStoreService != null && tempFile.length() > MAX_MULTIPART_SIZE) {
            try (final InputStream input = Files.newInputStream(tempFile.toPath())) {
                process.setPath(dataStoreService.getUri(tempFile.getName()).getPath());
                dataStoreService.upload(tempFile.getName(), input);
                processToSend = process;
                if (PROCESS_TEMPLATE.matches(postUri.toString())) {
                    method = HttpMethod.PUT;
                }
            } catch (IOException e) {
                throw new GoodDataException("Unable to access zipped process data at "
                        + tempFile.getAbsolutePath(), e);
            }
        } else {
            if (dataStoreService == null) { // we have no WebDAV support, so let's try send big file by multipart
                if (logger.isInfoEnabled()) {
                    logger.info("WebDAV calls not supported - sending huge file using multipart. " +
                            "Consider adding com.github.lookfirst:sardine to dependencies.");
                }
            }
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

    private FutureResult<DataloadProcess> postProcess(DataloadProcess process, URI postUri, HttpMethod method) {
        try {
            ResponseEntity<String> exchange = restTemplate.exchange(postUri, method, new HttpEntity<>(process), String.class);
            if (exchange.getStatusCode() == HttpStatus.ACCEPTED) { //deployment worker will create process
                AsyncTask asyncTask = mapper.readValue(exchange.getBody(), AsyncTask.class);
                return new PollResult<>(this, new SimplePollHandler<DataloadProcess>(asyncTask.getUri(), DataloadProcess.class) {

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
        if (!file.delete()) {
            // ignored
        }
    }
}
