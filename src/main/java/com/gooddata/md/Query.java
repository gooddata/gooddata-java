package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

/**
 * Metadata query result
 */
@JsonTypeName("query")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Query {

    public static final String URI = "/gdc/md/{projectId}/query/{type}";

    private final Collection<Entry> entries;

    private final Meta meta;

    @JsonCreator
    private Query(@JsonProperty("entries") Collection<Entry> entries, @JsonProperty("meta") Meta meta) {
        this.entries = entries;
        this.meta = meta;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    public String getCategory() {
        return meta.getCategory();
    }

    public String getSummary() {
        return meta.getSummary();
    }

    public String getTitle() {
        return meta.getTitle();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class Meta {
        private final String category;
        private final String summary;
        private final String title;

        @JsonCreator
        public Meta(@JsonProperty("category") String category, @JsonProperty("summary") String summary,
                    @JsonProperty("title") String title) {
            this.category = category;
            this.summary = summary;
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public String getSummary() {
            return summary;
        }

        public String getTitle() {
            return title;
        }
    }

}
