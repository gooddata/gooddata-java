package com.gooddata.dataset;

import org.junit.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;

public class PullTest {

    @Test
    public void testSerialization() throws Exception {
        final Pull pull = new Pull("DIR");
        assertThat(pull, serializesToJson("/dataset/pull.json"));
    }
}
