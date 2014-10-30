package com.gooddata.md;

import com.gooddata.util.GDDateTimeDeserializer;
import com.gooddata.util.GDDateTimeSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

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
    private final DateTime created;
    private final DateTime updated;
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
                 @JsonProperty("created") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime created,
                 @JsonProperty("updated") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime updated,
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

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getCreated() {
        return created;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getUpdated() {
        return updated;
    }

    public Integer getLocked() {
        return locked;
    }

    public Integer getUnlisted() {
        return unlisted;
    }
}
