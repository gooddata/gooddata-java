package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Metadata entry (can be named "LINK" in some API docs)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Entry {

    private final String link;
    private final String title;
    private final String summary;
    private final String category;
    private final String author;
    private final String contributor;
    private final String deprecated; //TODO boolean
    private final String identifier;
    private final String tags; //TODO collection
    private final String created; //TODO date time
    private final String updated; //TODO date time
    private final Integer locked; //TODO boolean
    private final Integer unlisted; //TODO boolean

    @JsonCreator
    public Entry(@JsonProperty("link") String link,
                 @JsonProperty("title") String title,
                 @JsonProperty("summary") String summary,
                 @JsonProperty("category") String category,
                 @JsonProperty("author") String author,
                 @JsonProperty("contributor") String contributor,
                 @JsonProperty("deprecated") String deprecated,
                 @JsonProperty("identifier") String identifier,
                 @JsonProperty("tags") String tags,
                 @JsonProperty("created") String created,
                 @JsonProperty("updated") String updated,
                 @JsonProperty("locked") Integer locked,
                 @JsonProperty("unlisted") Integer unlisted) {
        this.link = link;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.author = author;
        this.contributor = contributor;
        this.deprecated = deprecated;
        this.identifier = identifier;
        this.tags = tags;
        this.created = created;
        this.updated = updated;
        this.locked = locked;
        this.unlisted = unlisted;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getContributor() {
        return contributor;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTags() {
        return tags;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public Integer getLocked() {
        return locked;
    }

    public Integer getUnlisted() {
        return unlisted;
    }
}
