package com.gooddata.model;


import static org.junit.Assert.assertEquals;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ModelManageTest {

    @Test
    public void testSer() throws Exception {
        assertEquals("{\"manage\":{\"maql\":\"maqlddl\"}}", new ObjectMapper().writeValueAsString(new ModelManage("maqlddl")));

    }
}
