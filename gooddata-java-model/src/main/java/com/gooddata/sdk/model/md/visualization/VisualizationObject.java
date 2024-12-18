/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.gooddata.sdk.model.executeafm.UriObjQualifier;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.Execution;
import com.gooddata.sdk.model.executeafm.afm.filter.ExtendedFilter;
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec;
import com.gooddata.sdk.model.executeafm.resultspec.TotalItem;
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.md.Queryable;
import com.gooddata.sdk.model.md.Updatable;
import com.gooddata.sdk.model.md.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Complete information about new visualization object that can be stored as MD object (see {@link Obj})
 * to md server.
 * <p/>
 * The visualization object is part of new GD UI visualizations situated in AD and KPI dashboards.
 * This object is a persistent form of AFM (Attribute, Measures, Filters) report executions.
 */
@JsonTypeName(VisualizationObject.NAME)
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisualizationObject extends AbstractObj implements Queryable, Updatable {
    static final String NAME = "visualizationObject";

    @JsonProperty("content")
    private final Content content;

    /**
     * Constructor.
     *
     * @param title title of visualization object
     * @param visualizationClassUri uri to the {@link VisualizationClass}
     */
    public VisualizationObject(final String title, final String visualizationClassUri) {
        this(new Content(notEmpty(visualizationClassUri), emptyList()), new Meta(notEmpty(title)));
    }

    @JsonCreator
    private VisualizationObject(@JsonProperty("content") final Content content, @JsonProperty("meta") final Meta meta) {
        super(meta);
        this.content = notNull(content);
    }

    /**
     * @return all measures from all buckets in visualization object
     */
    @JsonIgnore
    public List<Measure> getMeasures() {
        return content.getMeasures();
    }

    /**
     * @return all totals from all buckets in visualization object
     */
    @JsonIgnore
    public List<TotalItem> getTotals() {
        return content.getTotals();
    }

    /**
     * Get measure by local identifier or null if not found
     * @param localIdentifier of measure
     * @return measure or null
     */
    @JsonIgnore
    public Measure getMeasure(final String localIdentifier) {
        return getMeasures().stream()
                .filter(measure -> measure.getLocalIdentifier().equals(localIdentifier))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return all measures from all buckets whose measure definition is instance of {@link VOSimpleMeasureDefinition}
     */
    @SuppressWarnings("deprecation")
    @JsonIgnore
    public List<Measure> getSimpleMeasures() {
        return getMeasures().stream()
                .filter(measure -> measure.getDefinition() instanceof VOSimpleMeasureDefinition)
                .collect(toList());
    }

    /**
     * @return all attributes from all buckets in visualization object
     */
    @JsonIgnore
    public List<VisualizationAttribute> getAttributes() {
        return content.getAttributes();
    }

    @JsonIgnore
    public VisualizationAttribute getAttribute(final String localIdentifier) {
        return getAttributes().stream()
                .filter(attribute -> attribute.getLocalIdentifier().equals(localIdentifier))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns attribute from collection bucket, if and only if bucket contains exactly one item of type
     * {@link VisualizationAttribute}, null otherwise
     * @param type of collection which we want to get, stored as local identifier in each bucket
     * @return attribute from collection bucket
     */
    @JsonIgnore
    public VisualizationAttribute getAttributeFromCollection(final CollectionType type) {
        return content.getBuckets().stream()
                .filter(bucket -> type.isValueOf(bucket.getLocalIdentifier()))
                .findFirst()
                .map(Bucket::getOnlyAttribute)
                .orElse(null);
    }

    @JsonIgnore
    public boolean hasMeasures() {
        return !getMeasures().isEmpty();
    }

    /**
     * @return true if visualization object contains at leas one PoP measure or measure with compute ratio, false otherwise
     */
    @JsonIgnore
    public boolean hasDerivedMeasure() {
        return getMeasures().stream().anyMatch(measure -> measure.isPop() || measure.hasComputeRatio());
    }

    /**
     * Method to get uri to requested local identifier from reference items
     * @param id of item
     * @return uri of requested item
     */
    @JsonIgnore
    public String getItemById(String id) {
        Map<String, String> referenceItems = getReferenceItems();
        if (referenceItems != null) {
            return referenceItems.get(id);
        }

        return null;
    }

    /**
     * @return attribute from stack collection
     */
    @JsonIgnore
    public VisualizationAttribute getStack() {
        return getAttributeFromCollection(CollectionType.STACK);
    }

    /**
     * @return attribute from view collection
     */
    @JsonIgnore
    public VisualizationAttribute getView() {
        return getAttributeFromCollection(CollectionType.VIEW);
    }

    /**
     * @return attribute from segment collection
     */
    @JsonIgnore
    public VisualizationAttribute getSegment() {
        return getAttributeFromCollection(CollectionType.SEGMENT);
    }

    /**
     * @return attribute from trend collection
     */
    @JsonIgnore
    public VisualizationAttribute getTrend() {
        return getAttributeFromCollection(CollectionType.TREND);
    }

    /**
     * @return uri to the {@link VisualizationClass}
     */
    @JsonIgnore
    public String getVisualizationClassUri() {
        return content.getVisualizationClassUri();
    }

    /**
     * @return buckets from visualization object
     */
    @JsonIgnore
    public List<Bucket> getBuckets() {
        return content.getBuckets();
    }

    /**
     * @param buckets replacing previous visualization object's buckets
     */
    @JsonIgnore
    public void setBuckets(List<Bucket> buckets) {
        content.setBuckets(buckets);
    }

    /**
     * @return filters from visualization object
     */
    @JsonIgnore
    public List<ExtendedFilter> getFilters() {
        return content.getFilters();
    }

    /**
     * @param filters replacing previous visualization object's filters
     */
    @JsonIgnore
    public void setFilters(List<ExtendedFilter> filters) {
        content.setFilters(filters);
    }

    /**
     * @return json properties of visualization object in form of string
     */
    @JsonIgnore
    public String getProperties() {
        return content.getProperties();
    }

    /**
     * @param properties to be set to visualization object in form of stringified json
     */
    @JsonIgnore
    public void setProperties(String properties) {
        content.setProperties(properties);
    }

    /**
     * @return hash map of references in form localIdentifier:uri
     */
    @JsonIgnore
    public Map<String, String> getReferenceItems() {
        return content.getReferenceItems();
    }

    /**
     * @param referenceItems is a hash map of references in form localIdentifier:uri to be set to visualization object
     */
    @JsonIgnore
    public void setReferenceItems(Map<String, String> referenceItems) {
        content.setReferenceItems(referenceItems);
    }

    /**
     * @return uri to visualization class wrapped as {@link UriObjQualifier}
     */
    @JsonIgnore
    public UriObjQualifier getVisualizationClass() {
        return content.getVisualizationClass();
    }

    /**
     * @param uri to replace previous visualization class's uri, wrapped as {@link UriObjQualifier}
     */
    @JsonIgnore
    public void setVisualizationClass(UriObjQualifier uri) {
        content.setVisualizationClass(uri);
    }

    /**
     * @see VisualizationConverter#convertToExecution(VisualizationObject, Function)
     */
    @JsonIgnore
    public Execution convertToExecution(Function<String, VisualizationClass> visualizationClassgetter) {
        return VisualizationConverter.convertToExecution(this, visualizationClassgetter);
    }

    /**
     * @see VisualizationConverter#convertToExecution(VisualizationObject, VisualizationClass)
     */
    @JsonIgnore
    public Execution convertToExecution(VisualizationClass visualizationClass) {
        return VisualizationConverter.convertToExecution(this, visualizationClass);
    }

    /**
     * @see VisualizationConverter#convertToAfm(VisualizationObject)
     */
    @JsonIgnore
    public Afm convertToAfm() {
        return VisualizationConverter.convertToAfm(this);
    }

    /**
     * @see VisualizationConverter#convertToResultSpec(VisualizationObject, Function)
     */
    @JsonIgnore
    public ResultSpec convertToResultSpec(Function<String, VisualizationClass> visualizationClassgetter) {
        return VisualizationConverter.convertToResultSpec(this, visualizationClassgetter);
    }

    /**
     * @see VisualizationConverter#convertToResultSpec(VisualizationObject, VisualizationClass)
     */
    @JsonIgnore
    public ResultSpec convertToResultSpec(VisualizationClass visualizationClass) {
        return VisualizationConverter.convertToResultSpec(this, visualizationClass);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content implements Serializable {

        private static final long serialVersionUID = 2895359822118041504L;
        private UriObjQualifier visualizationClass;
        private List<Bucket> buckets;
        private List<ExtendedFilter> filters;
        private String properties;
        private Map<String, String> referenceItems;

        private Content(final String visualizationClassUri, final List<Bucket> buckets) {
            this(new UriObjQualifier(visualizationClassUri), buckets, null, null, null);
        }

        @JsonCreator
        private Content(@JsonProperty("visualizationClass") final UriObjQualifier visualizationClass,
                       @JsonProperty("buckets") final List<Bucket> buckets,
                       @JsonProperty("filters") final List<ExtendedFilter> filters,
                       @JsonProperty("properties") final String properties,
                       @JsonProperty("references") final Map<String, String> referenceItems) {

            this.visualizationClass = notNull(visualizationClass);
            this.buckets = notNull(buckets);
            this.filters = filters;
            this.properties = properties;
            this.referenceItems = referenceItems;
        }

        public List<Bucket> getBuckets() {
            return buckets;
        }

        @JsonIgnore
        public List<VisualizationAttribute> getAttributes() {
            return buckets.stream()
                    .flatMap(bucket -> bucket.getItems().stream())
                    .filter(VisualizationAttribute.class::isInstance)
                    .map(VisualizationAttribute.class::cast)
                    .collect(toList());
        }

        public void setVisualizationClass(UriObjQualifier visualizationClass) {
            this.visualizationClass = visualizationClass;
        }

        public void setBuckets(List<Bucket> buckets) {
            this.buckets = buckets;
        }

        public void setFilters(List<ExtendedFilter> filters) {
            this.filters = filters;
        }

        public void setProperties(String properties) {
            this.properties = properties;
        }

        public void setReferenceItems(Map<String, String> referenceItems) {
            this.referenceItems = referenceItems;
        }

        @JsonIgnore
        public List<Measure> getMeasures() {
            return buckets.stream()
                    .flatMap(bucket -> bucket.getItems().stream())
                    .filter(Measure.class::isInstance)
                    .map(Measure.class::cast)
                    .collect(toList());
        }

        @JsonIgnore
        public List<TotalItem> getTotals() {
            return buckets.stream()
                    .filter(bucket -> bucket.getTotals() != null)
                    .flatMap(bucket -> bucket.getTotals().stream())
                    .collect(toList());
        }

        @JsonIgnore
        public String getVisualizationClassUri() {
            return visualizationClass.getUri();
        }

        public List<ExtendedFilter> getFilters() {
            if (filters == null) {
                return new ArrayList<>();
            }

            return filters;
        }

        public String getProperties() {
            return properties;
        }

        public UriObjQualifier getVisualizationClass() {
            return visualizationClass;
        }

        @JsonProperty("references")
        public Map<String, String> getReferenceItems() {
            return referenceItems;
        }
    }
}
