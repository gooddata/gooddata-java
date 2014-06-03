/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import static org.junit.Assert.assertEquals;
import com.gooddata.model.MaqlDdlTaskStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class MaqlDdlTaskStatusTest {

    @Test
    public void testDeser() throws Exception {
        final MaqlDdlTaskStatus maqlDdlTaskStatus = new ObjectMapper().readValue("{\"wTaskStatus\":{\"status\":\"OK\",\"poll\":\"someURI\"}}", MaqlDdlTaskStatus.class);
        assertEquals("OK", maqlDdlTaskStatus.getStatus());
        assertEquals("someURI", maqlDdlTaskStatus.getUri());


    }
}
