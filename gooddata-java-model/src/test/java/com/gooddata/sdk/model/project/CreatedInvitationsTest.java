/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class CreatedInvitationsTest {

    @Test
    public void shouldDeserializeUrisOnly() throws Exception {
        final CreatedInvitations created = readObjectFromResource("/project/created-invitations-uris-only.json", CreatedInvitations.class);
        assertThat(created, is(notNullValue()));
        assertThat(created.getInvitationUris(), contains("uri1", "uri2"));
        assertThat(created.getDomainMismatchEmails(), empty());
        assertThat(created.getAlreadyInProjectEmails(), empty());
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final CreatedInvitations created = readObjectFromResource("/project/created-invitations.json", CreatedInvitations.class);
        assertThat(created, is(notNullValue()));
        assertThat(created.getInvitationUris(), contains("uri1", "uri2"));
        assertThat(created.getDomainMismatchEmails(), contains("mis1", "mis2"));
        assertThat(created.getAlreadyInProjectEmails(), contains("alr1", "alr2"));

    }
}