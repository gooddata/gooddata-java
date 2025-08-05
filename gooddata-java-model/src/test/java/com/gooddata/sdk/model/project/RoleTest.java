/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class RoleTest {

    @Test
    public void testDeserialization() throws Exception {
        final Role role = readRole("");

        assertThat(role, notNullValue());
        assertThat(role.getPermissions(), notNullValue());
        assertThat(role.getPermissions(), hasSize(71));
        assertThat(role.getGrantedPermissions(), not(hasItem("canManageProjectDashboard")));
        assertThat(role.hasPermissionGranted("canManageProjectDashboard"), is(false));
        assertThat(role.getGrantedPermissions(), hasItem("canCreateReportResult2"));
        assertThat(role.hasPermissionGranted("canCreateReportResult2"), is(true));
        assertThat(role.getTitle(), is("Embedded Dashboard Only"));
    }

    @Test
    public void testEquals() throws IOException {
        final Role role1 = readRole("");

        assertThat(role1.equals(readRole("")), is(true));
        assertThat(role1.equals(readRole("2")), is(false));
        assertThat(role1.equals(readRole("3")), is(false));
    }

    private Role readRole(final String suffix) throws java.io.IOException {
        return readObjectFromResource("/project/project-role" + suffix + ".json", Role.class);
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(Role.class)
                .usingGetClass()
                .withNonnullFields("permissions", "meta", "links")
                .withIgnoredFields("links")
                .verify();
    }
}