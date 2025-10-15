/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.sdk.model.dataload.processes.DataloadProcess;
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

    public static ProcessIdMatcher hasSameProcessIdAs(final DataloadProcess process) {
        return new ProcessIdMatcher(process);
    }

    @Override
    protected boolean matchesSafely(DataloadProcess item) {
        return process.getId().equals(item.getId());
    }
}

