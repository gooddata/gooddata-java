/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.testng.annotations.Test;

public class CoupaInstancesTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final CoupaInstances coupaInstances =
                readObjectFromResource("/connector/coupa_instances.json", CoupaInstances.class);

        assertThat(coupaInstances.getItems(), hasSize(2));
        assertThat(coupaInstances.getItems(), hasItems(
                new CoupaInstance("instance 1", "https://gooddata-demo01.coupacloud.com/api", null),
                new CoupaInstance("instance 2", "https://gooddata-demo02.coupacloud.com/api", null)
        ));
    }
}