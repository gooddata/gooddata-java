/*
 * (C) 2025 GoodData Corporation.
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
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.md.Queryable;
import com.gooddata.sdk.model.md.Updatable;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Class for holding information about visualization, including uri to its implementation, icons, order index and checksum
 */
@JsonTypeName(VisualizationClass.NAME)
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisualizationClass extends AbstractObj implements Queryable, Updatable {

    static final String NAME = "visualizationClass";
    private static final long serialVersionUID = -72785788784079208L;
    private Content content;

    private VisualizationClass(@JsonProperty("content") final Content content, @JsonProperty("meta") final Meta meta) {
        super(meta);
        this.content = notNull(content);
    }

    /**
     * @return default icon for visualization, for local visualizations in form of 'local:{@link VisualizationType}'
     */
    @JsonIgnore
    public String getIcon() {
        return getContent().getIcon();
    }

    /**
     * @return icon displayed on mouse over, for local visualizations in form of 'local:{@link VisualizationType}.selected'
     */
    @JsonIgnore
    public String getIconSelected() {
        return getContent().getIconSelected();
    }

    /**
     * @return checksum of visualization class, 'local' for local visualizations
     */
    @JsonIgnore
    public String getChecksum() {
        return getContent().getChecksum();
    }

    /**
     * @return absolute position of icon in list
     */
    @JsonIgnore
    public Float getOrderIndex() {
        return getContent().getOrderIndex();
    }

    /**
     * @return uri to implementation of visualization, for local visualizations in form of 'local:{@link VisualizationType}'
     */
    @JsonIgnore
    public String getUrl() {
        return getContent().getUrl();
    }

    /**
     * @return type of visualization, table by default
     */
    @JsonIgnore
    public VisualizationType getVisualizationType() {
        VisualizationType visualizationType = VisualizationType.TABLE;

        String uriParts[] = getContent().getUrl().split(":");

        if (uriParts.length > 0 && isLocal()) {
            String derivedType = uriParts[uriParts.length - 1];

            visualizationType = VisualizationType.of(derivedType);
        }

        return visualizationType;
    }

    @JsonIgnore
    private boolean isLocal() {
        return getContent().getChecksum().equals("local");
    }

    private Content getContent() {
        return content;
    }

    private static class Content implements Serializable {
        private final String url;
        private final String icon;
        private final String iconSelected;
        private final String checksum;
        private final Float orderIndex;

        @JsonCreator
        private Content(@JsonProperty("url") String url,
                        @JsonProperty("icon") String icon,
                        @JsonProperty("iconSelected") String iconSelected,
                        @JsonProperty("checksum") String checksum,
                        @JsonProperty("orderIndex") Float orderIndex) {
            this.url = notNull(url);
            this.icon = notNull(icon);
            this.iconSelected = notNull(iconSelected);
            this.checksum = notNull(checksum);
            this.orderIndex = orderIndex;
        }

        public String getUrl() {
            return url;
        }

        public String getIcon() {
            return icon;
        }

        public String getIconSelected() {
            return iconSelected;
        }

        public String getChecksum() {
            return checksum;
        }

        public Float getOrderIndex() {
            return orderIndex;
        }
    }
}
