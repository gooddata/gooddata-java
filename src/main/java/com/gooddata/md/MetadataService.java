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

    /**
     * Create metadata object in given project
     *
     * @param project project
     * @param obj     metadata object to be created
     * @param <T>     type of the object to be created
     * @return new metadata object
     * @throws com.gooddata.md.ObjCreateException   if creation failed
     * @throws com.gooddata.md.ObjNotFoundException if new metadata object not found after creation
     * @throws com.gooddata.GoodDataRestException   if GoodData REST API returns unexpected status code when getting
     *                                              the new object
     * @throws com.gooddata.GoodDataException       if it encounters client-side HTTP errors when getting the new object
     */
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

        if (response == null) {
            throw new ObjCreateException("empty response from API call", obj);
        }
        return getObjByUri(response.getUri(), (Class<T>) obj.getClass());
    }

    /**
     * Get metadata object by URI (format is <code>/gdc/md/{PROJECT_ID}/obj/{OBJECT_ID}</code>)
     *
     * @param uri URI in format <code>/gdc/md/{PROJECT_ID}/obj/{OBJECT_ID}</code>
     * @param cls class of the resulting object
     * @param <T> type of the object to be returned
     * @return the metadata object
     * @throws com.gooddata.md.ObjNotFoundException if metadata object not found
     * @throws com.gooddata.GoodDataRestException   if GoodData REST API returns unexpected status code
     * @throws com.gooddata.GoodDataException       if it encounters client-side HTTP errors
     */
    public <T extends Obj> T getObjByUri(String uri, Class<T> cls) {
        notNull(uri, "uri");
        notNull(cls, "cls");
        try {
            final T result = restTemplate.getForObject(uri, cls);

            if (result != null) {
                return result;
            } else {
                throw new GoodDataException("empty response from API call");
            }
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

    /**
     * Get metadata object by id.
     *
     * @param project project where to search for the object
     * @param id      id of the object
     * @param cls     class of the resulting object
     * @param <T>     type of the object to be returned
     * @return the metadata object
     * @throws com.gooddata.md.ObjNotFoundException if metadata object not found
     * @throws com.gooddata.GoodDataRestException   if GoodData REST API returns unexpected status code
     * @throws com.gooddata.GoodDataException       if it encounters client-side HTTP errors
     */
    public <T extends Obj> T getObjById(Project project, String id, Class<T> cls) {
        notNull(project, "project");
        notNull(id, "id");
        notNull(cls, "cls");
        return getObjByUri(Obj.OBJ_TEMPLATE.expand(project.getId(), id).toString(), cls);
    }

    /**
     * Get metadata object URI by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the object
     * @param cls          class of the resulting object
     * @param restrictions query restrictions
     * @param <T>          type of the object to be returned
     * @return the URI of metadata object
     * @throws com.gooddata.md.ObjNotFoundException  if metadata object not found
     * @throws com.gooddata.md.NonUniqueObjException if more than one object corresponds to search restrictions
     */
    public <T extends Queryable> String getObjUri(Project project, Class<T> cls, Restriction... restrictions) {
        final Collection<String> results = findUris(project, cls, restrictions);
        if (results == null || results.isEmpty()) {
            throw new ObjNotFoundException(cls);
        } else if (results.size() != 1) {
            throw new NonUniqueObjException(cls, results);
        }
        return results.iterator().next();
    }

    /**
     * Find metadata by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the metadata
     * @param cls          class of searched metadata
     * @param restrictions query restrictions
     * @param <T>          type of the metadata referenced in returned entries
     * @return the collection of metadata entries
     * @throws com.gooddata.GoodDataException if unable to query metadata
     */
    public <T extends Queryable> Collection<Entry> find(Project project, Class<T> cls, Restriction... restrictions) {
        notNull(project, "project");
        notNull(cls, "cls");

        final String type = cls.getSimpleName().toLowerCase() +
                (cls.isAssignableFrom(ReportDefinition.class) ? "" : "s");
        try {
            final Query queryResult = restTemplate.getForObject(Query.URI, Query.class, project.getId(), type);

            if (queryResult != null && queryResult.getEntries() != null) {
                return filterEntries(queryResult.getEntries(), restrictions);
            } else {
                throw new GoodDataException("empty response from API call");
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to query metadata: " + type, e);
        }
    }

    private Collection<Entry> filterEntries(Collection<Entry> entries, Restriction... restrictions) {
        if (restrictions == null || restrictions.length == 0) {
            return entries;
        }
        final Collection<Entry> result = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            for (Restriction restriction : restrictions) {
                switch (restriction.getType()) {
                    case IDENTIFIER:
                        if (restriction.getValue().equals(entry.getIdentifier())) result.add(entry);
                        break;
                    case TITLE:
                        if (restriction.getValue().equals(entry.getTitle())) result.add(entry);
                        break;
                    case SUMMARY:
                        if (restriction.getValue().equals(entry.getSummary())) result.add(entry);
                        break;
                }
            }
        }
        return result;
    }

    /**
     * Find metadata URIs by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the metadata
     * @param cls          class of searched metadata
     * @param restrictions query restrictions
     * @param <T>          type of the metadata referenced by returned URIs
     * @return the collection of metadata URIs
     * @throws com.gooddata.GoodDataException if unable to query metadata
     */
    public <T extends Queryable> Collection<String> findUris(Project project,
                                                             Class<T> cls,
                                                             Restriction... restrictions) {
        final Collection<Entry> entries = find(project, cls, restrictions);
        final Collection<String> result = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            result.add(entry.getLink());
        }
        return result;
    }

}
