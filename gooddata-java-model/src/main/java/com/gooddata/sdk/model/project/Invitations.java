/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

import static java.util.Arrays.asList;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Invitations {

    /**
     * @see Project#getInvitationsUri()
     */
    public static final String URI = Project.URI + "/invitations";

    private final List<Invitation> invitations;

    public Invitations(Invitation... invitations) {
        this.invitations = asList(invitations);
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
