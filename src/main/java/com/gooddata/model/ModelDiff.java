package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("projectModelDiff")
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({"updateOperations", "updateScripts"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDiff {

    private final List<UpdateOperation> updateOperations;

    private final List<UpdateScript> updateScripts;

    /**
     * @param updateOperations informal structured representation of the diff; suitable for presentation purposes
     * @param updateScripts
     */
    @JsonCreator
    public ModelDiff(@JsonProperty("updateOperations") List<UpdateOperation> updateOperations,
            @JsonProperty("updateScripts") List<UpdateScript> updateScripts) {

        this.updateOperations = updateOperations == null ? Collections.<UpdateOperation>emptyList() : updateOperations;
        this.updateScripts = updateScripts == null ? Collections.<UpdateScript>emptyList() : updateScripts;
    }

    /**
     * Returns informal structured representation of the diff. Suitable for presentation purposes.
     *
     * Returned list will be empty if there are no differences.
     */
    public List<UpdateOperation> getUpdateOperations() {
        return Collections.unmodifiableList(updateOperations);
    }

    /**
     * Returns several variants of MAQL DDL evolution scripts that should be executed to update the source model to the
     * target state. Individual variants differ in their side-effects (truncation of loaded data, drops of related objects...)
     *
     * Will be empty for empty diff.
     */
    public List<UpdateScript> getUpdateScripts() {
        return Collections.unmodifiableList(updateScripts);
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("updateOperation")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateOperation {
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("updateScript")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateScript {

        private List<String> maqlChunks;

        @JsonCreator
        public UpdateScript(@JsonProperty("maqlDdlChunks") List<String> maqlChunks) {
            this.maqlChunks = maqlChunks;
        }

        public List<String> getMaqlChunks() {
            return maqlChunks;
        }
    }
}
