/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;

public class InvitationsTest {

    @Test
    public void shouldSerialize() throws Exception {
        final Invitations invitations = new Invitations(new Invitation("roman@gooddata.com"));
        assertThat(invitations, jsonEquals(resource("project/invitations.json")));
    }
}