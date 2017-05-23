/**
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.projecttemplate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.util.BooleanDeserializer;

import java.util.List;
import java.util.Objects;

/**
 * Represents one project template.
 * Deserialization only.
 */
@JsonTypeName("projectTemplate")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Template {

    private final String uri;
    private final String urn;
    private final String version;
    private final Boolean hidden;
    private final Content content;
    private final Meta meta;

    @JsonCreator
    private Template(@JsonProperty("link") String uri, @JsonProperty("urn") String urn, @JsonProperty("version") String version,
                    @JsonProperty("hidden") @JsonDeserialize(using = BooleanDeserializer.class) Boolean hidden,
                    @JsonProperty("content") Content content, @JsonProperty("meta") Meta meta) {
        this.uri = uri;
        this.urn = urn;
        this.version = version;
        this.hidden = hidden;
        this.content = content;
        this.meta = meta;
    }

    public String getUri() {
        return uri;
    }

    public String getUrn() {
        return urn;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public String getMdDefinitionUri() {
        return content != null ? content.getMdDefinitionUri() : null;
    }

    public String getDwDefinitionUri() {
        return content != null ? content.getDwDefinitionUri() : null;
    }

    public String getConfigUri() {
        return content != null ? content.getConfigUri() : null;
    }

    public List<String> getManifestsUris() {
        return content != null ? content.getManifestsUris() : null;
    }

    public Meta getMeta() {
        return meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return Objects.equals(uri, template.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content {
        private final String mdDefinitionUri;
        private final String dwDefinitionUri;
        private final String configUri;
        private final List<String> manifestsUris;

        @JsonCreator
        private Content(@JsonProperty("MDDefinition") String mdDefinitionUri, @JsonProperty("DWDefinition") String dwDefinitionUri,
                        @JsonProperty("config") String configUri, @JsonProperty("manifests") List<String> manifestsUris) {
            this.mdDefinitionUri = mdDefinitionUri;
            this.dwDefinitionUri = dwDefinitionUri;
            this.configUri = configUri;
            this.manifestsUris = manifestsUris;
        }

        public String getMdDefinitionUri() {
            return mdDefinitionUri;
        }

        public String getDwDefinitionUri() {
            return dwDefinitionUri;
        }

        public String getConfigUri() {
            return configUri;
        }

        public List<String> getManifestsUris() {
            return manifestsUris;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Meta {
        private final String summary;
        private final String title;
        private final String category;
        private final String apiVersion;
        private final Author author;

        @JsonCreator
        private Meta(@JsonProperty("summary") String summary, @JsonProperty("title") String title, @JsonProperty("category") String category,
                     @JsonProperty("apiVersion") String apiVersion, @JsonProperty("author") Author author) {
            this.summary = summary;
            this.title = title;
            this.category = category;
            this.apiVersion = apiVersion;
            this.author = author;
        }

        public String getSummary() {
            return summary;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public String getApiVersion() {
            return apiVersion;
        }

        public Author getAuthor() {
            return author;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Author {
        private final String name;
        private final String uri;

        @JsonCreator
        private Author(@JsonProperty("name") String name, @JsonProperty("uri") String uri) {
            this.name = name;
            this.uri = uri;
        }

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }
    }


}
