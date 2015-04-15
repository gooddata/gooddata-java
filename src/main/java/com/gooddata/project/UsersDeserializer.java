package com.gooddata.project;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;
import com.gooddata.dataload.processes.Schedule;

import java.util.List;

class UsersDeserializer extends PageableListDeserializer<Users, User> {

    protected UsersDeserializer() {
        super(User.class, "users");
    }

    @Override
    protected Users createList(final List<User> items, final Paging paging) {
        return new Users(items, paging);
    }
}
