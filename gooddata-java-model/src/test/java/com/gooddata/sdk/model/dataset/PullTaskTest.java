/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PullTaskTest {

    @Test
    public void testDeserialization() throws Exception {
        final PullTask task = readObjectFromResource("/dataset/pullTask.json", PullTask.class);

        assertThat(task.getPollUri(), is("/gdc/md/PROJECT/tasks/task/ID/status"));
    }
}
