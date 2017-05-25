/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.springframework.web.util.UriTemplate;

/**
 * First class metadata object - only dto objects, which have URI pointing to themselves should implement this.
 */
public interface Obj {

    String URI = "/gdc/md/{projectId}/obj";
    String CREATE_URI = URI + "?createAndGet=true";
    String OBJ_URI = URI + "/{objId}";
    UriTemplate OBJ_TEMPLATE = new UriTemplate(OBJ_URI);

    String getUri();
}
