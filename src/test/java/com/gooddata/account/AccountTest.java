package com.gooddata.account;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountTest {

    @Test
    public void testDeserialize() throws Exception {
        final Account account = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/account/account.json"), Account.class);
        assertThat(account, is(notNullValue()));

        assertThat(account.getFirstName(), is("Blah"));
        assertThat(account.getLastName(), is("Muhehe"));
        assertThat(account.getId(), is("ID"));
        assertThat(account.getSelfLink(), is("/gdc/account/profile/ID"));
        assertThat(account.getProjectsLink(), is("/gdc/account/profile/ID/projects"));
    }

    @Test
    public void testSerialization() {
        final Account account = new Account("Blah", "Muhehe");
        assertThat(account, serializesToJson("/account/account-input.json"));
    }

}