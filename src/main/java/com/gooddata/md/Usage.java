/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

/**
 * Describes object usages. Object is represented by its URI and objects using it by collection of entries.
 */
public class Usage {

    private final String uri;

    private final Collection<Entry> usedBy;

    /**
     * Constructs object.
     * @param uri object URI
     * @param usedBy using objects
     */
    Usage(final String uri, final Collection<Entry> usedBy) {
        this.uri = uri;
        this.usedBy = usedBy;
    }

    public String getUri() {
        return uri;
    }

    public Collection<Entry> getUsedBy() {
        return usedBy;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
