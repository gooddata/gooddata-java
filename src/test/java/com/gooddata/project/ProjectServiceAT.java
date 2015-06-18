package com.gooddata.project;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.GoodDataRestException;
import com.gooddata.collections.PageRequest;
import com.gooddata.gdc.FeatureFlag;
import org.springframework.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.gooddata.project.ProjectEnvironment.TESTING;
import static com.gooddata.project.ProjectIdMatcher.hasSameIdAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.testng.AssertJUnit.fail;

/**
 * Project acceptance tests.
 */
public class ProjectServiceAT extends AbstractGoodDataAT {

    private static final String PROJECT_FEATURE_FLAG = "testFeatureFlag";

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

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void createProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().createFeatureFlag(project, new ProjectFeatureFlag(PROJECT_FEATURE_FLAG));
        checkFeatureFlag(featureFlag, true);
    }

    @Test(groups = "project", dependsOnMethods = "createProjectFeatureFlag")
    public void listProjectFeatureFlags() throws Exception {
        gd.getProjectService().createFeatureFlag(project, new ProjectFeatureFlag("mostRecentFeatureFlag"));

        final List<ProjectFeatureFlag> projectFeatureFlags = gd.getProjectService().listFeatureFlags(project);

        assertThat(projectFeatureFlags, hasItems(
                new ProjectFeatureFlag("mostRecentFeatureFlag", true),
                new ProjectFeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test(groups = "project", dependsOnMethods = "createProjectFeatureFlag")
    public void getProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().getFeatureFlag(project, PROJECT_FEATURE_FLAG);
        checkFeatureFlag(featureFlag, true);
    }

    @Test(groups = "project", dependsOnMethods = "getProjectFeatureFlag")
    public void updateProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().getFeatureFlag(project, PROJECT_FEATURE_FLAG);

        // disable (update) feature flag
        featureFlag.setEnabled(false);
        final ProjectFeatureFlag disabledFlag = gd.getProjectService().updateFeatureFlag(featureFlag);
        checkFeatureFlag(disabledFlag, false);

        // enable again
        featureFlag.setEnabled(true);
        final ProjectFeatureFlag enabledFlag = gd.getProjectService().updateFeatureFlag(featureFlag);
        checkFeatureFlag(enabledFlag, true);
    }

    @Test(groups = "project", dependsOnMethods = "createProjectFeatureFlag")
    public void deleteProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().createFeatureFlag(project, new ProjectFeatureFlag("temporaryFeatureFlag"));

        gd.getProjectService().deleteFeatureFlag(featureFlag);

        try {
            gd.getProjectService().getFeatureFlag(project, featureFlag.getName());
            fail("Feature flag has not been deleted properly. HTTP status NOT FOUND expected.");
        } catch (GoodDataRestException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND.value()));
        }
    }

    private void checkFeatureFlag(ProjectFeatureFlag featureFlag, boolean expectedValue) {
        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(PROJECT_FEATURE_FLAG));
        assertThat(featureFlag.getEnabled(), is(expectedValue));
    }

}
