/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
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
     * @param updateScripts several variants of MAQL DDL evolution scripts that should be executed to update
     *                      the source model to the target state
     */
    ModelDiff(UpdateScript... updateScripts) {
        this(asList(updateScripts));
    }

    /**
     * Returns several variants of MAQL DDL evolution scripts that should be executed to update the source model to the
     * target state. Individual variants differ in their side-effects (truncation of loaded data, drops of related objects...)
     * <p>
     * Will be empty for empty diff.
     */
    List<UpdateScript> getUpdateScripts() {
        return unmodifiableList(updateScripts);
    }

    /**
     * Returns MAQL DDL update script that should be executed to update the source model to the
     * target state.
     * <p>
     * It picks the best possible variant by it's side-effects (truncation
     * of loaded data, drops of related objects). It returns first present variant by order:
     * <ol>
     * <li>preserve data + no cascade drops</li>
     * <li>no preserve data + no cascade drops</li>
     * <li>preserve data + cascade drops</li>
     * <li>no preserve data + cascade drops</li>
     * </ol>
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

    private UpdateScript getUpdateScriptByFlags(final boolean preserveData, final boolean cascadeDrops) {
        UpdateScript result = null;
        for (final UpdateScript script : updateScripts) {
            if (script.isPreserveData() == preserveData && script.isCascadeDrops() == cascadeDrops) {
                result = script;
            }
        }
        return result;
    }

    /**
     * Set of MAQL DDL scripts with one variant of side-effects (truncation of loaded data, drops of related objects...).
     */
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("updateScript")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class UpdateScript {

        private final List<String> maqlChunks;
        private final Boolean preserveData;
        private final Boolean cascadeDrops;

        /**
         * Create set of MAQL DDL scripts with one variant of side-effects (truncation of loaded data, drops of related objects...).
         *
         * @param preserveData true if data should be preserved, false if they should be truncated
         * @param cascadeDrops true if related objects should be also dropped, false if not
         * @param maqlChunks MAQL strings
         */
        @JsonCreator
        UpdateScript(@JsonProperty("preserveData") Boolean preserveData,
                     @JsonProperty("cascadeDrops") Boolean cascadeDrops,
                     @JsonProperty("maqlDdlChunks") List<String> maqlChunks) {
            this.preserveData = preserveData;
            this.cascadeDrops = cascadeDrops;
            this.maqlChunks = maqlChunks == null ? Collections.<String>emptyList() : maqlChunks;
        }

        /**
         * Create set of MAQL DDL scripts with one variant of side-effects (truncation of loaded data, drops of related objects...).
         *
         * @param preserveData true if data should be preserved, false if they should be truncated
         * @param cascadeDrops true if related objects should be also dropped, false if not
         * @param maqlChunks MAQL strings
         */
        UpdateScript(boolean preserveData, boolean cascadeDrops, String... maqlChunks) {
            this(preserveData, cascadeDrops, asList(maqlChunks));
        }

        /**
         * Returns MAQL strings.
         *
         * @return MAQL strings
         */
        public List<String> getMaqlChunks() {
            return maqlChunks;
        }

        /**
         * Returns true if data should be preserved, false if they should be truncated.
         *
         * @return true if data should be preserved, false if they should be truncated
         */
        public Boolean isPreserveData() {
            return preserveData;
        }

        /**
         * Returns true if related objects should be also dropped, false if not
         *
         * @return true if related objects should be also dropped, false if not
         */
        public Boolean isCascadeDrops() {
            return cascadeDrops;
        }
    }
}
