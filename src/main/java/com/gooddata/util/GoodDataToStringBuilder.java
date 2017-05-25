/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * GoodDataToStringBuilder enables to create string representation based on fields of an object and exclude specified fields.
 * links field is excluded by default.
 */
public class GoodDataToStringBuilder extends ReflectionToStringBuilder {

    private static final String LINKS_FIELD_NAME = "links";

    public GoodDataToStringBuilder(Object object) {
        super(object, RecursiveToStringStyle.SHORT_PREFIX_STYLE);
        setExcludeFieldNames(LINKS_FIELD_NAME);
    }

    /**
     * creates String representation of an object using reflection to get fields of an object.
     * links field of an object is ignored by default, allows to specify more fields to be excluded
     *
     * @param object to create String representation for
     * @param excludeFieldNames name of fields of an object to be excluded from String representation
     * @return String representation, which excludes links field by default
     */
    public static String defaultToString(Object object, String... excludeFieldNames) {
        return new GoodDataToStringBuilder(object).setExcludeFieldNames(ArrayUtils.add(excludeFieldNames, LINKS_FIELD_NAME))
                                                  .toString();
    }
}
