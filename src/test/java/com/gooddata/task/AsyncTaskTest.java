package com.gooddata.task;


import static org.junit.Assert.assertEquals;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class AsyncTaskTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeser() throws Exception {

        final AsyncTask asyncTask = mapper.readValue("{\n"
                + "    \"asyncTask\": {\n"
                + "        \"link\": {\n"
                + "            \"poll\": \"/gdc/projects/{project-id}/model/diff/{diff-id}\"\n"
                + "        }\n"
                + "    }\n"
                + "}", AsyncTask.class);
        assertEquals("/gdc/projects/{project-id}/model/diff/{diff-id}", asyncTask.getUri());


    }
}
