/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WarehouseS3CredentialsListTest {

    private final Map<String, String> credentials1Links = new HashMap<String, String>() {{
        put("self", "/gdc/datawarehouse/instances/{instance-id}/s3/region/accessKey");
        put("parent", "/gdc/datawarehouse/instances/{instance-id}/s3");
        put("instance", "/gdc/datawarehouse/instances/{instance-id}");
    }};
    private final WarehouseS3Credentials credentials1 = new WarehouseS3Credentials("region", "accessKey",
             "secretKey", "/gdc/datawarehouse/instances/{instance-id}/users/{user-id}",
            DateTime.parse("2017-08-02T09:40:24.064Z"), credentials1Links);
    private final WarehouseS3Credentials credentials2 = new WarehouseS3Credentials( "region2", "accessKey2",
            "secretKey2", null, null, null);

    private final Map<String, String> links = new HashMap<String, String>() {{
        put("self", "/gdc/datawarehouse/instances/{instance-id}/s3");
        put("parent", "/gdc/datawarehouse/instances/{instance-id}");
    }};

    private final WarehouseS3CredentialsList credentialsList = new WarehouseS3CredentialsList(
            asList(credentials1, credentials2), links);

    @Test
    public void serialize() {
        assertThat(credentialsList, jsonEquals(resource("warehouse/s3CredentialsList.json")));
    }

    @Test
    public void deserialize() {
        final WarehouseS3CredentialsList result = readObjectFromResource("/warehouse/s3CredentialsList.json",
                WarehouseS3CredentialsList.class);

        assertThat(result.size(), is(2));
        assertThat(result.getLinks(), is(links));
    }
}
