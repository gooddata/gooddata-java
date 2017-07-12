/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import static java.util.Arrays.asList;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class Invitations {

    /**
     * @see Project#getInvitationsUri()
     */
    static final String URI = Project.URI + "/invitations";
    static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private final List<Invitation> invitations;

    Invitations(Invitation... invitations) {
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
