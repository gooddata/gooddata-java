/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;

import static com.gooddata.Validate.notNull;

/**
 * Query, create and update project metadata - attributes, facts, metrics, reports,...
 */
public class MetadataService extends AbstractService {

    public MetadataService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @SuppressWarnings("unchecked")
    public <T extends Obj> T createObj(Project project, T obj) {
        notNull(project, "project");
        notNull(obj, "obj");
        final UriResponse response;
        try {
            response = restTemplate.postForObject(Obj.URI, obj, UriResponse.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ObjCreateException(obj, e);
        }
        return getObjByUri(response.getUri(), (Class<T>) obj.getClass());
    }

    public <T extends Obj> T getObjByUri(String uri, Class<T> cls) {
        notNull(uri, "uri");
        notNull(cls, "cls");
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
        notNull(project, "project");
        notNull(id, "id");
        notNull(cls, "cls");
        return getObjByUri(Obj.OBJ_TEMPLATE.expand(project.getId(), id).toString(), cls);
    }

    public <T extends Queryable> String getObjUri(Project project, Class<T> cls, Restriction... restrictions) {
        final Collection<String> results = findUris(project, cls, restrictions);
        if (results == null || results.isEmpty()) {
            throw new ObjNotFoundException(cls);
        } else if (results.size() != 1) {
            throw new NonUniqueObjException(cls, results);
        }
        return results.iterator().next();
    }

    public <T extends Queryable> Collection<Entry> find(Project project, Class<T> cls, Restriction... restrictions) {
        notNull(project, "project");
        notNull(cls, "cls");
        final String type = cls.getSimpleName().toLowerCase() + (cls.isAssignableFrom(ReportDefinition.class) ? "" : "s");
        try {
            final Collection<Entry> entries = restTemplate.getForObject(Query.URI, Query.class, project.getId(), type).getEntries();
            return filterEntries(entries, restrictions);
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to query metadata: " + type, e);
        }
    }

    private Collection<Entry> filterEntries(Collection<Entry> entries, Restriction... restrictions) {
        if (restrictions == null || restrictions.length == 0) {
            return entries;
        }
        final Collection<Entry> result = new ArrayList<>(entries.size());
        for (Entry entry: entries) {
            for (Restriction restriction: restrictions) {
                switch (restriction.getType()) {
                    case IDENTIFIER: if (restriction.getValue().equals(entry.getIdentifier())) result.add(entry); break;
                    case TITLE: if (restriction.getValue().equals(entry.getTitle())) result.add(entry); break;
                    case SUMMARY: if (restriction.getValue().equals(entry.getSummary())) result.add(entry); break;
                }
            }
        }
        return result;
    }

    public <T extends Queryable> Collection<String> findUris(Project project, Class<T> cls, Restriction... restrictions) {
        final Collection<Entry> entries = find(project, cls, restrictions);
        final Collection<String> result = new ArrayList<>(entries.size());
        for (Entry entry: entries) {
            result.add(entry.getLink());
        }
        return result;
    }

}
