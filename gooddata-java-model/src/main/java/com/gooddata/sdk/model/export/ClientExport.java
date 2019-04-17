/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

@JsonTypeName("clientExport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientExport {

    private static final String DASHBOARD_EXPORT_URI = "/dashboard.html#project=%s&dashboard=%s&tab=%s&export=1";

    private final String url;
    private final String name;

    ClientExport(final String url, final String name) {
        this.url = notEmpty(url, "url");
        this.name = notEmpty(name, "name");
    }

    public ClientExport(final String goodDataEndpointUri, final String projectUri, final String dashboardUri,
                        final String tabId) {
        this(notEmpty(goodDataEndpointUri, "goodDataEndpointUri") +
                        String.format(DASHBOARD_EXPORT_URI,
                                        notNull(projectUri, "projectUri"),
                                        notNull(dashboardUri, "dashboardUri"),
                                        notNull(tabId, "tabId")),
                "export.pdf");
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
