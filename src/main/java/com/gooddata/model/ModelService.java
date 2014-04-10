package com.gooddata.model;

import com.gooddata.AbstractService;
import com.gooddata.project.Project;
import org.springframework.web.client.RestTemplate;

/**
 * TODO
 */
public class ModelService extends AbstractService {

    public ModelService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ModelDiff getProjectModelDiff(Project project, DiffRequest diffRequest) {
        final AsyncTask asyncTask = restTemplate.postForObject(DiffRequest.URI, diffRequest, AsyncTask.class, project.getId());
        return null;
    }

    public void changeProjectModel(Project project, ModelDiff projectModelDiff) {
        // TODO
    }
}
