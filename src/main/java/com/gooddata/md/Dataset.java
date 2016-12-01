/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static com.gooddata.util.Validate.notNullState;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.BooleanStringSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents metadata dataset
 */
@JsonTypeName("dataSet")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dataset extends AbstractObj implements Queryable, Updatable {

    private static final String DATA_UPLOADS_LINK = "dataUploads";
    private static final String UPLOAD_CONFIGURATION_LINK = "uploadConfiguration";

    @JsonProperty("content")
    private final Content content;

    @JsonProperty("links")
    private final Map<String, String> links;

    @JsonCreator
    private Dataset(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content,
            @JsonProperty("links") Map<String, String> links) {
        super(meta);
        this.content = content;
        this.links = links;
    }

    /* Just for serialization test */
    Dataset(String title) {
        this(new Meta(title), new Content(Collections.<String>emptyList(), null, Collections.<String>emptyList(), Collections.<String>emptyList(),
                Collections.<String>emptyList(), false), null);
    }

    @JsonIgnore
    public List<String> getTies() {
        return content.getTies();
    }

    @JsonIgnore
    public String getMode() {
        return content.getMode();
    }

    @JsonIgnore
    public List<String> getFacts() {
        return content.getFacts();
    }

    @JsonIgnore
    public List<String> getDataLoadingColumns() {
        return content.getDataLoadingColumns();
    }

    @JsonIgnore
    public List<String> getAttributes() {
        return content.getAttributes();
    }

    @JsonIgnore
    public boolean hasUploadConfiguration() {
        return Boolean.TRUE.equals(content.hasUploadConfiguration());
    }

    /**
     * @return data uploads URI string
     * @deprecated use {@link #getDataUploadsUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getDataUploadsLink() {
        return getDataUploadsUri();
    }

    @JsonIgnore
    public String getDataUploadsUri() {
        return notNullState(links, "links").get(DATA_UPLOADS_LINK);
    }

    /**
     * @return upload configuration URI string
     * @deprecated use {@link #getUploadConfigurationUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getUploadConfigurationLink() {
        return getUploadConfigurationUri();
    }

    @JsonIgnore
    public String getUploadConfigurationUri() {
        return notNullState(links, "links").get(UPLOAD_CONFIGURATION_LINK);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content {
        private final List<String> ties;
        private final String mode;
        private final List<String> facts;
        private final List<String> dataLoadingColumns;
        private final List<String> attributes;
        private final Boolean hasUploadConfiguration;

        @JsonCreator
        private Content(@JsonProperty("ties") List<String> ties,
                @JsonProperty("mode") String mode,
                @JsonProperty("facts") List<String> facts,
                @JsonProperty("dataLoadingColumns") List<String> dataLoadingColumns,
                @JsonProperty("attributes") List<String> attributes,
                @JsonProperty("hasUploadConfiguration") @JsonDeserialize(using = BooleanDeserializer.class) Boolean hasUploadConfiguration) {
            this.ties = ties;
            this.mode = mode;
            this.facts = facts;
            this.dataLoadingColumns = dataLoadingColumns;
            this.attributes = attributes;
            this.hasUploadConfiguration = hasUploadConfiguration;
        }

        public List<String> getTies() {
            return ties;
        }

        public String getMode() {
            return mode;
        }

        public List<String> getFacts() {
            return facts;
        }

        public List<String> getDataLoadingColumns() {
            return dataLoadingColumns;
        }

        public List<String> getAttributes() {
            return attributes;
        }

        @JsonProperty("hasUploadConfiguration")
        @JsonSerialize(using = BooleanStringSerializer.class)
        public Boolean hasUploadConfiguration() {
            return hasUploadConfiguration;
        }
    }
}
