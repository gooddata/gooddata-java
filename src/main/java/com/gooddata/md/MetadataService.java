/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.UriResponse;
import com.gooddata.project.Project;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 */
public class MetadataService extends AbstractService {

    public MetadataService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public <T extends Obj> T getObjByUri(String uri, Class<T> cls) {
        try {
            return restTemplate.getForObject(uri, cls);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ObjNotFoundException(uri, cls, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get " + cls.getSimpleName().toLowerCase() + " " + uri, e);
        }
    }

    public <T extends Obj> T getObjById(Project project, String id, Class<T> cls) {
        return getObjByUri(Obj.OBJ_TEMPLATE.expand(project.getId(), id).toString(), cls);
    }

    @SuppressWarnings("unchecked")
    public <T extends Obj> T createObj(Project project, T obj) {
        final UriResponse response = restTemplate.postForObject(Obj.URI, obj, UriResponse.class, project.getId());
        return getObjByUri(response.getUri(), (Class<T>) obj.getClass());
    }
}
