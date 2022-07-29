/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project.model;

import org.testng.annotations.Test;

import java.util.Collections;

import static com.gooddata.sdk.model.project.model.ModelDiff.UpdateScript;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ModelDiffTest {

    @Test
    public void testGetUpdateMaqls() throws Exception {
        final ModelDiff diff = new ModelDiff(new UpdateScript(true, false, "maql1", "maql2"));

        assertThat(diff.getUpdateMaql(), hasSize(2));
        assertThat(diff.getUpdateMaql(), contains("maql1", "maql2"));
    }

    @Test
    public void testGetUpdateMaqlsReturnsBest() throws Exception {
        final ModelDiff diff = new ModelDiff(
                new UpdateScript(false, false, "maql1"),
                new UpdateScript(true, false, "maql2")
        );

        assertThat(diff.getUpdateMaql(), hasSize(1));
        assertThat(diff.getUpdateMaql(), contains("maql2"));
    }

    @Test
    public void testGetUpdateMaqlsReturnsNotWorst() throws Exception {
        final ModelDiff diff = new ModelDiff(
                new UpdateScript(false, true, "maql1"),
                new UpdateScript(false, false, "maql2")
        );

        assertThat(diff.getUpdateMaql(), hasSize(1));
        assertThat(diff.getUpdateMaql(), contains("maql2"));
    }

    @Test
    public void testGetUpdateMaqlsNoPreserveData() throws Exception {
        final ModelDiff diff = new ModelDiff(new UpdateScript(false, false, "maql"));

        assertThat(diff.getUpdateMaql(), hasSize(1));
        assertThat(diff.getUpdateMaql().get(0), equalTo("maql"));
    }

    @Test
    public void testGetUpdateMaqlsNoUpdateScript() throws Exception {
        final ModelDiff diff = new ModelDiff(Collections.emptyList());

        assertThat(diff.getUpdateMaql(), hasSize(0));
    }

    @Test
    public void testGetUpdateMaqlsNoMaqlInUpdateScript() throws Exception {
        final ModelDiff diff = new ModelDiff(new UpdateScript(true, false, Collections.emptyList()));

        assertThat(diff.getUpdateMaql(), hasSize(0));
    }

    @Test
    public void testDeserialization() throws Exception {
        final ModelDiff diff = readObjectFromResource("/model/modelDiff.json", ModelDiff.class);

        assertThat(diff, is(notNullValue()));
        assertThat(diff.getUpdateScripts(), hasSize(2));
        assertThat(diff.getUpdateScripts().get(0).isPreserveData(), is(true));
        assertThat(diff.getUpdateScripts().get(0).isCascadeDrops(), is(false));
        assertThat(diff.getUpdateScripts().get(0).getMaqlChunks(), hasSize(1));
        assertThat(diff.getUpdateScripts().get(0).getMaqlChunks(), contains(
                "CREATE FOLDER {ffld.employee} VISUAL(TITLE \"Employee\") TYPE FACT;\nCREATE FACT {fact.employee.age} VISUAL(TITLE \"Employee Age\", FOLDER {ffld.employee}) AS {f_employee.f_age};\nALTER DATASET {dataset.employee} ADD {fact.employee.age};\nSYNCHRONIZE {dataset.employee} PRESERVE DATA;"));
    }

    @Test
    public void testToStringFormat() {
        final ModelDiff diff = new ModelDiff(new UpdateScript(true, false, Collections.emptyList()));

        assertThat(diff.toString(), matchesPattern(ModelDiff.class.getSimpleName() + "\\[.*\\]"));
    }
}