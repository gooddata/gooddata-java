/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.gooddata.sdk.model.md.report.MetricGroup.METRIC_GROUP;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;

public class GridElementSerializerTest {

    @Test
    public void testSerializer() throws Exception {
        final GridElements elems = new GridElements();
        elems.add(new AttributeInGrid("/uriValue", "aliasValue"));
        elems.add(METRIC_GROUP);
        assertThat(elems, jsonEquals(resource("md/report/gridElements.json")));
    }

    @JsonSerialize(contentUsing = GridElementSerializer.class)
    private static class GridElements extends ArrayList<GridElement> {}

}