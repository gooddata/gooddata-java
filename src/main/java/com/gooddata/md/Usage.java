package com.gooddata.md;

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
}
