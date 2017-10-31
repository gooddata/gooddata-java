/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;

import static com.gooddata.util.Validate.notEmpty;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.toObject;

/**
 * Paging of {@link ExecutionResult}. Represents paging in multiple dimensions.
 */
public class Paging {
    private List<Integer> size;
    private List<Integer> offset;
    private List<Integer> total;

    /**
     * Creates new paging
     */
    public Paging() {
    }

    /**
     * Creates new paging
     * @param size multiple dimensions size
     * @param offset multiple dimensions offset
     * @param total multiple dimensions total
     */
    @JsonCreator
    public Paging(@JsonProperty("size") final List<Integer> size,
                  @JsonProperty("offset") final List<Integer> offset,
                  @JsonProperty("total") final List<Integer> total) {
        this.size = notEmpty(size, "size");
        this.offset = notEmpty(offset, "offset");
        this.total = notEmpty(total, "total");
    }

    /**
     * @return multiple dimensions size
     */
    public List<Integer> getSize() {
        return size;
    }

    /**
     * @return multiple dimensions offset
     */
    public List<Integer> getOffset() {
        return offset;
    }

    /**
     * @return multiple dimensions total
     */
    public List<Integer> getTotal() {
        return total;
    }

    /**
     * Sets size compound of given elements, each element per dimension
     * @param size size elements
     * @return this
     */
    public Paging size(final int... size) {
        this.size = asList(toObject(size));
        return this;
    }

    /**
     * Sets size compound of given elements, each element per dimension
     * @param total size elements
     * @return this
     */
    public Paging total(final int... total) {
        this.total = asList(toObject(total));
        return this;
    }

    /**
     * Sets size compound of given elements, each element per dimension
     * @param offset size elements
     * @return this
     */
    public Paging offset(final int... offset) {
        this.offset = asList(toObject(offset));
        return this;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
