/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.md.report.MetricGroup.METRIC_GROUP;
import static org.hamcrest.MatcherAssert.assertThat;

public class GridElementSerializerTest {

    @Test
    public void testSerializer() throws Exception {
        final GridElements elems = new GridElements();
        elems.add(new AttributeInGrid("/uriValue", "aliasValue"));
        elems.add(METRIC_GROUP);
        assertThat(elems, serializesToJson("/md/report/gridElements.json"));
    }

    @JsonSerialize(contentUsing = GridElementSerializer.class)
    private static class GridElements extends ArrayList<GridElement> {}

}