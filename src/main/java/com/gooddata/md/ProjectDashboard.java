/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.Objects;

/**
 * Project Dashboard of GoodData project.<br>
 * Deserialization only. This object is not complete representation of real 'projectDashboard' object.
 * It contains only dashboard tabs basic definitions (identifier and name).
 */
@JsonTypeName("projectDashboard")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDashboard extends AbstractObj implements Queryable {

    private final Content content;

    private ProjectDashboard(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = notNull(content, "content");
    }

    @JsonIgnore
    public Collection<Tab> getTabs() {
        return content.tabs;
    }

    /**
     * Returns dashboard tab with the given tab name.
     * If tab with such name doesn't exist, returns {@code null}.
     *
     * @param name tab name
     * @return
     * <ul>
     *     <li>dashboard tab with the given name</li>
     *     <li>{@code null} if tab doesn't exist</li>
     * </ul>
     */
    @JsonIgnore
    public Tab getTabByName(String name) {
        for (Tab tab : getTabs()) {
            if (Objects.equals(tab.title, name)) {
                return tab;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content {

        private final Collection<Tab> tabs;

        @JsonCreator
        private Content(@JsonProperty("tabs") Collection<Tab> tabs) {
            this.tabs = tabs;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.toStringExclude(this);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tab {

        private final String identifier;
        private final String title;

        @JsonCreator
        private Tab(@JsonProperty("identifier") String identifier, @JsonProperty("title") String title) {
            this.identifier = identifier;
            this.title = title;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.toStringExclude(this);
        }
    }
}
