/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.executeafm;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.executeafm.Execution;
import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;
import com.gooddata.sdk.model.executeafm.VisualizationExecution;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.model.executeafm.afm.AttributeItem;
import com.gooddata.sdk.model.executeafm.afm.MeasureItem;
import com.gooddata.sdk.model.executeafm.afm.SimpleMeasureDefinition;
import com.gooddata.sdk.model.executeafm.response.AttributeHeader;
import com.gooddata.sdk.model.executeafm.response.ExecutionResponse;
import com.gooddata.sdk.model.executeafm.response.MeasureGroupHeader;
import com.gooddata.sdk.model.executeafm.response.ResultDimension;
import com.gooddata.sdk.model.executeafm.result.DataList;
import com.gooddata.sdk.model.executeafm.result.DataValue;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;
import com.gooddata.sdk.model.executeafm.result.ResultHeaderItem;
import com.gooddata.sdk.model.md.visualization.Bucket;
import com.gooddata.sdk.model.md.visualization.Measure;
import com.gooddata.sdk.model.md.visualization.VOSimpleMeasureDefinition;
import com.gooddata.sdk.model.md.visualization.VisualizationAttribute;
import com.gooddata.sdk.model.md.visualization.VisualizationClass;
import com.gooddata.sdk.model.md.visualization.VisualizationObject;
import org.junit.jupiter.api.MethodOrderer; 
import org.junit.jupiter.api.Order; 
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collection;
import java.util.List;

import static com.gooddata.sdk.model.md.Restriction.identifier;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExecuteAfmServiceAT extends AbstractGoodDataAT {

    private static final String GDC_TABLE_VISUALIZATION_CLASS_ID = "gdc.visualization.table";
    private static final String ATTRIBUTE_LOCAL_IDENTIFIER = "a1";
    private static final String MEASURE_LOCAL_IDENTIFIER = "m1";

    private static ExecutionResponse afmResponse;
    private static ExecutionResponse visResponse;

    @Test
    @Order(1)
    public void testExecuteAfm() {
        final Execution execution = new Execution(new Afm()
                .addAttribute(new AttributeItem(new IdentifierObjQualifier(attr.getDefaultDisplayForm().getIdentifier()),
                        ATTRIBUTE_LOCAL_IDENTIFIER))
                .addMeasure(new MeasureItem(new SimpleMeasureDefinition(new UriObjQualifier(metric.getUri())),
                        MEASURE_LOCAL_IDENTIFIER))
        );

        afmResponse = gd.getExecuteAfmService().executeAfm(project, execution);

        checkExecutionResponse(afmResponse);
    }

    @Test
    @Order(2)
    public void testExecuteVisualization() {
        final VisualizationObject vizObject = createVisualizationObject();
        final VisualizationExecution execution = new VisualizationExecution(vizObject.getUri());

        visResponse = gd.getExecuteAfmService().executeVisualization(project, execution);

        checkExecutionResponse(visResponse);
    }

    @Test
    @Order(3)
    public void testGetAfmExecutionResult() {
        final ExecutionResult afmResult = gd.getExecuteAfmService().getResult(afmResponse).get();
        checkExecutionResult(afmResult);
    }

    @Test
    @Order(4)
    public void testGetVisualizationExecutionResult() {
        final ExecutionResult visResult = gd.getExecuteAfmService().getResult(visResponse).get();
        checkExecutionResult(visResult);
    }

    @SuppressWarnings("deprecation")
    private VisualizationObject createVisualizationObject() {
        final String vizClassUri = gd.getMetadataService().getObjUri(project, VisualizationClass.class,
                identifier(GDC_TABLE_VISUALIZATION_CLASS_ID));

        final VisualizationObject vizObject = new VisualizationObject("some title", vizClassUri);
        vizObject.setBuckets(asList(
                new Bucket("vizObjBucket1", singletonList(new VisualizationAttribute(
                        new UriObjQualifier(attr.getDefaultDisplayForm().getUri()), ATTRIBUTE_LOCAL_IDENTIFIER))),
                new Bucket("vizObjBucket2", singletonList(new Measure(
                        new VOSimpleMeasureDefinition(new UriObjQualifier(metric.getUri())), MEASURE_LOCAL_IDENTIFIER)))
        ));
        return gd.getMetadataService().createObj(project, vizObject);
    }

    private static void checkExecutionResponse(final ExecutionResponse response) {
        assertThat(response, notNullValue());
        assertThat("should have 2 dimensions", response.getDimensions(), hasSize(2));

        final ResultDimension firstDim = response.getDimensions().get(0);
        assertThat("1st dim should have 1 header", firstDim.getHeaders(), hasSize(1));
        assertThat("1st dim 1st header should be AttributeHeader", firstDim.getHeaders().get(0), instanceOf(AttributeHeader.class));
        final AttributeHeader attrHeader = (AttributeHeader) firstDim.getHeaders().get(0);
        assertThat("header's formOf should point to given attribute", attrHeader.getFormOf().getUri(), is(attr.getUri()));

        final ResultDimension secondDim = response.getDimensions().get(1);
        assertThat("2nd dim should have 1 header", secondDim.getHeaders(), hasSize(1));
        assertThat("2nd dim 1st header should be MeasureGroupHeader", secondDim.getHeaders().get(0), instanceOf(MeasureGroupHeader.class));
        final MeasureGroupHeader measureGroupHeader = (MeasureGroupHeader) secondDim.getHeaders().get(0);
        assertThat(measureGroupHeader.getItems(), hasSize(1));
        assertThat("the only measureHeader should point to given metric", measureGroupHeader.getItems().get(0).getUri(), is(metric.getUri()));
    }

    private static void checkExecutionResult(final ExecutionResult result) {
        assertThat(result, notNullValue());

        final List<ResultHeaderItem> firstDimHeaders = result.getHeaderItems().get(0).get(0);
        assertThat("1st dim should have two header items", firstDimHeaders, hasSize(2));
        assertThat(headerItemsNames(firstDimHeaders), hasItems("HR", "DevOps"));

        final List<ResultHeaderItem> secondDimHeaders = result.getHeaderItems().get(1).get(0);
        assertThat("2nd dim should have one header item", secondDimHeaders, hasSize(1));
        assertThat(headerItemsNames(secondDimHeaders), hasItems(metric.getTitle()));

        assertThat(result.getData(), is(new DataList(asList(
                new DataList(singletonList(new DataValue("41"))),
                new DataList(singletonList(new DataValue("36")))))));
    }

    private static Collection<String> headerItemsNames(final List<ResultHeaderItem> items) {
        return items.stream().map(ResultHeaderItem::getName).collect(toList());
    }
}