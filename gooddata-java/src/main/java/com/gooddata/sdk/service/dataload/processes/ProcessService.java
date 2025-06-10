/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageBrowser;
import com.gooddata.sdk.common.collections.PageRequest;
import com.gooddata.sdk.common.util.SpringMutableUri;
import com.gooddata.sdk.model.dataload.processes.AsyncTask;
import com.gooddata.sdk.model.dataload.processes.DataloadProcess;
import com.gooddata.sdk.model.dataload.processes.DataloadProcesses;
import com.gooddata.sdk.model.dataload.processes.ProcessExecution;
import com.gooddata.sdk.model.dataload.processes.ProcessExecutionDetail;
import com.gooddata.sdk.model.dataload.processes.ProcessExecutionTask;
import com.gooddata.sdk.model.dataload.processes.Schedule;
import com.gooddata.sdk.model.dataload.processes.ScheduleExecution;
import com.gooddata.sdk.model.dataload.processes.Schedules;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractPollHandler;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.FutureResult;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.PollResult;
import com.gooddata.sdk.service.SimplePollHandler;
import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.util.ZipHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collection;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.Validate.isTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service to manage dataload processes and process executions.
 */
public class ProcessService extends AbstractService {

    private static final Logger log = LoggerFactory.getLogger(ProcessService.class);

