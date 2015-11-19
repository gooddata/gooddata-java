package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

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
        final InputStream stream = getClass().getResourceAsStream("/project/project-role" + suffix + ".json");
        return new ObjectMapper().readValue(stream, Role.class);
    }
}