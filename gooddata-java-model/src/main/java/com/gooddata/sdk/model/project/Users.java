/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;

import static com.gooddata.sdk.model.project.Users.ROOT_NODE;
import static java.util.Arrays.asList;

/**
 * List of users.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = Id.NONE)
@JsonTypeName(ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = UsersDeserializer.class)
@JsonSerialize(using = UsersSerializer.class)
public class Users extends Page<User> {
    public static final String URI = "/gdc/projects/{projectId}/users";

    static final String ROOT_NODE = "users";

    Users(final List<User> items, final Paging paging) {
        super(items, paging);
    }

    public Users(final User... users) {
        this(asList(users), null);
    }
}
