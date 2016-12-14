/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Collection;

/**
 * GoodDataToStringBuilder enables to create string representation based on fields of an object and exclude specified fields
 * links field is excluded by default.
 */
public class GoodDataToStringBuilder extends ReflectionToStringBuilder {

    private static final String LINKS_FIELD_NAME = "links";

    public GoodDataToStringBuilder(Object object) {
        super(object, RecursiveToStringStyle.SHORT_PREFIX_STYLE);
        setExcludeFieldNames(LINKS_FIELD_NAME);
    }

    /**
     * replaces implementation of {@link ReflectionToStringBuilder#toStringExclude(Object, Collection)},
     * adds default exclusion of links field
     *
     * @param object
     * @param excludeFieldNames
     * @return
     */
    public static String toStringExclude(Object object, Collection<String> excludeFieldNames) {
        return new GoodDataToStringBuilder(object).setExcludeFieldNames(ArrayUtils.add((String[]) excludeFieldNames.toArray(), LINKS_FIELD_NAME))
                                                  .toString();
    }

    /**
     * replaces implementation of {@link ReflectionToStringBuilder#toStringExclude(Object, String...)},
     * adds default exclusion of links field
     *
     * @param object
     * @param excludeFieldNames
     * @return
     */
    public static String toStringExclude(Object object, String... excludeFieldNames) {
        return new GoodDataToStringBuilder(object).setExcludeFieldNames(ArrayUtils.add(excludeFieldNames, LINKS_FIELD_NAME))
                                                  .toString();
    }
}
