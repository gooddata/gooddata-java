/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

class UsersDeserializer extends PageDeserializer<Users, User> {

    protected UsersDeserializer() {
        super(User.class, "users");
    }

    @Override
    protected Users createPage(final List<User> items, final Paging paging, final Map<String, String> links) {
        return new Users(items, paging);
    }
}
