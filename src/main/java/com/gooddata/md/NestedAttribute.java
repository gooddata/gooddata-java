/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

/**
 * Attribute representation which is nested in some other metadata object (i.e. within {@link Dimension}).
 * Can't be queried, get or updated directly - use {@link Attribute} for these operations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedAttribute extends AbstractObj {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    protected NestedAttribute(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    @JsonIgnore
    public Collection<DisplayForm> getDisplayForms() {
        return content.getDisplayForms();
    }

    @JsonIgnore
    public Collection<Key> getPrimaryKeys() {
        return content.getPk();
    }

    @JsonIgnore
    public Collection<Key> getForeignKeys() {
        return content.getFk();
    }

    @JsonIgnore
    public DisplayForm getDefaultDisplayForm() {
        return getDisplayForms().iterator().next();
    }

    /**
     * @return dimension URI string
     * @deprecated use {@link #getDimensionUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getDimensionLink() {
        return getDimensionUri();
    }

    @JsonIgnore
    public String getDimensionUri() {
        return content.getDimensionUri();
    }

    public boolean hasDimension() {
        return getDimensionUri() != null;
    }

    @JsonIgnore
    public Collection<String> getRelations() {
        return content.getRelations();
    }

    @JsonIgnore
    public String getDirection() {
        return content.getDirection();
    }

    /**
     * @return sort setting - pk, byUsedDF or uri linking some display form, null if not set
     * @see #isSortedByLinkedDf()
     * @see #isSortedByUsedDf()
     * @see #isSortedByPk()
     */
    @JsonIgnore
    public String getSort() {
        return content.getSort() != null ? content.getSort().getValue() : null;
    }

    /**
     * @return true when the sort is set and it is a link to display form, false otherwise
     */
    @JsonIgnore
    public boolean isSortedByLinkedDf() {
        return content.getSort() != null && content.getSort().isLinkType();
    }

    /**
     * @return true when the sort is set to byUsedDF (used display form), false otherwise
     */
    @JsonIgnore
    public boolean isSortedByUsedDf() {
        return content.getSort() != null && AttributeSort.BY_USED_DF.equals(content.getSort().getValue());
    }

    /**
     * @return true when the sort is set to pk (primary key), false otherwise
     */
    @JsonIgnore
    public boolean isSortedByPk() {
        return content.getSort() != null && AttributeSort.PK.equals(content.getSort().getValue());
    }

    @JsonIgnore
    public String getType() {
        return content.getType();
    }

    @JsonIgnore
    public Collection<String> getCompositeAttribute() {
        return content.getCompositeAttribute();
    }

    @JsonIgnore
    public Collection<String> getCompositeAttributePk() {
        return content.getCompositeAttributePk();
    }

    /**
     * @return drill-down step display form URI string
     * @deprecated use {@link #getDrillDownStepDisplayFormUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getDrillDownStepDisplayFormLink() {
        return getDrillDownStepDisplayFormUri();
    }

    @JsonIgnore
    public String getDrillDownStepDisplayFormUri() {
        return content.getDrillDownStepDisplayFormUri();
    }

    /**
     * @return linked display form URI string
     * @deprecated use {@link #getLinkedDisplayFormUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getLinkedDisplayFormLink() {
        return getLinkedDisplayFormUri();
    }

    @JsonIgnore
    public String getLinkedDisplayFormUri() {
        return content.getLinkedDisplayFormUri();
    }

    /**
     * URIs of folders containing this object
     * @return collection of URIs or null
     */
    @JsonIgnore
    public Collection<String> getFolders() {
        return content.getFolders();
    }

    @JsonIgnore
    public Collection<String> getGrain() {
        return content.getGrain();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected static class Content {
        private final Collection<Key> pk;
        private final Collection<Key> fk;
        private final Collection<DisplayForm> displayForms;
        private final String dimension;
        private final String direction;
        private final AttributeSort sort;
        private final String type;
        private final Collection<String> rel;
        private final Collection<String> compositeAttribute;
        private final Collection<String> compositeAttributePk;
        private final String drillDownStepAttributeDF;
        private final String linkedDisplayFormUri;
        private final Collection<String> folders;
        private final Collection<String> grain;

        @JsonCreator
        protected Content(@JsonProperty("pk") Collection<Key> pk,
                          @JsonProperty("fk") Collection<Key> fk,
                          @JsonProperty("displayForms") Collection<DisplayForm> displayForms,
                          @JsonProperty("dimension") String dimension,
                          @JsonProperty("direction") String direction,
                          @JsonProperty("sort") AttributeSort sort,
                          @JsonProperty("type") String type,
                          @JsonProperty("rel") Collection<String> rel,
                          @JsonProperty("compositeAttribute") Collection<String> compositeAttribute,
                          @JsonProperty("compositeAttributePk") Collection<String> compositeAttributePk,
                          @JsonProperty("drillDownStepAttributeDF") String drillDownStepAttributeDF,
                          @JsonProperty("linkAttributeDF") String linkedDisplayFormUri,
                          @JsonProperty("folders") Collection<String> folders,
                          @JsonProperty("grain") Collection<String> grain) {
            this.pk = pk;
            this.fk = fk;
            this.displayForms = displayForms;
            this.dimension = dimension;
            this.direction = direction;
            this.sort = sort;
            this.type = type;
            this.rel = rel;
            this.compositeAttribute = compositeAttribute;
            this.compositeAttributePk = compositeAttributePk;
            this.drillDownStepAttributeDF = drillDownStepAttributeDF;
            this.linkedDisplayFormUri = linkedDisplayFormUri;
            this.folders = folders;
            this.grain = grain;
        }

        public Collection<Key> getPk() {
            return pk;
        }

        public Collection<Key> getFk() {
            return fk;
        }

        public Collection<DisplayForm> getDisplayForms() {
            return displayForms;
        }

        /**
         * @return dimension URI string
         * @deprecated use {@link #getDimensionUri()} instead
         */
        @Deprecated
        @JsonIgnore
        public String getDimensionLink() {
            return getDimensionUri();
        }

        @JsonProperty("dimension")
        public String getDimensionUri() {
            return dimension;
        }

        public String getDirection() {
            return direction;
        }

        public AttributeSort getSort() {
            return sort;
        }

        public String getType() {
            return type;
        }

        @JsonProperty("rel")
        public Collection<String> getRelations() {
            return rel;
        }

        public Collection<String> getCompositeAttribute() {
            return compositeAttribute;
        }

        public Collection<String> getCompositeAttributePk() {
            return compositeAttributePk;
        }

        /**
         * @return drill-down step display form URI string
         * @deprecated use {@link #getDrillDownStepDisplayFormUri()} instead
         */
        @Deprecated
        @JsonIgnore
        public String getDrillDownStepDisplayFormLink() {
            return getDrillDownStepDisplayFormUri();
        }

        @JsonProperty("drillDownStepAttributeDF")
        public String getDrillDownStepDisplayFormUri() {
            return drillDownStepAttributeDF;
        }

        /**
         * @return linked display form URI string
         * @deprecated use {@link #getLinkedDisplayFormUri()} instead
         */
        @Deprecated
        @JsonIgnore
        public String getLinkedDisplayFormLink() {
            return getLinkedDisplayFormUri();
        }

        @JsonProperty("linkAttributeDF")
        public String getLinkedDisplayFormUri() {
            return linkedDisplayFormUri;
        }

        public Collection<String> getFolders() {
            return folders;
        }

        public Collection<String> getGrain() {
            return grain;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

}
