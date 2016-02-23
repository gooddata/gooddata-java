/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md.maintenance;

/**
 * TODO
 */
public class PartialImportToken {
    private final String token;
    private final boolean exportAttributeProperties;

    public PartialImportToken(String token, boolean exportAttributeProperties) {

        this.token = token;
        this.exportAttributeProperties = exportAttributeProperties;
    }

    public String getToken() {
        return token;
    }

    public boolean isExportAttributeProperties() {
        return exportAttributeProperties;
    }
}
