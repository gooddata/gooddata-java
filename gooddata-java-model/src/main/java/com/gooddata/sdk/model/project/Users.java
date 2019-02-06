/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.collections.PageableList;
import com.gooddata.collections.Paging;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.web.util.UriTemplate;

import java.util.List;

/**
 * List of users. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = Id.NONE)
@JsonTypeName("users")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = UsersDeserializer.class)
@JsonSerialize(using = UsersSerializer.class)
public class Users extends PageableList<User> {
    public static final String URI = "/gdc/projects/{projectId}/users";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    static final String ROOT_NODE = "users";

    Users(final List<User> items, final Paging paging) {
        super(items, paging);
    }

    public Users(final User... users) {
        this(asList(users), null);
    }
}
