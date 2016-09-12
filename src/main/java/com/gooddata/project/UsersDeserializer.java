/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;
import com.gooddata.dataload.processes.Schedule;

import java.util.List;
import java.util.Map;

class UsersDeserializer extends PageableListDeserializer<Users, User> {

    protected UsersDeserializer() {
        super(User.class, "users");
    }

    @Override
    protected Users createList(final List<User> items, final Paging paging, final Map<String, String> links) {
        return new Users(items, paging);
    }
}
