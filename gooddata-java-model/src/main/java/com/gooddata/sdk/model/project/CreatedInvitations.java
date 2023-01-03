/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Created invitations
 */
@JsonTypeName("createdInvitations")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatedInvitations {

    private final List<String> invitationUris;
    private final List<String> domainMismatchEmails;
    private final List<String> alreadyInProjectEmails;

    @JsonCreator
    private CreatedInvitations(@JsonProperty("uri") List<String> invitationUris,
                               @JsonProperty("loginsDomainMismatch") List<String> domainMismatchEmails,
                               @JsonProperty("loginsAlreadyInProject") List<String> alreadyInProjectEmails) {
        this.invitationUris = invitationUris != null ? invitationUris : Collections.emptyList();
        this.domainMismatchEmails = domainMismatchEmails != null ? domainMismatchEmails : Collections.emptyList();
        this.alreadyInProjectEmails = alreadyInProjectEmails != null ? alreadyInProjectEmails : Collections.emptyList();
    }

    /**
     * List of successful invitations
     * @return list of URIs (never null)
     */
    public List<String> getInvitationUris() {
        return invitationUris;
    }

    /**
     * List of emails which can't be invited to the project because their users belongs to a different domain
     * @return list of emails (never null)
     */
    public List<String> getDomainMismatchEmails() {
        return domainMismatchEmails;
    }

    /**
     * List of emails which can't be invited to the project because they are already members of the project
     * @return list of emails (never null)
     */
    public List<String> getAlreadyInProjectEmails() {
        return alreadyInProjectEmails;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
