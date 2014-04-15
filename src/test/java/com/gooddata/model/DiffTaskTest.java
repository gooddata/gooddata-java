/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;


import static org.junit.Assert.assertEquals;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class DiffTaskTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeser() throws Exception {

        final DiffTask diffTask = mapper.readValue("{\n"
                + "    \"asyncTask\": {\n"
                + "        \"link\": {\n"
                + "            \"poll\": \"/gdc/projects/{project-id}/model/diff/{diff-id}\"\n"
                + "        }\n"
                + "    }\n"
                + "}", DiffTask.class);
        assertEquals("/gdc/projects/{project-id}/model/diff/{diff-id}", diffTask.getUri());


    }
}
