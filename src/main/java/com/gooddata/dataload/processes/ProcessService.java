package com.gooddata.dataload.processes;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.util.Collections.emptyList;

import com.gooddata.AbstractService;
import com.gooddata.AbstractPollHandler;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.util.ZipUtils;
import com.gooddata.account.AccountService;
import com.gooddata.project.Project;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;

/**
 * Service to manage dataload processes and process executions.
 */
public class ProcessService extends AbstractService {

    private static final MediaType MEDIA_TYPE_ZIP = MediaType.parseMediaType("application/zip");

    private final AccountService accountService;

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @param accountService service to access accounts
     */
    public ProcessService(RestTemplate restTemplate, AccountService accountService) {
        super(restTemplate);
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
                    noopRequestCallback, new OutputStreamResponseExtractor(outputStream));
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
                    noopRequestCallback, new OutputStreamResponseExtractor(outputStream));
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

        return new FutureResult<>(this, new AbstractPollHandler<Void, ProcessExecutionDetail>(executionTask.getPollLink(), Void.class, ProcessExecutionDetail.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
            }

            @Override
            public void handlePollResult(Void pollResult) {
                ProcessExecutionDetail executionDetail;
                try {
                    executionDetail = restTemplate.getForObject(detailLink, ProcessExecutionDetail.class);
                } catch (GoodDataException | RestClientException e) {
                    throw new ProcessExecutionException("Execution finished, but cannot get its result.", e, detailLink);
                }

                if (!executionDetail.isSuccess()) {
                    throw new ProcessExecutionException("Execution was not successful", executionDetail);
                } else {
                    setResult(executionDetail);
                }
            }
        });
    }

    private Collection<DataloadProcess> listProcesses(URI uri) {
        try {
            final DataloadProcesses processes = restTemplate.getForObject(uri, DataloadProcesses.class);
            if (processes == null || processes.getItems() == null) {
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

        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>(2);
        parts.add("process", process);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MEDIA_TYPE_ZIP);
        parts.add("data", new HttpEntity<>(new FileSystemResource(tempFile), headers));

        try (FileOutputStream output = new FileOutputStream(tempFile)) {
            ZipUtils.zip(processData, output);
        } catch (IOException e) {
            throw new GoodDataException("Unable to zip process data", e);
        }

        try {
            return restTemplate.postForObject(postUri, parts, DataloadProcess.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to post dataload process.", e);
        } finally {
            deleteTempFile(tempFile);
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
