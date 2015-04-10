package com.gooddata.project;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class RolesTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/project/project-roles.json");
        final Roles roles = new ObjectMapper().readValue(stream, Roles.class);

        assertThat(roles, notNullValue());
        assertThat(roles.getRoles(), hasSize(2));
        assertThat(roles.getRoles(), hasItem("/gdc/projects/PROJECT_ID/roles/ROLE1"));
        assertThat(roles.getRoles(), hasItem("/gdc/projects/PROJECT_ID/roles/ROLE2"));
    }
}