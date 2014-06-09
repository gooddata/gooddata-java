/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataRestException;
import com.gooddata.project.Project;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.Reader;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;

/**
 * Service for manipulating with project model
 */
public class ModelService extends AbstractService {

    public ModelService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ModelDiff getProjectModelDiff(Project project, DiffRequest diffRequest) {
        notNull(project, "project");
        notNull(diffRequest, "diffRequest");
        try {
            final DiffTask diffTask = restTemplate.postForObject(DiffRequest.URI, diffRequest, DiffTask.class, project.getId());
            return poll(diffTask.getUri(), new StatusOkConditionCallback(), ModelDiff.class);
        } catch (GoodDataRestException | RestClientException e) {
            throw new ModelException("Unable to get project model diff", e);
        }
    }

    public ModelDiff getProjectModelDiff(Project project, String targetModel) {
        notNull(project, "project");
        notNull(targetModel, "targetModel");
        return getProjectModelDiff(project, new DiffRequest(targetModel));
    }

    public ModelDiff getProjectModelDiff(Project project, Reader targetModel) {
        notNull(project, "project");
        notNull(targetModel, "targetModel");
        try {
            return getProjectModelDiff(project, FileCopyUtils.copyToString(targetModel));
        } catch (IOException e) {
            throw new ModelException("Can't read target model", e);
        }
    }

    public void updateProjectModel(Project project, ModelDiff projectModelDiff) {
        notNull(project, "project");
        notNull(projectModelDiff, "projectModelDiff");
        for (ModelDiff.UpdateScript updateScript : projectModelDiff.getUpdateScripts()) {
            for (String maql : updateScript.getMaqlChunks()) {
                updateProjectModel(project, maql);
            }
        }
    }

    public void updateProjectModel(Project project, String maqlDdl) {
        notNull(project, "project");
        notEmpty(maqlDdl, "maqlDdl");
        try {
            final MaqlDdlLinks linkEntries = restTemplate.postForObject(MaqlDdl.URI, new MaqlDdl(maqlDdl), MaqlDdlLinks.class, project.getId());
            final MaqlDdlTaskStatus maqlDdlTaskStatus = poll(linkEntries.getStatusLink(), MaqlDdlTaskStatus.class);
            if (!maqlDdlTaskStatus.isSuccess()) {
                 throw new ModelException("Unable to update project model: " + maqlDdlTaskStatus.getMessages());
            }
        } catch (GoodDataRestException | RestClientException e) {
            throw new ModelException("Unable to update project model", e);
        }
    }



}
