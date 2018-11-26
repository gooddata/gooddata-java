/*
 * Copyright (C) 2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.visualization;

import static com.gooddata.executeafm.resultspec.Dimension.MEASURE_GROUP;
import static com.gooddata.md.visualization.CollectionType.SEGMENT;
import static com.gooddata.md.visualization.CollectionType.STACK;
import static com.gooddata.md.visualization.CollectionType.TREND;
import static com.gooddata.md.visualization.CollectionType.VIEW;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.executeafm.Execution;
import com.gooddata.executeafm.afm.Afm;
import com.gooddata.executeafm.afm.AttributeItem;
import com.gooddata.executeafm.afm.CompatibilityFilter;
import com.gooddata.executeafm.afm.DateFilter;
import com.gooddata.executeafm.afm.FilterItem;
import com.gooddata.executeafm.afm.MeasureItem;
import com.gooddata.executeafm.afm.NegativeAttributeFilter;
import com.gooddata.executeafm.afm.PositiveAttributeFilter;
import com.gooddata.executeafm.afm.SimpleMeasureDefinition;
import com.gooddata.executeafm.resultspec.Dimension;
import com.gooddata.executeafm.resultspec.ResultSpec;
import com.gooddata.executeafm.resultspec.SortItem;
import com.gooddata.util.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class for converting {@link VisualizationObject} into {@link Execution}
 */
