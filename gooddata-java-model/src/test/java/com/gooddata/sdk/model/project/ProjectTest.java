/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProjectTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialize() throws Exception {
        final Project project = readObjectFromResource("/project/project.json", Project.class);
        assertThat(project, is(notNullValue()));

        assertThat(project.getAuthorizationToken(), is("AUTH_TOKEN"));
        assertThat(project.getDriver(), is("Pg"));
        assertThat(project.getGuidedNavigation(), is("1"));
        assertThat(project.getCluster(), is("CLUSTER"));
        assertThat(project.isPublic(), is(false));
        assertThat(project.getState(), is("ENABLED"));
        assertThat(project.getEnvironment(), is("TESTING"));

        assertThat(project.getTitle(), is("TITLE"));
        assertThat(project.getSummary(), is("DESC"));
        assertThat(project.getAuthor(), is("/gdc/account/profile/USER_ID"));
        assertThat(project.getContributor(), is("/gdc/account/profile/CONTRIB_USER_ID"));
        assertThat(project.getCreated(), is(new DateTime(2014, 4, 11, 11, 43, 45, DateTimeZone.UTC)));
        assertThat(project.getUpdated(), is(new DateTime(2014, 4, 11, 11, 43, 47, DateTimeZone.UTC)));

        assertThat(project.getLdmThumbnailLink(), is("/gdc/projects/PROJECT_ID/ldm?thumbnail=1"));
        assertThat(project.getLdmThumbnailUri(), is("/gdc/projects/PROJECT_ID/ldm?thumbnail=1"));
        assertThat(project.getUri(), is("/gdc/projects/PROJECT_ID"));
        assertThat(project.getClearCachesLink(), is("/gdc/projects/PROJECT_ID/clearCaches"));
        assertThat(project.getClearCachesUri(), is("/gdc/projects/PROJECT_ID/clearCaches"));
        assertThat(project.getInvitationsLink(), is("/gdc/projects/PROJECT_ID/invitations"));
        assertThat(project.getInvitationsUri(), is("/gdc/projects/PROJECT_ID/invitations"));
        assertThat(project.getUsersLink(), is("/gdc/projects/PROJECT_ID/users?link=1"));
        assertThat(project.getUsersUri(), is("/gdc/projects/PROJECT_ID/users?link=1"));
        assertThat(project.getGroupsLink(), is("/gdc/projects/PROJECT_ID/groups"));
        assertThat(project.getGroupsUri(), is("/gdc/projects/PROJECT_ID/groups"));
        assertThat(project.getUploadsLink(), is("https://ea-di.staging.getgooddata.com/project-uploads/PROJECT_ID/"));
        assertThat(project.getUploadsUri(), is("https://ea-di.staging.getgooddata.com/project-uploads/PROJECT_ID/"));
        assertThat(project.getLdmLink(), is("/gdc/projects/PROJECT_ID/ldm"));
        assertThat(project.getLdmUri(), is("/gdc/projects/PROJECT_ID/ldm"));
        assertThat(project.getMetadataLink(), is("/gdc/md/PROJECT_ID"));
        assertThat(project.getMetadataUri(), is("/gdc/md/PROJECT_ID"));
        assertThat(project.getPublicArtifactsLink(), is("/gdc/projects/PROJECT_ID/publicartifacts"));
        assertThat(project.getPublicArtifactsUri(), is("/gdc/projects/PROJECT_ID/publicartifacts"));
        assertThat(project.getRolesLink(), is("/gdc/projects/PROJECT_ID/roles"));
        assertThat(project.getRolesUri(), is("/gdc/projects/PROJECT_ID/roles"));
        assertThat(project.getDataLoadLink(), is("/gdc/projects/PROJECT_ID/dataload"));
        assertThat(project.getDataLoadUri(), is("/gdc/projects/PROJECT_ID/dataload"));
        assertThat(project.getConnectorsLink(), is("/gdc/projects/PROJECT_ID/connectors"));
        assertThat(project.getConnectorsUri(), is("/gdc/projects/PROJECT_ID/connectors"));
        assertThat(project.getExecuteLink(), is("/gdc/projects/PROJECT_ID/execute"));
        assertThat(project.getExecuteUri(), is("/gdc/projects/PROJECT_ID/execute"));
        assertThat(project.getSchedulesLink(), is("/gdc/projects/PROJECT_ID/schedules"));
        assertThat(project.getSchedulesUri(), is("/gdc/projects/PROJECT_ID/schedules"));
        assertThat(project.getTemplatesLink(), is("/gdc/md/PROJECT_ID/templates"));
        assertThat(project.getEventStoresLink(), is("/gdc/projects/PROJECT_ID/dataload/eventstore/stores"));
        assertThat(project.getEventStoresUri(), is("/gdc/projects/PROJECT_ID/dataload/eventstore/stores"));
    }

    @Test
    public void testSerialize() throws Exception {
        final Project project = new Project("TITLE", "SUMMARY", "TOKEN");
        project.setProjectTemplate("/projectTemplates/TEMPLATE");
        project.setEnvironment(ProjectEnvironment.TESTING);
        final String serializedProject = OBJECT_MAPPER.writeValueAsString(project);

        assertThat(serializedProject, startsWith("{\"project\""));

        assertThat(serializedProject, containsString("\"content\""));
        assertThat(serializedProject, containsString("\"authorizationToken\":\"TOKEN\""));
        assertThat(serializedProject, containsString("\"driver\":\"Pg\""));
        assertThat(serializedProject, containsString("\"guidedNavigation\":\"1\""));
        assertThat(serializedProject, not(containsString("\"cluster\"")));
        assertThat(serializedProject, not(containsString("\"isPublic\"")));
        assertThat(serializedProject, not(containsString("\"state\"")));

        assertThat(serializedProject, containsString("\"meta\""));
        assertThat(serializedProject, containsString("\"title\":\"TITLE\""));
        assertThat(serializedProject, containsString("\"summary\":\"SUMMARY\""));
        assertThat(serializedProject, containsString("\"projectTemplate\":\"/projectTemplates/TEMPLATE\""));
        assertThat(serializedProject, containsString("\"environment\":\"TESTING\""));
        assertThat(serializedProject, not(containsString("\"author\"")));
        assertThat(serializedProject, not(containsString("\"contributor\"")));
        assertThat(serializedProject, not(containsString("\"created\"")));
        assertThat(serializedProject, not(containsString("\"updated\"")));
        assertThat(serializedProject, not(containsString("\"deprecated\"")));

        assertThat(serializedProject, not(containsString("\"links\"")));
    }

    @Test
    public void testDeserializeVerticaProject() throws Exception {
        final Project project = readObjectFromResource("/project/project-vertica.json", Project.class);
        assertThat(project, is(notNullValue()));

        assertThat(project.getDriver(), is("vertica"));
        assertThat(project.getState(), is("ENABLED"));
        assertThat(project.getTitle(), is("TITLE"));
    }

    @Test
    public void testSerializeVerticaProject() throws Exception {
        final Project project = new Project("TITLE", "SUMMARY", "TOKEN");
        project.setDriver(ProjectDriver.VERTICA);
        project.setProjectTemplate("/projectTemplates/TEMPLATE");
        final String serializedProject = OBJECT_MAPPER.writeValueAsString(project);

        assertThat(serializedProject, startsWith("{\"project\""));
        assertThat(serializedProject, containsString("\"driver\":\"vertica\""));
    }

    @Test
    public void testToStringFormat() {
        final Project project = new Project("TITLE", "SUMMARY", "TOKEN");

        assertThat(project.toString(), matchesPattern(Project.class.getSimpleName() + "\\[.*\\]"));
    }
}