/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.model.DiffRequest;
import com.gooddata.model.ModelDiff;
import com.gooddata.model.ModelService;
import com.gooddata.project.Project;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;

/**
 * TODO
 */
public class ModelExample {

    public static void main(String... args) {
        final GoodData gd = new GoodData("staging.getgooddata.com", "jiri.mikulasek@gooddata.com", "jindrisska");

        final ModelService modelService = gd.getModelService();
        final Project project = gd.getProjectService().getProjectById("amxhoeyj7oskijld63tajq0o9f4nhxy7");
        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(project, new DiffRequest(projectModelData));

        modelService.updateProjectModel(project, projectModelDiff);


        gd.logout();
    }

    public static final String projectModelData = "{\"projectModel\": {\"datasets\": [\n"
            + "{\n"
            + "    \"dataset\": {\n"
            + "        \"identifier\": \"dataset.person\",\n"
            + "        \"title\": \"Person\",\n"
            + "        \"description\": \"Dataset with Person-related data\",\n"
            + "        \"anchor\": {\n"
            + "            \"attribute\": {\n"
            + "                \"identifier\": \"attr.person.id\",\n"
            + "                \"title\": \"Person ID\",\n"
            + "                \"labels\": [\n"
            + "                    {\n"
            + "                        \"label\": {\n"
            + "                            \"identifier\": \"attr.person.id.name\",\n"
            + "                            \"title\": \"Person Name\"\n"
            + "                        }\n"
            + "                    }\n"
            + "                ]\n"
            + "            }\n"
            + "        },\n"
            + "        \"attributes\": [\n"
            + "            {\n"
            + "                \"attribute\": {\n"
            + "                    \"identifier\": \"attr.person.department\",\n"
            + "                    \"title\": \"Department\",\n"
            + "                    \"labels\": [\n"
            + "                        {\n"
            + "                            \"label\": {\n"
            + "                                \"identifier\": \"attr.person.xdepartment\",\n"
            + "                                \"title\": \"Department\"\n"
            + "                            }\n"
            + "                        }\n"
            + "                    ]\n"
            + "                }\n"
            + "            },\n"
            + "            {\n"
            + "                \"attribute\": {\n"
            + "                    \"identifier\": \"attr.person.role\",\n"
            + "                    \"title\": \"Person Role\",\n"
            + "                    \"labels\": [\n"
            + "                        {\n"
            + "                            \"label\": {\n"
            + "                                \"identifier\": \"attr.person.xrole\",\n"
            + "                                \"title\": \"Person Role\"\n"
            + "                            }\n"
            + "                        }\n"
            + "                    ]\n"
            + "                }\n"
            + "            }\n"
            + "        ],\n"
            + "        \"facts\": [\n"
            + "            {\n"
            + "                \"fact\": {\n"
            + "                    \"identifier\": \"fact.person.age\",\n"
            + "                    \"title\": \"Person Age\"\n"
            + "                }\n"
            + "            },\n"
            + "            {\n"
            + "                \"fact\": {\n"
            + "                    \"identifier\": \"fact.person.shoesize\",\n"
            + "                    \"title\": \"Person Shoe Size\"\n"
            + "                }\n"
            + "            }\n"
            + "        ]\n"
            + "    }\n"
            + "}"
            + "]}}";
}
