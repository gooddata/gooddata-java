package com.gooddata.md;

import org.springframework.web.util.UriTemplate;

/**
 * Metadata object
 */
public interface Obj {

    String URI = "/gdc/md/{projectId}/obj";
    String OBJ_URI = URI + "/{objId}";
    UriTemplate OBJ_TEMPLATE = new UriTemplate(OBJ_URI);

    String getUri();
}
