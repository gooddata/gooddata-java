/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Model diff including MAQL DDL.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("projectModelDiff")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDiff {

    private final List<UpdateScript> updateScripts;

    /**
     * @param updateScripts several variants of MAQL DDL evolution scripts that should be executed to update
     *                      the source model to the target state
     */
    @JsonCreator
    ModelDiff(@JsonProperty("updateScripts") List<UpdateScript> updateScripts) {
        this.updateScripts = updateScripts == null ? Collections.<UpdateScript>emptyList() : updateScripts;
    }

    /**
     * Returns several variants of MAQL DDL evolution scripts that should be executed to update the source model to the
     * target state. Individual variants differ in their side-effects (truncation of loaded data, drops of related objects...)
     * <p/>
     * Will be empty for empty diff.
     */
    List<UpdateScript> getUpdateScripts() {
        return unmodifiableList(updateScripts);
    }

    /**
     * Returns MAQL DDL update script that should be executed to update the source model to the
     * target state.
     * <p/>
     * It picks the best possible variant by it's side-effects (truncation
     * of loaded data, drops of related objects). It returns first present variant by order:
     * <ol>
     * <li>preserve data + no cascade drops</li>
     * <li>no preserve data + no cascade drops</li>
     * <li>preserve data + cascade drops</li>
     * <li>no preserve data + cascade drops</li>
     * </ol>
     * <p/>
     * Returned list will be empty if there are no differences.
     *
     * @return MAQL DDL update scripts. Empty list if there are no differences.
     */
    public List<String> getUpdateMaql() {
        if (updateScripts.isEmpty()) {
            return emptyList();
        }

        UpdateScript chosenScript;
        if ((chosenScript = getUpdateScriptByFlags(true, false)) != null) {
            return unmodifiableList(chosenScript.getMaqlChunks());
        }
        if ((chosenScript = getUpdateScriptByFlags(false, false)) != null) {
            return unmodifiableList(chosenScript.getMaqlChunks());
        }
        if ((chosenScript = getUpdateScriptByFlags(true, true)) != null) {
            return unmodifiableList(chosenScript.getMaqlChunks());
        }

        return unmodifiableList(updateScripts.get(0).getMaqlChunks());
    }

    /**
     * Returns MAQL DDL update script by given flags determining side-effects (truncation of loaded data,
     * drops of related objects).
     *
     * @param preserveData true to preserve data, false to truncate
     * @param cascadeDrops true to drop all related objects
     * @return update script with requested side-effect flags
     */
    public UpdateScript getUpdateScriptByFlags(final boolean preserveData, final boolean cascadeDrops) {
        UpdateScript result = null;
        for (final UpdateScript script : updateScripts) {
            if (script.isPreserveData() == preserveData && script.isCascadeDrops() == cascadeDrops) {
                result = script;
            }
        }
        return result;
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("updateScript")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class UpdateScript {

        private List<String> maqlChunks;
        private Boolean preserveData;
        private Boolean cascadeDrops;

        @JsonCreator
        UpdateScript(@JsonProperty("maqlDdlChunks") List<String> maqlChunks,
                     @JsonProperty("preserveData") Boolean preserveData,
                     @JsonProperty("cascadeDrops") Boolean cascadeDrops) {
            this.maqlChunks = maqlChunks == null ? Collections.<String>emptyList() : maqlChunks;
            this.preserveData = preserveData;
            this.cascadeDrops = cascadeDrops;
        }

        public List<String> getMaqlChunks() {
            return maqlChunks;
        }

        public Boolean isPreserveData() {
            return preserveData;
        }

        public Boolean isCascadeDrops() {
            return cascadeDrops;
        }
    }
}
