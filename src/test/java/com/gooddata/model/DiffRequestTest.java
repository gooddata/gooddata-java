package com.gooddata.model;

import static org.junit.Assert.assertEquals;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class DiffRequestTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSer() throws Exception {
        String valueAsString = mapper.writeValueAsString(new DiffRequest("{\"projectModel\":\"xxx\"}"));
        assertEquals("{\"diffRequest\":{\"targetModel\":{\"projectModel\":\"xxx\"}}}", valueAsString);

    }
}