public abstract class VisualizationConverter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Generate Execution from Visualization object.
     *
     * @param visualizationObject which will be converted to {@link Execution}
     * @param visualizationClassGetter {@link Function} for fetching VisualizationClass, which is necessary for correct generation of {@link ResultSpec}
     * @return {@link Execution} object
     * @see #convertToExecution(VisualizationObject, VisualizationClass)
     */
    public static Execution convertToExecution(final VisualizationObject visualizationObject,
            final Function<String, VisualizationClass> visualizationClassGetter) {
        return convertToExecution(visualizationObject,
                notNull(visualizationClassGetter).apply(notNull(visualizationObject).getVisualizationClassUri()));
    }

    /**
     * Generate Execution from Visualization object.
     *
     * @param visualizationObject which will be converted to {@link Execution}
     * @param visualizationClass visualizationClass, which is necessary for correct generation of {@link ResultSpec}
     * @return {@link Execution} object
     * @see #convertToAfm(VisualizationObject)
     * @see #convertToResultSpec(VisualizationObject, VisualizationClass)
     */
    public static Execution convertToExecution(final VisualizationObject visualizationObject,
            final VisualizationClass visualizationClass) {
        ResultSpec resultSpec = convertToResultSpec(visualizationObject, visualizationClass);
        Afm afm = convertToAfm(visualizationObject);
        return new Execution(afm, resultSpec);
    }

    /**
     * Generate Afm from Visualization object.
     *
     * @param visualizationObject which will be converted to {@link Execution}
     * @return {@link Afm} object
     */
    public static Afm convertToAfm(final VisualizationObject visualizationObject) {
        final List<AttributeItem> attributes = convertAttributes(visualizationObject.getAttributes());
        final List<CompatibilityFilter> filters = convertFilters(visualizationObject.getFilters());
        final List<MeasureItem> measures = convertMeasures(visualizationObject.getMeasures());

        return new Afm(attributes, filters, measures, null);
    }

    /**
     * Generate ResultSpec from Visualization object. Currently {@link ResultSpec}'s {@link Dimension}s can be generated
     * for table and four types of chart: bar, column, line and pie.
     *
     * @param visualizationObject which will be converted to {@link Execution}
     * @param visualizationClassGetter {@link Function} for fetching VisualizationClass, which is necessary for correct generation of {@link ResultSpec}
     * @return {@link Execution} object
     */
    public static ResultSpec convertToResultSpec(final VisualizationObject visualizationObject,
            final Function<String, VisualizationClass> visualizationClassGetter) {
        return convertToResultSpec(visualizationObject,
                notNull(visualizationClassGetter).apply(notNull(visualizationObject).getVisualizationClassUri()));
    }

    /**
     * Generate ResultSpec from Visualization object. Currently {@link ResultSpec}'s {@link Dimension}s can be generated
     * for table and four types of chart: bar, column, line and pie.
     *
     * @param visualizationObject which will be converted to {@link Execution}
     * @param visualizationClass VisualizationClass, which is necessary for correct generation of {@link ResultSpec}
     * @return {@link Execution} object
     */
    public static ResultSpec convertToResultSpec(final VisualizationObject visualizationObject,
            final VisualizationClass visualizationClass) {
        Validate.isTrue(visualizationObject.getVisualizationClassUri().equals(visualizationClass.getUri()),
                "visualizationClass URI does not match the URI within visualizationObject, "
                        + "you're trying to create ResultSpec for incompatible objects");
        List<SortItem> sorts = getSorting(visualizationObject);
        List<Dimension> dimensions = getDimensions(visualizationObject, visualizationClass.getVisualizationType());
        return new ResultSpec(dimensions, sorts);
    }

    static List<SortItem> getSorting(final VisualizationObject visualizationObject) {
        try {
             List<SortItem> sorts = parseSorting(visualizationObject.getProperties());
             if (sorts != null) {
                 return sorts;
             }
        } catch (Exception ignored) {}

        return null;
    }

    static List<SortItem> parseSorting(final String properties) throws Exception {
        JsonNode jsonProperties = parseProperties(properties);
        JsonNode nodeSortItems = jsonProperties.get("sortItems");
        TypeReference<List<SortItem>> mapType = new TypeReference<List<SortItem>>() {};
        return MAPPER.convertValue(nodeSortItems, mapType);
    }

    private static List<Dimension> getDimensions(final VisualizationObject visualizationObject,
                                                 final VisualizationType visualizationType) {
        switch (visualizationType) {
            case COLUMN:
            case BAR:
                return getDimensionsForStacked(visualizationObject, STACK, VIEW);
            case LINE:
                return getDimensionsForStacked(visualizationObject, SEGMENT, TREND);
            case PIE:
                return getDimensionsForPie(visualizationObject);
            default:
                return getDimensionsForTable(visualizationObject);
        }
    }

    private static List<Dimension> getDimensionsForPie(final VisualizationObject visualizationObject) {
        VisualizationAttribute attribute = visualizationObject.getView();

        List<Dimension> dimensions = new ArrayList<>();

        if (visualizationObject.hasMeasures()) {
            dimensions.add(new Dimension(MEASURE_GROUP));
        }

        if (attribute != null) {
            dimensions.add(new Dimension(attribute.getLocalIdentifier()));
        }

        return dimensions;
    }

    private static List<Dimension> getDimensionsForStacked(final VisualizationObject visualizationObject,
                                                           final CollectionType stackBy,
                                                           final CollectionType viewBy) {
        VisualizationAttribute stack = visualizationObject.getAttributeFromCollection(stackBy);
        VisualizationAttribute view = visualizationObject.getAttributeFromCollection(viewBy);

        List<Dimension> dimensions = new ArrayList<>();

        if (stack != null) {
            dimensions.add(new Dimension(stack.getLocalIdentifier()));

            List<String> dimensionItems = new ArrayList<>();

            if (view != null) {
                dimensionItems.add(view.getLocalIdentifier());
            }

            if (visualizationObject.hasMeasures()) {
                dimensionItems.add(MEASURE_GROUP);
            }

            if (!dimensionItems.isEmpty()) {
                dimensions.add(new Dimension(dimensionItems));
            }
        } else {
            if (visualizationObject.hasMeasures()) {
                dimensions.add(new Dimension(MEASURE_GROUP));
            }

            if (view != null) {
                dimensions.add(new Dimension(view.getLocalIdentifier()));
            }
        }

        return dimensions;
    }

    private static List<Dimension> getDimensionsForTable(final VisualizationObject visualizationObject) {
        List<Dimension> dimensions = new ArrayList<>();

        List<VisualizationAttribute> attributes = visualizationObject.getAttributes();

        if (!attributes.isEmpty()) {
            dimensions.add(new Dimension(attributes.stream()
                    .map(VisualizationAttribute::getLocalIdentifier)
                    .collect(toList())
            ));
        } else {
            dimensions.add(new Dimension(new ArrayList<>()));
        }

        if (visualizationObject.hasMeasures()) {
            dimensions.add(new Dimension(MEASURE_GROUP));
        }

        return dimensions;
    }

    private static JsonNode parseProperties(final String properties) throws Exception {
        return MAPPER.readValue(properties, JsonNode.class);
    }

    private static List<AttributeItem> convertAttributes(final List<VisualizationAttribute> attributes) {
        return attributes.stream()
                .map(AttributeItem.class::cast)
                .collect(toList());
    }

    private static List<CompatibilityFilter> convertFilters(final List<FilterItem> filters) {
        if (filters == null) {
            return new ArrayList<>();
        }

        List<FilterItem> validFilters = removeIrrelevantFilters(filters);
        return getCompatibilityFilters(validFilters);
    }

    private static List<MeasureItem> convertMeasures(final List<Measure> measures) {
        return measures.stream()
                .map(VisualizationConverter::removeFormat)
                .map(VisualizationConverter::getAfmMeasure)
                .map(VisualizationConverter::convertMeasureFilters)
                .collect(toList());
    }

    private static Measure removeFormat(final Measure measure) {
        if (measure.hasComputeRatio()) {
            measure.setFormat("");
        }

        return measure;
    }

    private static MeasureItem getAfmMeasure(final Measure measure) {
        String alias = measure.getAlias();
        String usedTitle = alias;
        if(alias == null || alias.isEmpty()) {
            if (measure.getTitle() != null) {
                usedTitle = measure.getTitle();
            }
        }
        return new MeasureItem(measure.getDefinition(), measure.getLocalIdentifier(), usedTitle, measure.getFormat());
    }

    private static MeasureItem convertMeasureFilters(final MeasureItem measure) {
        if (measure.getDefinition() instanceof SimpleMeasureDefinition) {
            List<FilterItem> filters = ((SimpleMeasureDefinition) measure.getDefinition()).getFilters();

            List<FilterItem> validFilters;
            if (filters == null) {
                validFilters = new ArrayList<>();
            } else {
                validFilters = removeIrrelevantFilters(filters);
            }

            ((SimpleMeasureDefinition) measure.getDefinition()).setFilters(validFilters);
        }

        return measure;
    }


    private static List<CompatibilityFilter> getCompatibilityFilters(final List<FilterItem> filters) {
        return (List<CompatibilityFilter>)(List<?>) filters;
    }

    private static List<FilterItem> removeIrrelevantFilters(final List<FilterItem> filters) {
        return filters.stream()
                .filter((f) -> {
                    if (f instanceof DateFilter) {
                        return !((DateFilter) f).isAllTimeSelected();
                    } else if (f instanceof NegativeAttributeFilter) {
                        return !((NegativeAttributeFilter) f).isAllSelected();
                    } else if (f instanceof PositiveAttributeFilter) {
                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }
}
