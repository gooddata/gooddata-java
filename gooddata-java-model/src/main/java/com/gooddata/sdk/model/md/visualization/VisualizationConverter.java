/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.sdk.model.executeafm.Execution;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.afm.AttributeItem;
import com.gooddata.sdk.model.executeafm.afm.filter.CompatibilityFilter;
import com.gooddata.sdk.model.executeafm.afm.filter.DateFilter;
import com.gooddata.sdk.model.executeafm.afm.filter.ExtendedFilter;
import com.gooddata.sdk.model.executeafm.afm.filter.FilterItem;
import com.gooddata.sdk.model.executeafm.afm.MeasureItem;
import com.gooddata.sdk.model.executeafm.afm.filter.MeasureValueFilter;
import com.gooddata.sdk.model.executeafm.afm.filter.NegativeAttributeFilter;
import com.gooddata.sdk.model.executeafm.afm.filter.PositiveAttributeFilter;
import com.gooddata.sdk.model.executeafm.afm.SimpleMeasureDefinition;
import com.gooddata.sdk.model.executeafm.afm.filter.RankingFilter;
import com.gooddata.sdk.model.executeafm.resultspec.Dimension;
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec;
import com.gooddata.sdk.model.executeafm.resultspec.SortItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gooddata.sdk.model.executeafm.resultspec.Dimension.MEASURE_GROUP;
import static com.gooddata.sdk.common.util.Validate.isTrue;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.util.stream.Collectors.toList;

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
        notNull(visualizationObject, "visualizationObject");
        notNull(visualizationClassGetter, "visualizationClassGetter");
        return convertToExecution(visualizationObject,
                visualizationClassGetter.apply(visualizationObject.getVisualizationClassUri()));
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
        notNull(visualizationObject, "visualizationObject");
        notNull(visualizationClass, "visualizationClass");
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
        notNull(visualizationObject, "visualizationObject");
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
        notNull(visualizationObject, "visualizationObject");
        notNull(visualizationClassGetter, "visualizationClassGetter");
        return convertToResultSpec(visualizationObject,
                visualizationClassGetter.apply(visualizationObject.getVisualizationClassUri()));
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
        notNull(visualizationObject, "visualizationObject");
        notNull(visualizationClass, "visualizationClass");
        isTrue(visualizationObject.getVisualizationClassUri().equals(visualizationClass.getUri()),
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
                return getDimensionsForStacked(visualizationObject, CollectionType.STACK, CollectionType.VIEW);
            case LINE:
                return getDimensionsForStacked(visualizationObject, CollectionType.SEGMENT, CollectionType.TREND);
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

    private static List<CompatibilityFilter> convertFilters(final List<ExtendedFilter> filters) {
        if (filters == null) {
            return new ArrayList<>();
        }

        List<ExtendedFilter> validFilters = removeIrrelevantFilters(filters);
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

    private static List<CompatibilityFilter> getCompatibilityFilters(final List<ExtendedFilter> filters) {
        return filters.stream()
            .map(CompatibilityFilter.class::cast)
            .collect(toList());
    }

    private static <T> List<T> removeIrrelevantFilters(final List<T> filters) {
        return filters.stream()
                .filter(f -> {
                    if (f instanceof DateFilter) {
                        return !((DateFilter) f).isAllTimeSelected();
                    } else if (f instanceof MeasureValueFilter) {
                        return ((MeasureValueFilter) f).getCondition() != null;
                    } else if (f instanceof NegativeAttributeFilter) {
                        return !((NegativeAttributeFilter) f).isAllSelected();
                    } else return f instanceof PositiveAttributeFilter || f instanceof RankingFilter;

                })
                .collect(Collectors.toList());
    }
}
