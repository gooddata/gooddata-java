/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.gooddata.export.ExportFormat;
import org.apache.commons.lang3.SerializationUtils;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ScheduledMailTest {

    private final ReportAttachment rp1 = new ReportAttachment(
            "/gdc/md/PROJ_ID/obj/OBJECT_ID1",
            Collections.singletonMap("pageOrientation", "landscape"),
            ExportFormat.PDF, ExportFormat.XLS
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
        final ScheduledMail scheduledMail = readObjectFromResource("/md/scheduledMail.json", ScheduledMail.class);
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

        assertThat(scheduledMail, jsonEquals(resource("md/scheduledMail.json")));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final ScheduledMail scheduledMail = readObjectFromResource("/md/scheduledMail.json", ScheduledMail.class);

        assertThat(scheduledMail.toString(), matchesPattern(ScheduledMail.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final ScheduledMail scheduledMail = readObjectFromResource("/md/scheduledMail.json", ScheduledMail.class);
        final ScheduledMail deserialized = SerializationUtils.roundtrip(scheduledMail);

        assertThat(deserialized, jsonEquals(scheduledMail));
    }

}
