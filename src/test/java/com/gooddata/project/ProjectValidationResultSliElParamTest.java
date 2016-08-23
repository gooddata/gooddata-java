package com.gooddata.project;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ProjectValidationResultSliElParamTest {

    @Test
    public void testAsMapNullIds() throws Exception {
        final ProjectValidationResultSliElParam param = new ProjectValidationResultSliElParam(null, singletonList("2"));

        assertThat(param.asMap(), nullValue());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testAsMapNullVals() throws Exception {
        new ProjectValidationResultSliElParam(singletonList("2"), null).asMap();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testAsMapNotEqualIdsVals() throws Exception {
        new ProjectValidationResultSliElParam(asList("2", "1"), singletonList("1234")).asMap();
    }

    @Test
    public void testAsMap() throws Exception {
        final ProjectValidationResultSliElParam param =
                new ProjectValidationResultSliElParam(asList("1", "3", "2"), asList("1234", "9012", "5678"));

        final Map<String, String> expectedMap = new HashMap<>(3);
        expectedMap.put("1", "1234");
        expectedMap.put("2", "5678");
        expectedMap.put("3", "9012");

        assertThat(param.asMap(), is(expectedMap));
    }
}