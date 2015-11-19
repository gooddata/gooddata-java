package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class UsersTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/project/project-users.json");
        final Users users = new ObjectMapper().readValue(stream, Users.class);

        assertThat(users, notNullValue());
        assertThat(users, hasSize(1));
        assertThat(users.getNextPage(), nullValue());

        final User user = users.get(0);
        assertThat(user, notNullValue());
        assertThat(user.getEmail(), is("ateam+ads-testing@gooddata.com"));
        assertThat(user.getFirstName(), is("ateam"));
        assertThat(user.getUserRoles(), hasItem("/gdc/projects/PROJECT_ID/roles/ROLE1"));
        assertThat(user.getPhoneNumber(), is("123456789"));
        assertThat(user.getStatus(), is("ENABLED"));
        assertThat(user.getLastName(), is("ads-testing"));
        assertThat(user.getLogin(), is("ateam+ads-testing@gooddata.com"));
    }
}