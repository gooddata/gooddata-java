/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.lcm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.PageSerializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * List of {@link LcmEntity}.
 */
@JsonDeserialize(using = LcmEntities.Deserializer.class)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(LcmEntities.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = LcmEntities.Serializer.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LcmEntities extends Page<LcmEntity> {

    public static final String URI = "/gdc/account/profile/{profileId}/lcmEntities";

    static final String ROOT_NODE = "lcmEntities";

    public LcmEntities(final List<LcmEntity> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }

    LcmEntities(final LcmEntity... lcmEntities) {
        this(asList(lcmEntities), null, null);
    }

    static class Serializer extends PageSerializer {
        public Serializer() {
            super(ROOT_NODE);
        }
    }

    static class Deserializer extends PageDeserializer<LcmEntities, LcmEntity> {
        protected Deserializer() {
            super(LcmEntity.class);
        }

        @Override
        protected LcmEntities createPage(final List<LcmEntity> items, final Paging paging, final Map<String, String> links) {
            return new LcmEntities(items, paging, links);
        }
    }
}
