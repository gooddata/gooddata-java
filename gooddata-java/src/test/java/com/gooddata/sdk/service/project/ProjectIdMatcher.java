/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.model.project.Project;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class ProjectIdMatcher extends TypeSafeMatcher<Project> {

    private final Project project;

    public ProjectIdMatcher(final Project project) {
        this.project = project;
    }

    public static ProjectIdMatcher hasSameIdAs(final Project project) {
        return new ProjectIdMatcher(project);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Project ids don't match");
    }

    @Override
    protected boolean matchesSafely(Project item) {
        return project.getId().equals(item.getId());
    }
}
