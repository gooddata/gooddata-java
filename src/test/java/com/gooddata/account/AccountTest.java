package com.gooddata.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountTest {

    private static final String MAIL = "fake@gooddata.com";
    private static final String FIRST_NAME = "Blah";
    private static final String LAST_NAME = "Muhehe";

    @Test
    public void testDeserialize() throws Exception {
        final Account account = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/account/account.json"), Account.class);
        assertThat(account, is(notNullValue()));

        assertThat(account.getFirstName(), is(FIRST_NAME));
        assertThat(account.getLastName(), is(LAST_NAME));
        assertThat(account.getId(), is("ID"));
        assertThat(account.getUri(), is("/gdc/account/profile/ID"));
        assertThat(account.getProjectsLink(), is("/gdc/account/profile/ID/projects"));
    }

    @Test
    public void testSerialization() {
        final Account account = new Account(FIRST_NAME, LAST_NAME, null);
        assertThat(account, serializesToJson("/account/account-input.json"));
    }

    @Test
    public void testSerializationOfCreateAccount() {
        final Account account = new Account(MAIL, "password", FIRST_NAME, LAST_NAME);
        assertThat(account, serializesToJson("/account/create-account.json"));
    }

}
