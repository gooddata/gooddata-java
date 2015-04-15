/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.collections.PageableList;
import com.gooddata.collections.Paging;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.web.util.UriTemplate;

import java.util.List;

/**
 * List of users. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = Id.NONE)
@JsonTypeName("users")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = UsersDeserializer.class)
class Users extends PageableList<User> {
    public static final String URI = "/gdc/projects/{projectId}/users";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    Users(final List<User> items, final Paging paging) {
        super(items, paging);
    }
}
