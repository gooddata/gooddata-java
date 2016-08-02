/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import java.util.List;

/**
 * Parent class for all validation result params containing ids with values.
 */
public abstract class ProjectValidationResultElParam extends ProjectValidationResultParam {

    private final List<String> ids;
    private final List<String> vals;

    protected ProjectValidationResultElParam(final List<String> ids, final List<String> vals) {
        this.ids = ids;
        this.vals = vals;
    }

    public List<String> getIds() {
        return ids;
    }

    public List<String> getVals() {
        return vals;
    }

    @Override
    public int hashCode() {
        int result = ids != null ? ids.hashCode() : 0;
        result = 31 * result + (vals != null ? vals.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return  ids + " " + vals;
    }
}
