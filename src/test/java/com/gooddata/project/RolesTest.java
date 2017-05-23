/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class RolesTest {

    @Test
    public void testDeserialization() throws Exception {
        final Roles roles = readObjectFromResource("/project/project-roles.json", Roles.class);

        assertThat(roles, notNullValue());
        assertThat(roles.getRoles(), hasSize(2));
        assertThat(roles.getRoles(), hasItem("/gdc/projects/PROJECT_ID/roles/ROLE1"));
        assertThat(roles.getRoles(), hasItem("/gdc/projects/PROJECT_ID/roles/ROLE2"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Roles roles = readObjectFromResource("/project/project-roles.json", Roles.class);

        assertThat(roles.toString(), matchesPattern(Roles.class.getSimpleName() + "\\[.*\\]"));
    }
}