/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project.model;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.project.model.ModelDiff;
import org.testng.annotations.Test;

import java.io.InputStreamReader;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;

/**
 * Project model acceptance test
 */
public class ModelServiceAT extends AbstractGoodDataAT {

    @Test(groups = "model", dependsOnGroups = "project")
    public void createModel() throws Exception {
        final ModelService modelService = gd.getModelService();

        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(project,
                new InputStreamReader(readFromResource("/person.json"))).get();
        modelService.updateProjectModel(project, projectModelDiff).get();
    }


}