    public static final UriTemplate SCHEDULE_TEMPLATE = new UriTemplate(Schedule.URI);
    public static final UriTemplate PROCESS_TEMPLATE = new UriTemplate(DataloadProcess.URI);
    public static final UriTemplate SCHEDULES_TEMPLATE = new UriTemplate(Schedules.URI);
    public static final UriTemplate PROCESSES_TEMPLATE = new UriTemplate(DataloadProcesses.URI);
    public static final UriTemplate USER_PROCESSES_TEMPLATE = new UriTemplate(DataloadProcesses.USER_PROCESSES_URI);
    private static final MediaType MEDIA_TYPE_ZIP = MediaType.parseMediaType("application/zip");
    private static final long MAX_MULTIPART_SIZE = 1024L * 1024L;

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
    public ProcessService(final WebClient webClient, final AccountService accountService,
                          final DataStoreService dataStoreService, final GoodDataSettings settings) {
        super(webClient, settings);
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
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(DataloadProcess.class)
                    .block();
        } catch (WebClientResponseException e) { 
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ProcessNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (GoodDataRestException e) {
            // Map 404 GoodDataRestException to ProcessNotFoundException
            if (e.getStatusCode() == 404) {
                throw new ProcessNotFoundException(uri, e);
            } else {
                throw new GoodDataException("Unable to get process " + uri, e);
            }
        } catch (Exception e) {
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
            webClient.delete()
                    .uri(process.getUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
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
            byte[] bytes = webClient.get()
                    .uri(process.getSourceUri())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if (bytes != null) {
                outputStream.write(bytes);
            }
        } catch (WebClientResponseException | IOException e) {
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
            byte[] bytes = webClient.get()
                    .uri(executionDetail.getLogUri())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if (bytes != null) {
                outputStream.write(bytes);
            }
        } catch (WebClientResponseException | IOException e) {
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
            executionTask = webClient.post()
                    .uri(execution.getExecutionsUri())
                    .bodyValue(execution)
                    .retrieve()
                    .bodyToMono(ProcessExecutionTask.class)
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
            throw new ProcessExecutionException("Cannot execute process", e);
        }

        if (executionTask == null) {
            throw new ProcessExecutionException("Cannot find started execution.");
        }

        final String detailLink = executionTask.getDetailUri();

        return new PollResult<>(this, new AbstractPollHandler<Void, ProcessExecutionDetail>(executionTask.getPollUri(), Void.class, ProcessExecutionDetail.class) {
            @Override
            public boolean isFinished(ClientResponse response) {
                return response.statusCode().equals(HttpStatus.NO_CONTENT);
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
                    return webClient.get()
                            .uri(uri)
                            .retrieve()
                            .bodyToMono(ProcessExecutionDetail.class)
                            .block();
                } catch (WebClientResponseException | GoodDataException e) {
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
                return webClient.put()
                        .uri(uri)
                        .bodyValue(schedule)
                        .retrieve()
                        .bodyToMono(Schedule.class)
                        .block();
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                    throw new ScheduleNotFoundException(uri, e);
                } else {
                    throw e;
                }
            } catch (Exception e) {
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
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Schedule.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ScheduleNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (Exception e) {
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
     * Get browser of schedules by given project.
     *
     * @param project project of schedules
     * @return {@link PageBrowser} of found schedules or empty list
     */
    public PageBrowser<Schedule> listSchedules(final Project project) {
        return listSchedules(project, new CustomPageRequest());
    }

    /**
     * Get defined page of paged list of schedules by given project.
     *
     * @param project   project of schedules
     * @param startPage page to be retrieved
     * @return {@link PageBrowser} list of found schedules or empty list
     */
    public PageBrowser<Schedule> listSchedules(final Project project,
                                                final PageRequest startPage) {
        notNull(project, "project");
        notNull(startPage, "startPage");
        return new PageBrowser<>(startPage, page -> listSchedules(getSchedulesUri(project, page)));
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
            webClient.delete()
                    .uri(schedule.getUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
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
            scheduleExecution = webClient.post()
                    .uri(schedule.getExecutionsUri())
                    .bodyValue(new ScheduleExecution())
                    .retrieve()
                    .bodyToMono(ScheduleExecution.class)
                    .block();
        } catch (RuntimeException e) {
            throw new ScheduleExecutionException("Cannot execute schedule", e);
        }

        return new PollResult<>(this, new AbstractPollHandler<ScheduleExecution, ScheduleExecution>(
                notNullState(scheduleExecution, "created schedule execution").getUri(),
                ScheduleExecution.class, ScheduleExecution.class) {
            @Override
            public boolean isFinished(ClientResponse response) {
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

    private Page<Schedule> listSchedules(URI uri) {
        try {
            final Schedules schedules = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Schedules.class)
                    .block();
            if (schedules == null) {
                return new Page<>();
            }
            return schedules;
        } catch (WebClientResponseException | GoodDataException e) { 
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

    private static URI getSchedulesUri(final Project project, final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getSchedulesUri(project)));
    }

    private Schedule postSchedule(Schedule schedule, URI postUri) {
        try {
            return webClient.post()
                    .uri(postUri)
                    .bodyValue(schedule)
                    .retrieve()
                    .bodyToMono(Schedule.class)
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable to post schedule.", e);
        }
    }

    private Collection<DataloadProcess> listProcesses(URI uri) {
        try {
            final DataloadProcesses processes = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(DataloadProcesses.class)
                    .block();
            if (processes == null) {
                throw new GoodDataException("empty response from API call");
            } else if (processes.getItems() == null) {
                return emptyList();
            }
            return processes.getItems();
        } catch (RuntimeException e) {
            // Wrap any unexpected error in GoodDataException
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

        try {
            if (dataStoreService != null && tempFile.length() > MAX_MULTIPART_SIZE) {
                try (final InputStream input = Files.newInputStream(tempFile.toPath())) {
                    process.setPath(dataStoreService.getUri(tempFile.getName()).getPath());
                    dataStoreService.upload(tempFile.getName(), input);

                    // Log process details before sending
                    log.debug("Process details: {}", process);

                    // Check for null before sending the process object
                    if (process == null) {
                        throw new IllegalArgumentException("Request body for process POST must not be null");
                    }

                    DataloadProcess result = webClient.post()
                            .uri(postUri)
                            .bodyValue(process)
                            .retrieve()
                            .bodyToMono(DataloadProcess.class)
                            .block();
                    if (result == null) {
                        throw new GoodDataException("No response from API (null result) when posting dataload process (large, via WebDAV).");
                    }
                    return result;
                } catch (IOException e) {
                    throw new GoodDataException("Unable to access zipped process data at " + tempFile.getAbsolutePath(), e);
                }
            } else {
                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("process", process);
                builder.part("data", new FileSystemResource(tempFile))
                        .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_ZIP.toString());

                // Log process details before sending multipart request
                log.debug("Process details: {}", process);

                DataloadProcess result = webClient.post()
                        .uri(postUri)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(builder.build()))
                        .retrieve()
                        .bodyToMono(DataloadProcess.class)
                        .block();
                if (result == null) {
                    throw new GoodDataException("No response from API (null result) when posting dataload process (multipart).");
                }
                return result;
            }
        } catch (Exception e) {
            throw new GoodDataException("Unable to post dataload process.", e);
        } finally {
            deleteTempFile(tempFile);
        }
    }




    private FutureResult<DataloadProcess> postProcess(DataloadProcess process, URI postUri, HttpMethod method) {
        try {
            ClientResponse response;
            if (method == HttpMethod.POST) {
                response = webClient.post()
                    .uri(postUri)
                    .bodyValue(process)
                    .exchange()
                    .block();
            } else if (method == HttpMethod.PUT) {
                response = webClient.put()
                    .uri(postUri)
                    .bodyValue(process)
                    .exchange()
                    .block();
            } else {
                throw new IllegalStateException("Unsupported HTTP method: " + method);
            }

            if (response == null) {
                throw new GoodDataException("No response received from API call");
            }

            ObjectMapper mapper = new ObjectMapper();
            String body = response.bodyToMono(String.class).block();

            if (response.statusCode().equals(HttpStatus.ACCEPTED)) {
                AsyncTask asyncTask = mapper.readValue(body, AsyncTask.class);
                return new PollResult<>(this, new SimplePollHandler<DataloadProcess>(asyncTask.getUri(), DataloadProcess.class) {
                    @Override
                    public void handlePollException(GoodDataRestException e) {
                        throw new GoodDataException("Creating process failed", e);
                    }
                });
            } else if (response.statusCode().equals(HttpStatus.OK)) {
                final DataloadProcess dataloadProcess = mapper.readValue(body, DataloadProcess.class);
                return new PollResult<>(this, new SimplePollHandler<DataloadProcess>(dataloadProcess.getUri(), DataloadProcess.class) {
                    @Override
                    public void handlePollException(GoodDataRestException e) {
                        throw new GoodDataException("Creating process failed", e);
                    }
                });
            } else {
                throw new IllegalStateException("Unexpected status code from resource: " + response.statusCode());
            }
        } catch (WebClientResponseException | IOException e) {
            throw new GoodDataException("Creating process failed", e);
        }
    }

    private DataloadProcess postProcess(DataloadProcess process, URI postUri) {
        try {
            return webClient.post()
                    .uri(postUri)
                    .bodyValue(process)
                    .retrieve()
                    .bodyToMono(DataloadProcess.class)
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
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
