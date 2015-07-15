package com.gooddata.project;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class ProjectIdMatcher extends TypeSafeMatcher<Project> {

    private final Project project;

    public ProjectIdMatcher(final Project project) {
        this.project = project;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Project ids don't match");
    }

    public static ProjectIdMatcher hasSameIdAs(final Project project) {
        return new ProjectIdMatcher(project);
    }

    @Override
    protected boolean matchesSafely(Project item) {
        return project.getId().equals(item.getId());
    }
}
