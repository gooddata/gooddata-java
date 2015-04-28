/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Arrays;
import java.util.Collection;

/**
 * Attachment to {@link ScheduledMail} represents dashboard-related information for the schedule.
 */
public class DashboardAttachment extends Attachment {

    private final Integer allTabs;
    private final Collection<String> tabs;
    private final String executionContext;

    @JsonCreator
    protected DashboardAttachment(
            @JsonProperty("uri") String uri,
            @JsonProperty("allTabs") Integer allTabs,
            @JsonProperty("executionContext") String executionContext,
            @JsonProperty("tabs") String... tabs
            ) {
        super(uri);
        this.allTabs = allTabs;
        this.tabs = Arrays.asList(tabs);
        this.executionContext = executionContext;
    }

    public Integer getAllTabs() { return allTabs; }

    public Collection<String> getTabs() { return tabs; }

    public String getExecutionContext() { return executionContext; }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardAttachment that = (DashboardAttachment) o;

        if (allTabs != null ? !allTabs.equals(that.allTabs) : that.allTabs != null) return false;
        if (tabs != null ? !tabs.equals(that.tabs) : that.tabs != null) return false;
        return !(executionContext != null ? !executionContext.equals(that.executionContext) : that.executionContext != null);

    }

    @Override
    public int hashCode() {
        int result = allTabs != null ? allTabs.hashCode() : 0;
        result = 31 * result + (tabs != null ? tabs.hashCode() : 0);
        result = 31 * result + (executionContext != null ? executionContext.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DashboardAttachment{" +
                "uri=" + getUri() +
                ", allTabs=" + allTabs +
                ", tabs=" + tabs +
                ", executionContext='" + executionContext + '\'' +
                '}';
    }
}
