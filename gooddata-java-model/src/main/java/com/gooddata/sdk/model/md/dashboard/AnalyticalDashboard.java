/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.md.Queryable;
import com.gooddata.sdk.model.md.Updatable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Represents analytical dashboard configuration.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("analyticalDashboard")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticalDashboard extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = -2217112261771452747L;

    private final Content content;

    /**
     * Constructor.
     *
     * @param title             dashboard title
     * @param widgetUris        URIs of widgets located on dashboard
     * @param filtersContextUri URI of filters context applied to this dashboard (optional)
     */
    public AnalyticalDashboard(final String title, final List<String> widgetUris, final String filtersContextUri) {
        this(new Meta(title), new Content(notNull(widgetUris, "widgetUris"), filtersContextUri));
    }

    @JsonCreator
    private AnalyticalDashboard(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    @JsonProperty
    private Content getContent() {
        return content;
    }

    /**
     * @return URIs of widgets contained in this dashboard
     */
    @JsonIgnore
    public List<String> getWidgetUris() {
        return Collections.unmodifiableList(getContent().getWidgets());
    }

    /**
     * @return URI of filters context applied to this dashboard
     */
    @JsonIgnore
    public String getFiltersContextUri() {
        return getContent().getFilterContext();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content implements Serializable {

        private static final long serialVersionUID = 3492968666803382202L;

        private final List<String> widgets;
        private final String filterContext;

        @JsonCreator
        private Content(
                @JsonProperty("widgets") final List<String> widgets,
                @JsonProperty("filterContext") final String filterContext) {
            this.widgets = widgets;
            this.filterContext = filterContext;
        }

        @JsonProperty
        private List<String> getWidgets() {
            return widgets;
        }

        @JsonProperty
        private String getFilterContext() {
            return filterContext;
        }
    }
}
