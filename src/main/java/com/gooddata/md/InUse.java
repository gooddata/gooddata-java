package com.gooddata.md;

import com.gooddata.util.BooleanStringDeserializer;
import com.gooddata.util.BooleanStringSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

import static com.gooddata.util.Validate.noNullElements;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.beans.Introspector.decapitalize;

/**
 * UsedBy/Using result
 */
@JsonTypeName("inUse")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class InUse {

    public static final String USEDBY_URI = "/gdc/md/{projectId}/usedby2";

    private final String uri;

    private final Set<String> types;

    private final boolean nearest;

    @JsonCreator
    InUse(@JsonProperty("uri") String uri,
          @JsonProperty("nearest") @JsonDeserialize(using = BooleanStringDeserializer.class) boolean nearest,
          @JsonProperty("types") Set<String> types) {

            this.uri = notEmpty(uri, "uri");
            this.types = types;
            this.nearest = nearest;
    }

    @SafeVarargs
    public InUse(String uri, boolean nearest, Class<? extends Obj>... type) {
        this.uri = notNull(uri, "uri");
        noNullElements(type, "type");
        this.types = new HashSet<>();
        for (Class<? extends Obj> t: type) {
            this.types.add(decapitalize(t.getSimpleName()));
        }
        this.nearest = nearest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InUse inUse = (InUse) o;

        if (nearest != inUse.nearest) return false;
        if (!types.equals(inUse.types)) return false;
        if (!uri.equals(inUse.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + types.hashCode();
        result = 31 * result + (nearest ? 1 : 0);
        return result;
    }

    public String getUri() {
        return uri;
    }

    public Set<String> getTypes() {
        return types;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isNearest() {
        return nearest;
    }
}
