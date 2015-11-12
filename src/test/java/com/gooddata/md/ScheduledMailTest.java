/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.JsonMatchers;
import com.gooddata.report.ReportExportFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ScheduledMailTest {

    private final ReportAttachment rp1 = new ReportAttachment(
            "/gdc/md/PROJ_ID/obj/OBJECT_ID1",
            Collections.singletonMap("pageOrientation", "landscape"),
            ReportExportFormat.PDF, ReportExportFormat.XLS
    );
    private final DashboardAttachment da1 = new DashboardAttachment(
            "/gdc/md/PROJ_ID/obj/DASHBOARD_ID2",
            1,
            null);
    private final DashboardAttachment da2 = new DashboardAttachment(
            "/gdc/md/PROJ_ID/obj/DASHBOARD_ID3",
            0,
            "/gdc/md/PROJ_ID/obj/EXECUTION_CONTEXT_ID",
            "testTab");

    private final ScheduledMailWhen scheduledMailWhen = new ScheduledMailWhen("0:0:0:1*12:0:0", new LocalDate(2012, 6, 5), "America/Los_Angeles");

    @Test
    public void testDeserialization() throws Exception {
        final ScheduledMail scheduledMail = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/scheduledMail.json"), ScheduledMail.class);
        assertThat(scheduledMail, is(notNullValue()));
        assertThat(scheduledMail.getToAddresses(), hasItems("email@example.com"));
        assertThat(scheduledMail.getBccAddresses(), hasItems("secret-email@example.com"));
        assertThat(scheduledMail.getSubject(), is("Scheduled report"));
        assertThat(scheduledMail.getBody(), is("Hey, I'm sending you new Reports and Dashboards!"));
        assertThat(scheduledMail.getWhen(), is(scheduledMailWhen));
        assertThat(scheduledMail.getAttachments(), containsInAnyOrder(rp1, da1, da2));
    }

    @Test
    public void testSerialization() throws Exception {
        final ScheduledMail scheduledMail = new ScheduledMail("Scheduled report example", "Daily at 12:00pm PT")
                                                .setRecurrency("0:0:0:1*12:0:0")
                                                .setStartDate(new LocalDate(2012, 6, 5))
                                                .setTimeZone("America/Los_Angeles")
                                                .setTo(Arrays.asList("email@example.com"))
                                                .setBcc(Arrays.asList("secret-email@example.com"))
                                                .setSubject("Scheduled report")
                                                .setBody("Hey, I'm sending you new Reports and Dashboards!")
                                                .setAttachments(Arrays.asList(rp1, da1, da2));

        assertThat(scheduledMail, JsonMatchers.serializesToJson("/md/scheduledMail.json"));
    }

}
