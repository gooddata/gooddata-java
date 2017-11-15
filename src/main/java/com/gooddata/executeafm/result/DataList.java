/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.gooddata.util.GoodDataToStringBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gooddata.util.Validate.notNull;
import static java.util.stream.Collectors.toList;

/**
 * List value type of {@link Data}, basically wrapper for list of nested {@link Data}
 */
public class DataList extends ArrayList<Data> implements Data {

    /**
     * Creates new instance of given list of data
     * @param values list to use as values, can't be null
     */
    public DataList(final List<Data> values) {
        super(notNull(values, "values"));
    }

    /**
     * Creates new instance by transforming the given array to list of simple or null values
     * @param array array of values
     */
    DataList(final String[] array) {
        this(Arrays.stream(array).map(DataList::simpleValue).collect(toList()));
    }

    /**
     * Creates new instance by transforming the given array to list of data lists of simple or null values
     * @param array array of values
     */
    DataList(final String[][] array) {
        this(Arrays.stream(array).map(DataList::new).collect(toList()));
    }

    private static Data simpleValue(final String value) {
        return value == null ? Data.NULL : new DataValue(value);
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public String textValue() {
        throw new UnsupportedOperationException("DataList doesn't contain text value");
    }

    @Override
    public List<Data> asList() {
        return this;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
