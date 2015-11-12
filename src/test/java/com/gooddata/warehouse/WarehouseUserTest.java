package com.gooddata.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class WarehouseUserTest {

    public static final String ROLE = "admin";
    public static final String PROFILE = "/gdc/account/profile/{profile-id}";
    public static final String LOGIN = "foo@bar.com";
    public static final String SELF_LINK = "/gdc/datawarehouse/instances/{instance-id}/users/{profile-id}";
    public static final Map<String, String> LINKS = new LinkedHashMap<String, String>() {{
        put("self", SELF_LINK);
        put("parent", "/gdc/datawarehouse/instances/{instance-id}/users");
    }};

    @Test
    public void testSerializationWithProfile() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, PROFILE, null);
        assertThat(user, serializesToJson("/warehouse/user-createWithProfile.json"));
    }

    @Test
    public void testSerializationWithLogin() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, null, LOGIN);
        assertThat(user, serializesToJson("/warehouse/user-createWithLogin.json"));
    }

    @Test
    public void testCompleteSerialization() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, PROFILE, LOGIN, LINKS);
        assertThat(user, serializesToJson("/warehouse/user.json"));
    }

    @Test
    public void testDeserialization() throws Exception {
        final WarehouseUser user = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/warehouse/user.json"), WarehouseUser.class);

        assertThat(user.getRole(), is(ROLE));
        assertThat(user.getProfile(), is(PROFILE));
        assertThat(user.getLogin(), is(LOGIN));
        assertThat(user.getUri(), is(SELF_LINK));
        assertThat(user.getLinks(), is(LINKS));
    }
}