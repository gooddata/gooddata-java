/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class WarehouseS3CredentialsTest {

    private static final String REGION = "region";
    private static final String ACCESS_KEY = "accessKey";
    private static final String UPDATED_BY = "/gdc/datawarehouse/instances/{instance-id}/users/{user-id}";
    private static final String SECRET_KEY = "secretKey";
    private static final DateTime UPDATED_AT = DateTime.parse("2017-08-02T09:40:24.064Z");
    private static final String SELF_LINK = "/gdc/datawarehouse/instances/{instance-id}/s3/region/accessKey";
    private static final String PARENT_LINK = "/gdc/datawarehouse/instances/{instance-id}/s3";
    private static final String INSTANCE_LINK = "/gdc/datawarehouse/instances/{instance-id}";
    private static final WarehouseS3Credentials.Links LINKS = new WarehouseS3Credentials.Links(
            SELF_LINK, PARENT_LINK, INSTANCE_LINK, UPDATED_BY
    );

    @Test
    public void serializeFull() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY, SECRET_KEY,
                UPDATED_AT, LINKS);
        assertThat(credentials, jsonEquals(resource("warehouse/s3Credentials-full.json")));
    }

    @Test
    public void serializeGet() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY, null,
                UPDATED_AT, null);
        assertThat(credentials, jsonEquals(resource("warehouse/s3Credentials-get.json")));
    }

    @Test
    public void serializeCreate() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY, SECRET_KEY);
        assertThat(credentials, jsonEquals(resource("warehouse/s3Credentials-create.json")));
    }

    @Test
    public void deserializeFull() {
        final WarehouseS3Credentials credentials = readObjectFromResource("/warehouse/s3Credentials-full.json",
                WarehouseS3Credentials.class);

        assertThat(credentials.getRegion(), is(REGION));
        assertThat(credentials.getAccessKey(), is(ACCESS_KEY));
        assertThat(credentials.getSecretKey(), is(SECRET_KEY));
        assertThat(credentials.getUpdated().toString(), is(UPDATED_AT.toString()));
        assertThat(credentials.getLinks(), is(LINKS));
    }

    @Test
    public void deserializeGet() {
        final WarehouseS3Credentials credentials = readObjectFromResource("/warehouse/s3Credentials-get.json",
                WarehouseS3Credentials.class);

        assertThat(credentials.getRegion(), is(REGION));
        assertThat(credentials.getAccessKey(), is(ACCESS_KEY));
        assertThat(credentials.getSecretKey(), is(nullValue()));
        assertThat(credentials.getUpdated().toString(), is(UPDATED_AT.toString()));
        assertThat(credentials.getLinks(), is(nullValue()));
    }

    @Test
    public void deserializeCreate() {
        final WarehouseS3Credentials credentials = readObjectFromResource("/warehouse/s3Credentials-create.json",
                WarehouseS3Credentials.class);

        assertThat(credentials.getRegion(), is(REGION));
        assertThat(credentials.getAccessKey(), is(ACCESS_KEY));
        assertThat(credentials.getSecretKey(), is(SECRET_KEY));
        assertThat(credentials.getUpdated(), is(nullValue()));
        assertThat(credentials.getLinks(), is(nullValue()));
    }

    @Test
    public void withSecretKey() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY, null,
                UPDATED_AT, null);
        assertThat(credentials.getSecretKey(), is(nullValue()));

        credentials.setSecretKey(SECRET_KEY);
        assertThat(credentials.getSecretKey(), is(SECRET_KEY));
    }

    @Test
    public void withLinks() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY,
                SECRET_KEY, UPDATED_AT, LINKS);
        assertThat(credentials.getLinks(), is(LINKS));
    }

    @Test
    public void withLinksForWarehouse() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY,
                "updaterId", UPDATED_AT, LINKS);
        assertThat(credentials.getInstanceUri(), endsWith("/{instance-id}"));
        assertThat(credentials.getListUri(), endsWith("/{instance-id}/s3"));
        assertThat(credentials.getUri(), endsWith("/{instance-id}/s3/region/accessKey"));
        assertThat(credentials.getUpdatedByUri(), endsWith("/users/{user-id}"));
    }

    @Test
    public void getUri() {
        final WarehouseS3Credentials credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY,
                UPDATED_BY, UPDATED_AT, LINKS);

        assertThat(credentials.getUri(), is("/gdc/datawarehouse/instances/{instance-id}/s3/region/accessKey"));
    }

    @Test
    public void testToString() {
        final WarehouseS3Credentials credentials = readObjectFromResource("/warehouse/s3Credentials-create.json",
                WarehouseS3Credentials.class);

        assertThat(credentials.toString(), is("WarehouseS3Credentials[region=region,accessKey=accessKey,updated=<null>]"));
    }
}
