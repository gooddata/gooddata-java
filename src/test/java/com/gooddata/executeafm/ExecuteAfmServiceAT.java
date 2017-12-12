/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.executeafm.afm.Afm;
import com.gooddata.executeafm.afm.AttributeItem;
import com.gooddata.executeafm.afm.MeasureItem;
import com.gooddata.executeafm.afm.SimpleMeasureDefinition;
import com.gooddata.executeafm.response.AttributeHeader;
import com.gooddata.executeafm.response.ExecutionResponse;
import com.gooddata.executeafm.response.MeasureGroupHeader;
import com.gooddata.executeafm.response.ResultDimension;
import com.gooddata.executeafm.result.DataList;
import com.gooddata.executeafm.result.DataValue;
import com.gooddata.executeafm.result.ExecutionResult;
import com.gooddata.executeafm.result.ResultHeaderItem;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public class ExecuteAfmServiceAT extends AbstractGoodDataAT {

    private ExecutionResponse response;

    @Test(dependsOnGroups = {"model", "md", "dataset"})
    public void testExecuteAfm() {
        final Execution execution = new Execution(new Afm()
                .addAttribute(new AttributeItem(new IdentifierObjQualifier(attr.getDefaultDisplayForm().getIdentifier()), "a1"))
                .addMeasure(new MeasureItem(new SimpleMeasureDefinition(new UriObjQualifier(metric.getUri())), "m1"))
        );

        response = gd.getExecuteAfmService().execute(project, execution);

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

    @Test(dependsOnMethods = "testExecuteAfm")
    public void testGetResult() {
        final ExecutionResult result = gd.getExecuteAfmService().getResult(response).get();

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