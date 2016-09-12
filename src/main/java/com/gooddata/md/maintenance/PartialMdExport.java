/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.maintenance;

import static com.gooddata.util.Validate.notEmpty;
import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Collection;
import java.util.HashSet;

/**
 * Partial metadata export configuration structure.
 * Serialization only.
 */
@JsonTypeName("partialMDExport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PartialMdExport {

    public static final String URI = "/gdc/md/{projectId}/maintenance/partialmdexport";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private final Collection<String> uris;
    private final boolean crossDataCenterExport;
    private final boolean exportAttributeProperties;

    /**
     * Creates new PartialMdExport. At least one uri should be given.
     * Sets crossDataCenterExport and exportAttributeProperties to false.
     *
     * @param mdObjectsUris list of uris to metadata objects which should be exported
     */
    public PartialMdExport(String... mdObjectsUris) {
        this(new HashSet<>(asList(mdObjectsUris)));
    }

    /**
     * Creates new PartialMdExport. At least one uri should be given.
     * Sets crossDataCenterExport and exportAttributeProperties to false.
     *
     * @param mdObjectsUris list of uris to metadata objects which should be exported
     */
    public PartialMdExport(Collection<String> mdObjectsUris) {
        this(false, false, mdObjectsUris);
    }

    /**
     * Creates new PartialMdExport. At least one uri should be given.
     *
     * @param exportAttributeProperties whether to add necessary data to be able to clone attribute properties
     * @param crossDataCenterExport whether export should be usable in any Data Center
     * @param mdObjectsUris list of uris to metadata objects which should be exported
     */
    public PartialMdExport(boolean exportAttributeProperties, boolean crossDataCenterExport, String... mdObjectsUris) {
        this(exportAttributeProperties, crossDataCenterExport, new HashSet<>(asList(mdObjectsUris)));
    }

    /**
     * Creates new PartialMdExport.  At least one uri should be given.
     *
     * @param exportAttributeProperties whether to add necessary data to be able to clone attribute properties
     * @param crossDataCenterExport whether export should be usable in any Data Center
     * @param mdObjectsUris list of uris to metadata objects which should be exported
     */
    public PartialMdExport(boolean exportAttributeProperties, boolean crossDataCenterExport, Collection<String> mdObjectsUris) {
        notEmpty(mdObjectsUris, "uris");
        this.uris = mdObjectsUris;
        this.crossDataCenterExport = crossDataCenterExport;
        this.exportAttributeProperties = exportAttributeProperties;
    }

    public Collection<String> getUris() {
        return uris;
    }

    public boolean isCrossDataCenterExport() {
        return crossDataCenterExport;
    }

    public boolean isExportAttributeProperties() {
        return exportAttributeProperties;
    }
}
