/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.model;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.Test;

import java.io.InputStreamReader;

/**
 * Project model acceptance test
 */
public class ModelServiceAT extends AbstractGoodDataAT {

    @Test(groups = "model", dependsOnGroups = "project")
    public void createModel() throws Exception {
        final ModelService modelService = gd.getModelService();

        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(project,
                new InputStreamReader(getClass().getResourceAsStream("/person.json"))).get();
        modelService.updateProjectModel(project, projectModelDiff).get();
    }


}
