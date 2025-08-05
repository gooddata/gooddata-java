/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.md.*;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.gooddata.sdk.common.util.Validate.*;
import static com.gooddata.sdk.model.md.Service.TIMEZONE_URI;
import static java.util.Arrays.asList;

/**
 * Query, create and update project metadata - attributes, facts, metrics, reports,...
 */
public class MetadataService extends AbstractService {

    public static final UriTemplate OBJ_TEMPLATE = new UriTemplate(Obj.OBJ_URI);
    private static final Set<String> IRREGULAR_PLURAL_WORD_SUFFIXES = new HashSet<>(asList("s", "ch", "sh", "x", "o"));

    public MetadataService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings);
    }

    /**
     * Create metadata object in given project
     *
     * @param project project
     * @param obj     metadata object to be created
     * @param <T>     type of the object to be created
     * @return new metadata object
     * @throws ObjCreateException   if creation failed
     * @throws ObjNotFoundException if new metadata object not found after creation
     * @throws com.gooddata.sdk.common.GoodDataRestException   if GoodData REST API returns unexpected status code when getting
     *                                              the new object
     * @throws com.gooddata.sdk.common.GoodDataException       if no response from API or client-side HTTP error when getting the new object
     */
    @SuppressWarnings("unchecked")
    public <T extends Obj> T createObj(Project project, T obj) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(obj, "obj");

        final T response;
        try {
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(Obj.CREATE_WITH_ID_URI).build(project.getId()))
                    .bodyValue(obj)
                    .retrieve()
                    .bodyToMono((Class<T>) obj.getClass())
                    .block();
        } catch (WebClientResponseException e) {
            throw new ObjCreateException(obj, e);
        } catch (Exception e) {
            throw new ObjCreateException(obj, e);
        }

        if (response == null) {
            throw new ObjCreateException("Received empty response from API call.", obj);
        }
        return response;
    }

    /**
     * Get metadata object by URI (format is <code>/gdc/md/{PROJECT_ID}/obj/{OBJECT_ID}</code>)
     *
     * @param uri URI in format <code>/gdc/md/{PROJECT_ID}/obj/{OBJECT_ID}</code>
     * @param cls class of the resulting object
     * @param <T> type of the object to be returned
     * @return the metadata object
     * @throws ObjNotFoundException if metadata object not found
     * @throws com.gooddata.sdk.common.GoodDataRestException   if GoodData REST API returns unexpected status code
     * @throws com.gooddata.sdk.common.GoodDataException       if no response from API or client-side HTTP error
     */
    public <T extends Obj> T getObjByUri(String uri, Class<T> cls) {    //CHANGED
        notNull(uri, "uri");    
        notNull(cls, "cls");
        try {
            final T result = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(cls)
                    .block();

            if (result != null) {
                return result;
            } else {
                throw new GoodDataException("Received empty response from API call.");
            }
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ObjNotFoundException(uri, cls, e);
            } else {
                throw new GoodDataException("Unable to get " + cls.getSimpleName().toLowerCase() + " " + uri, e);
            }
        } catch (GoodDataRestException e) {
            if (e.getStatusCode() == 404) {
                throw new ObjNotFoundException(uri, cls, e);
            } else {
                throw new GoodDataException("Unable to get " + cls.getSimpleName().toLowerCase() + " " + uri, e);
            }
        } catch (Exception e) {
            throw new GoodDataException("Unable to get " + cls.getSimpleName().toLowerCase() + " " + uri, e);
        }
    }


    /**
     * Retrieves a collection of objects corresponding to the supplied collection of URIs.
     *
     * @param project project that contains the objects to be retrieved
     * @param uris collection of URIs
     * @return collection of metadata objects corresponding to the supplied URIs
     */
    public Collection<Obj> getObjsByUris(Project project, Collection<String> uris) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(uris, "uris");

        try {
            final BulkGet result = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(BulkGet.URI).build(project.getId()))
                    .bodyValue(new BulkGetUris(uris))
                    .retrieve()
                    .bodyToMono(BulkGet.class)
                    .block();

           if (result != null) {
                return result.getItems();
            } else {
                throw new GoodDataException("Received empty response from API call.");
            }
        } catch (GoodDataRestException e) {
            throw e;
        } catch (Exception e) {
            throw new GoodDataException("Unable to get objects. Some of the supplied URIs may be malformed.", e);
        }
    }

    /**
     * Update given metadata object.
     *
     * @param obj object to update
     * @param <T> type of the updated object
     * @return updated metadata object
     * @throws ObjUpdateException in case of error
     */
    @SuppressWarnings("unchecked")
    public <T extends Updatable> T updateObj(T obj) {
        notNull(obj, "obj");
        notNull(obj.getUri(), "obj.uri");
        try {
            webClient.put()
                    .uri(obj.getUri())
                    .bodyValue(obj)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return getObjByUri(obj.getUri(), (Class<T>) obj.getClass());
        } catch (Exception e) {
            throw new ObjUpdateException(obj, e);
        }
    }

    /**
     * Remove metadata object URI
     *
     * @param obj metadata object to remove
     * @throws ObjNotFoundException if metadata object not found
     * @throws com.gooddata.sdk.common.GoodDataRestException   if GoodData REST API returns unexpected status code
     * @throws com.gooddata.sdk.common.GoodDataException       if no response from API or client-side HTTP error
     */
    public void removeObj(Obj obj) {
        notNull(obj, "obj");
        notNull(obj.getUri(), "obj.uri");
        try {
            webClient.delete()
                    .uri(obj.getUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) { 
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ObjNotFoundException(obj);
            } else {
                throw new GoodDataException("Unable to remove " + obj.getClass().getSimpleName().toLowerCase() + " " + obj.getUri(), e);
            }
        } catch (Exception e) {
            throw new GoodDataException("Unable to remove " + obj.getClass().getSimpleName().toLowerCase() + " " + obj.getUri(), e);
        }
    }

    /**
     * Remove metadata object by URI (format is <code>/gdc/md/{PROJECT_ID}/obj/{OBJECT_ID}</code>)
     *
     * @param uri URI in format <code>/gdc/md/{PROJECT_ID}/obj/{OBJECT_ID}</code>
     * @throws ObjNotFoundException if metadata object not found
     * @throws com.gooddata.sdk.common.GoodDataRestException   if GoodData REST API returns unexpected status code
     * @throws com.gooddata.sdk.common.GoodDataException       if no response from API or client-side HTTP error
     */
    public void removeObjByUri(String uri) {
        notNull(uri, "uri");
        try {
            webClient.delete()
                    .uri(uri)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) { 
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ObjNotFoundException(uri);
            } else {
                throw new GoodDataException("Unable to remove " + uri, e);
            }
        } catch (Exception e) { 
            throw new GoodDataException("Unable to remove " + uri, e);
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
     * @throws ObjNotFoundException if metadata object not found
     * @throws com.gooddata.sdk.common.GoodDataRestException   if GoodData REST API returns unexpected status code
     * @throws com.gooddata.sdk.common.GoodDataException       if no response from API or client-side HTTP error
     */
    public <T extends Obj> T getObjById(Project project, String id, Class<T> cls) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(id, "id");
        notNull(cls, "cls");
        return getObjByUri(OBJ_TEMPLATE.expand(project.getId(), id).toString(), cls);
    }

    /**
     * Get metadata object URI by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the object
     * @param cls          class of the resulting object
     * @param restrictions query restrictions
     * @param <T>          type of the object to be returned
     * @return the URI of metadata object
     * @throws ObjNotFoundException  if metadata object not found
     * @throws NonUniqueObjException if more than one object corresponds to search restrictions
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
     * Get metadata object by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the object
     * @param cls          class of the resulting object
     * @param restrictions query restrictions
     * @param <T>          type of the object to be returned
     * @return metadata object
     * @throws ObjNotFoundException  if metadata object not found
     * @throws NonUniqueObjException if more than one object corresponds to search restrictions
     */
    public <T extends Queryable> T getObj(Project project, Class<T> cls, Restriction... restrictions) {
        final Collection<String> results = findUris(project, cls, restrictions);
        if (results == null || results.isEmpty()) {
            throw new ObjNotFoundException(cls);
        } else if (results.size() != 1) {
            throw new NonUniqueObjException(cls, results);
        }
        return getObjByUri(results.iterator().next(), cls);
    }

    /**
     * Find metadata by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the metadata
     * @param cls          class of searched metadata
     * @param restrictions query restrictions
     * @param <T>          type of the metadata referenced in returned entries
     * @return the collection of metadata entries
     * @throws com.gooddata.sdk.common.GoodDataException if unable to query metadata
     */
    public <T extends Queryable> Collection<Entry> find(Project project, Class<T> cls, Restriction... restrictions) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(cls, "cls");

        final String type = getQueryType(cls);
        try {

            final Query queryResult = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path(Query.URI).build(project.getId(), type))
                    .retrieve()
                    .bodyToMono(Query.class)
                    .block();

            if (queryResult != null && queryResult.getEntries() != null) {
                return filterEntries(queryResult.getEntries(), restrictions);
            } else {
                throw new GoodDataException("Received empty response from API call.");
            }
        } catch (Exception e) { 
            throw new GoodDataException("Unable to query metadata: " + type, e);
        }
    }

    /**
     * Find metadata URIs by restrictions like identifier, title or summary.
     *
     * @param project      project where to search for the metadata
     * @param cls          class of searched metadata
     * @param restrictions query restrictions
     * @param <T>          type of the metadata referenced by returned URIs
     * @return the collection of metadata URIs
     * @throws com.gooddata.sdk.common.GoodDataException if unable to query metadata
     */
    public <T extends Queryable> Collection<String> findUris(Project project,
                                                             Class<T> cls,
                                                             Restriction... restrictions) {
        final Collection<Entry> entries = find(project, cls, restrictions);
        final Collection<String> result = new ArrayList<>(entries.size());
        result.addAll(entries.stream().map(Entry::getUri).collect(Collectors.toList()));
        return result;
    }

    /**
     * Find all objects which use the given object.
     * @param project project
     * @param obj     object to find using objects for
     * @param nearest find nearest objects only
     * @param types   what types (categories) to search for (for example 'reportDefinition', 'report', 'tableDataLoad',
     *                'table'...)
     * @return objects using given objects.
     */
    @SuppressWarnings("unchecked")
    public Collection<Entry> usedBy(Project project, Obj obj, boolean nearest, Class<? extends Obj>... types) {
        notNull(obj, "obj");
        return usedBy(project, obj.getUri(), nearest, types);
    }

    /**
     * Find all objects which use the given object.
     * @param project project
     * @param uri     URI of object to find using objects for
     * @param nearest find nearest objects only
     * @param types   what types (categories) to search for (for example 'reportDefinition', 'report', 'tableDataLoad',
     *                'table'...)
     * @return objects using given objects.
     * @see #usedBy(Project, Collection, boolean, Class[])
     */
    @SuppressWarnings("unchecked")
    public Collection<Entry> usedBy(Project project, String uri, boolean nearest, Class<? extends Obj>... types) {
        notNull(uri, "uri");
        notNull(project, "project");

        final Collection<Usage> usages = usedBy(project, asList(uri), nearest, types);
        return usages.size() > 0 ? usages.iterator().next().getUsedBy() : Collections.emptyList();
    }

    /**
     * Find all objects which use the given objects. Batch alternative to {@link #usedBy(Project, String, boolean, Class[])}
     * @param project project
     * @param uris    URIs of object to find using objects for
     * @param nearest find nearest objects only
     * @param types   what types (categories) to search for (for example 'reportDefinition', 'report', 'tableDataLoad',
     *                'table'...), returns all objects if no type is provided
     * @return objects usages
     * @see #usedBy(Project, String, boolean, Class[])
     */
    @SuppressWarnings("unchecked")
    public Collection<Usage> usedBy(Project project, Collection<String> uris, boolean nearest, Class<? extends Obj>... types) {
        notNull(uris, "uris");
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        final UseMany response;
        try {
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(InUseMany.USEDBY_URI).build(project.getId()))
                    .bodyValue(new InUseMany(uris, nearest, types))
                    .retrieve()
                    .bodyToMono(UseMany.class)
                    .block();
        } catch (Exception e) { 
            throw new GoodDataException("Unable to find objects.", e);
        }
        final List<Usage> usages = new ArrayList<>(uris.size());
        final Collection<UseManyEntries> useManyEntries = notNullState(response, "usedBy response").getUseMany();
        usages.addAll(useManyEntries.stream().map(useMany -> new Usage(useMany.getUri(), useMany.getEntries())).collect(Collectors.toList()));
        return usages;
    }

    /**
     * Find metadata URIs by restrictions. Identifier is the only supported restriction.
     *
     * @param project      project where to search for the metadata
     * @param restrictions query restrictions
     * @return the collection of metadata URIs
     * @throws com.gooddata.sdk.common.GoodDataException if unable to query metadata
     */
    public Collection<String> findUris(Project project, Restriction... restrictions) {
        notNull(project, "project" );
        noNullElements(restrictions, "restrictions");

        final List<String> ids = new ArrayList<>(restrictions.length);
        for (Restriction restriction: restrictions) {
            if (!Restriction.Type.IDENTIFIER.equals(restriction.getType())) {
                throw new IllegalArgumentException("All restrictions have to be of type " + Restriction.Type.IDENTIFIER);
            }
            ids.add(restriction.getValue());
        }

        return getUrisForIdentifiers(project, ids).getUris();
    }

    /**
     * Find metadata URIs for given identifiers.
     *
     * @param project     project where to search for the metadata
     * @param identifiers query restrictions
     * @return the map of identifiers as keys and metadata URIs as values
     * @throws com.gooddata.sdk.common.GoodDataException if unable to query metadata
     * @see #findUris(Project, Restriction...)
     */
    public Map<String, String> identifiersToUris(Project project, Collection<String> identifiers) {
        notNull(project, "project" );
        noNullElements(identifiers, "identifiers");

        return getUrisForIdentifiers(project, identifiers).asIdentifierToUri();
    }

    /**
     * Fetches attribute elements for given attribute using default display form.
     *
     * @param attribute attribute to fetch elements for
     * @return attribute elements or empty set if there is no link for elements in default display form
     */
    public List<AttributeElement> getAttributeElements(Attribute attribute) {
        notNull(attribute, "attribute");

        return getAttributeElements(attribute.getDefaultDisplayForm());
    }

    /**
     * Fetches attribute elements by given display form.
     *
     * @param displayForm display form to fetch attributes for
     * @return attribute elements or empty set if there is no link for elements
     */
    public List<AttributeElement> getAttributeElements(DisplayForm displayForm) {
        notNull(displayForm, "displayForm");

        final String elementsUri = displayForm.getElementsUri();
        if (StringUtils.isEmpty(elementsUri)) {
            return Collections.emptyList();
        }

        try {
            // changed
            final AttributeElements attributeElements = webClient.get()
                    .uri(elementsUri)
                    .retrieve()
                    .bodyToMono(AttributeElements.class)
                    .block();
            return notNullState(attributeElements, "attributeElements").getElements();
        } catch (Exception e) { // changed
            throw new GoodDataException("Unable to get attribute elements from " + elementsUri + ".", e);
        }
    }

    /**
     * Get project/workspace timezone.
     *
     * @param project project from what to return the timezone
     * @return string identifier of the timezone (see IANA/Olson tz database for possible values)
     * @throws com.gooddata.sdk.common.GoodDataRestException if GoodData REST API returns unexpected status code
     * @throws com.gooddata.sdk.common.GoodDataException     if no response from API or client-side HTTP error
     */
    public String getTimezone(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            final Service result = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path(TIMEZONE_URI).build(project.getId()))
                    .retrieve()
                    .bodyToMono(Service.class)
                    .block();

            if (result != null) {
                return result.getTimezone();
            } else {
                throw new GoodDataException("Received empty response from API call.");
            }
        } catch (Exception e) { 
            throw new GoodDataException("Unable to get timezone of project/workspace " + project.getId(), e);
        }
    }

    /**
     * Set project/workspace timezone.
     *
     * @param project the project/workspace where to set the timezone
     * @param timezone the timezone to be set (see IANA/Olson tz database for possible values)
     */
    public void setTimezone(final Project project, final String timezone) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(timezone, "timezone");
        notEmpty(timezone, "timezone");

        try {
            final Service result = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(TIMEZONE_URI).build(project.getId()))
                    .bodyValue(new Service(timezone))
                    .retrieve()
                    .bodyToMono(Service.class)
                    .block();

            if (result == null) {
                throw new GoodDataException("Unexpected empty result from API call.");
            }
        } catch (Exception e) {
            throw new GoodDataException("Unable to set timezone of project/workspace " + project.getId(), e);
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

    private IdentifiersAndUris getUrisForIdentifiers(final Project project, final Collection<String> identifiers) {
        final IdentifiersAndUris response;
        try {
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(IdentifiersAndUris.URI).build(project.getId()))
                    .bodyValue(new IdentifierToUri(identifiers))
                    .retrieve()
                    .bodyToMono(IdentifiersAndUris.class)
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to get URIs from identifiers.", e);
        }
        return response;
    }

    private <T extends Queryable> String getQueryType(final Class<T> cls) {
        final String typeBase = cls.getSimpleName().toLowerCase();

        if (ReportDefinition.class.equals(cls)) {
            return typeBase;
        }

        if (IRREGULAR_PLURAL_WORD_SUFFIXES.stream().anyMatch(typeBase::endsWith)) {
            return typeBase + "es";
        }

        return typeBase + "s";
    }
}
