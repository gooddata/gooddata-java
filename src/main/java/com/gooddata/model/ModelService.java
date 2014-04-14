package com.gooddata.model;

import com.gooddata.AbstractService;
import com.gooddata.project.Project;
import org.apache.commons.io.IOUtils;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * TODO
 */
public class ModelService extends AbstractService {

    public ModelService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ModelDiff getProjectModelDiff(Project project, DiffRequest diffRequest) {
        final DiffTask diffTask = restTemplate.postForObject(DiffRequest.URI, diffRequest, DiffTask.class, project.getId());
        return poll(diffTask.getUri(), new StatusOkConditionCallback(), ModelDiff.class);
    }

    public ModelDiff getProjectModelDiff(Project project, String targetModel) {
        return getProjectModelDiff(project, new DiffRequest(targetModel));
    }

    public ModelDiff getProjectModelDiff(Project project, InputStream targetModel) {
        try {
            return getProjectModelDiff(project, new String(IOUtils.toByteArray(targetModel)));
        } catch (IOException e) {
            throw new ModelException("Can't read target model", e);
        }
    }

    public void updateProjectModel(Project project, ModelDiff projectModelDiff) {
        for (ModelDiff.UpdateScript updateScript : projectModelDiff.getUpdateScripts()) {
            for (String maql : updateScript.getMaqlChunks()) {
                updateProjectModel(project, new MaqlDdl(maql));
            }
        }
    }

    public void updateProjectModel(Project project, MaqlDdl maqlDdl) {
        final MaqlDdlLinks linkEntries = restTemplate.postForObject(MaqlDdl.URI, maqlDdl, MaqlDdlLinks.class, project.getId());
        MaqlDdlTaskStatus maqlDdlTaskStatus = poll(linkEntries.getStatusLink(), MaqlDdlTaskStatus.class);
        if (!maqlDdlTaskStatus.isSuccess()) {
             throw new ModelException("Update project model finished with status " + maqlDdlTaskStatus.getStatus());
        }
    }



}
