/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.md.Meta;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.util.Validate.notEmpty;

/**
 * Project invitation
 */
@JsonTypeName("invitation")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Invitation {

    private final InvitationContent content;

    private final Meta meta;

    private Invitation(final InvitationContent content, final Meta meta) {
        this.meta = meta;
        this.content = content;
    }

    public Invitation(final String email) {
        this(new InvitationContent(email), null);
    }

    @JsonProperty // because getter is private
    private InvitationContent getContent() {
        return content;
    }

    public Meta getMeta() {
        return meta;
    }

    @JsonIgnore
    public String getEmail() {
        return content != null ? content.getEmail() : null;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class InvitationContent {

        private final String email;

        private InvitationContent(final String email) {
            this.email = notEmpty(email, "email");
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }

    }
}