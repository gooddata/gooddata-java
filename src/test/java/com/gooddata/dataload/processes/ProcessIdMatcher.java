package com.gooddata.dataload.processes;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class ProcessIdMatcher extends TypeSafeMatcher<DataloadProcess> {

    private final DataloadProcess process;

    public ProcessIdMatcher(final DataloadProcess process) {
        this.process = process;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Process id " + process.getId());
    }

    public static ProcessIdMatcher hasSameIdAs(final DataloadProcess process) {
        return new ProcessIdMatcher(process);
    }

    @Override
    protected boolean matchesSafely(DataloadProcess item) {
        return process.getId().equals(item.getId());
    }
}
