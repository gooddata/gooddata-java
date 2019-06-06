/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import static com.gooddata.util.Validate.notNull;

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
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Class encapsulates list of filters on analytical dashboard.
 * Currently can contain {@link DashboardDateFilter}s and {@link DashboardAttributeFilter}s.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(DashboardFilterContext.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardFilterContext extends AbstractObj implements Updatable, Queryable {

    private static final long serialVersionUID = -4572881756272497057L;

    static final String NAME = "filterContext";

    private final Content content;

    /**
     * Constructs new dashboard filter context with given filters.
     *
     * @param filters list of dashboard filters (can be empty)
     */
    public DashboardFilterContext(final List<DashboardFilter> filters) {
        this(new Meta(NAME), new Content(notNull(filters, "filters")));
    }

    @JsonCreator
    private DashboardFilterContext(@JsonProperty("meta") final Meta meta, @JsonProperty("content") final Content content) {
        super(meta);
        this.content = content;
    }

    /**
     * @return all dashboard filters from this filter context
     */
    @JsonIgnore
    public List<DashboardFilter> getFilters() {
        return Collections.unmodifiableList(getContent().getFilters());
    }

    @JsonProperty
    private Content getContent() {
        return content;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content implements Serializable {

        private final List<DashboardFilter> filters;

        @JsonCreator
        private Content(@JsonProperty("filters") final List<DashboardFilter> filters) {
            this.filters = filters;
        }

        @JsonProperty
        private List<DashboardFilter> getFilters() {
            return filters;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.toString(this);
        }
    }
}
