package com.gooddata.project;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.collections.PageRequest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.gooddata.project.ProjectEnvironment.TESTING;
import static com.gooddata.project.ProjectIdMatcher.hasSameIdAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Project acceptance tests.
 */
public class ProjectServiceAT extends AbstractGoodDataAT {

    private final String projectToken;

    public ProjectServiceAT() {
        projectToken = getProperty("projectToken");
    }

    @Test(groups = "project", dependsOnGroups = "account")
    public void createProject() throws Exception {
        final Project p = new Project(title, projectToken);
        p.setEnvironment(TESTING);
        project = gd.getProjectService().createProject(p).get();
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjects() throws Exception {
        final ProjectService projectService = gd.getProjectService();

        final Collection<Project> projects = projectService.getProjects();
        assertThat(projects, hasItem(hasSameIdAs(project)));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjectUsers() throws Exception {
        final ProjectService projectService = gd.getProjectService();

        final List<User> users = new ArrayList<>();
        List<User> page;
        while (!(page = projectService.listUsers(project, new PageRequest(users.size(), 100))).isEmpty()) {
            users.addAll(page);
        }
        assertThat(users, not(empty()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjectRoles() throws Exception {
        final ProjectService projectService = gd.getProjectService();

        final Set<Role> roles = projectService.getRoles(project);
        assertThat(roles, not(empty()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectById() throws Exception {
        final Project project = gd.getProjectService().getProjectById(this.project.getId());
        assertThat(project, hasSameIdAs(this.project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectByUri() throws Exception {
        final Project project = gd.getProjectService().getProjectByUri(this.project.getUri());
        assertThat(project, hasSameIdAs(this.project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void validateProject() throws Exception {
        final ProjectValidationResults results = gd.getProjectService().validateProject(project).get();
        assertThat(results, is(notNullValue()));
        assertThat(results.getResults(), is(notNullValue()));
    }

}
