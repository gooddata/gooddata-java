/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.GoodDataEndpoint;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

@JsonTypeName("clientExport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ClientExport {

    private static final String DASHBOARD_EXPORT_URI = "/dashboard.html#project={projectUri}&dashboard={dashboardUri}&tab={tabId}&export=1";
    private static final UriTemplate DASHBOARD_EXPORT_TEMPLATE = new UriTemplate(DASHBOARD_EXPORT_URI);

    private final String url;
    private final String name;

    ClientExport(final String url, final String name) {
        this.url = notEmpty(url, "url");
        this.name = notEmpty(name, "name");
    }

    ClientExport(final GoodDataEndpoint endpoint, final String projectUri, final String dashboardUri, final String tabId) {
        this(notNull(endpoint, "endpoint").toUri() +
                        DASHBOARD_EXPORT_TEMPLATE
                                .expand(
                                        notNull(projectUri, "projectUri"),
                                        notNull(dashboardUri, "dashboardUri"),
                                        notNull(tabId, "tabId")
                                ).toString(),
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
