/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.report.ReportExportFormat;
import org.joda.time.LocalDate;

import java.util.*;

import static com.gooddata.util.Validate.notNull;

/**
 * A scheduled mail MD object. It represents a <a href="http://search.cpan.org/~sbeck/Date-Manip-6.49/lib/Date/Manip/Recur.pod">schedule</a> on mail-sending of
 * exported dashboards and reports.
 */
@JsonTypeName("scheduledMail")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduledMail extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    ScheduledMail(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    private ScheduledMail(String title, String summary, Set<String> tags, boolean deprecated, String recurrency,
                          LocalDate startDate, String timeZone, Collection<String> toAddresses,
                          Collection<String> bccAddresses, String subject, String body, List<Attachment> attachments) {
        super(new Meta(null, null, null, null, summary, title, null, tags, null, null, deprecated, null, false, false,
                null));
        notNull(toAddresses, "toAddresses");
        notNull(subject, "subject");
        notNull(body, "body");
        notNull(attachments, "attachments");
        content = new Content(new ScheduledMailWhen(recurrency, startDate, timeZone), toAddresses, bccAddresses,
                subject, body, attachments);
    }

    /**
     * Creates an almost empty instance of the object. It's up to the user's responsibility to call all the necessary setters.
     *
     * @param title the title of the MD object
     * @param summary the summary of the MD object
     */
    public ScheduledMail(String title, String summary) {
        super(new Meta(null, null, null, null, summary, title, null, Collections.emptySet(), null, null, false,
                null, false, false, null));
        this.content = new Content();
    }

    /**
     * Creates full, safe mail schedule object.
     *
     * @param title the title of the MD object
     * @param summary the summary of the MD object
     * @param recurrency schedule in format defined in <a href="http://search.cpan.org/~sbeck/Date-Manip-6.49/lib/Date/Manip/Recur.pod">schedule</a>
     * @param startDate schedule starting date
     * @param timeZone time zone of the starting date
     * @param toAddresses collection of email addresses to send the mail to
     * @param bccAddresses collection of blind copy addresses to send the mail to
     * @param subject the subject of the scheduled mail
     * @param body the text body of the scheduled mail
     * @param attachments reports and dashboards to send in the scheduled email
     */
    public ScheduledMail(String title, String summary, String recurrency, LocalDate startDate, String timeZone,
                         Collection<String> toAddresses, Collection<String> bccAddresses, String subject, String body,
                         List<Attachment> attachments) {
        this(title, summary, Collections.emptySet(), false, recurrency, startDate, timeZone, toAddresses,
                bccAddresses, subject, body, attachments);
    }

    /**
     * Mail schedule MD object payload.
     */
    private static class Content {

        @JsonProperty("when")
        private ScheduledMailWhen scheduledMailWhen;

        /**
         * Collection of email addresses.
         */
        @JsonProperty("to")
        private Collection<String> toAddress;

        /**
         * Collection of email addresses.
         */
        @JsonProperty("bcc")
        private Collection<String> bccAddress;
        private String subject;
        private String body;
        private Collection<Attachment> attachments;

        @JsonCreator
        public Content(
                @JsonProperty("when") ScheduledMailWhen scheduledMailWhen,
                @JsonProperty("to") Collection<String> toAddress,
                @JsonProperty("bcc") Collection<String> bccAddress,
                @JsonProperty("subject") String subject,
                @JsonProperty("body") String body,
                @JsonProperty("attachments") Collection<Attachment> attachments) {

            this.scheduledMailWhen = scheduledMailWhen;
            this.toAddress = toAddress;
            this.bccAddress = bccAddress;
            this.subject = subject;
            this.body = body;
            this.attachments = attachments;
        }

        public Content() {
            this.scheduledMailWhen = new ScheduledMailWhen();
            this.toAddress = new ArrayList<>();
            this.bccAddress = new ArrayList<>();
            this.attachments = new ArrayList<>();
        }

        @JsonIgnore
        public ScheduledMailWhen getScheduledMailWhen() { return scheduledMailWhen; }

        @JsonIgnore
        public Collection<String> getToAddresses() { return toAddress; }

        @JsonIgnore
        public Collection<String> getBccAddresses() { return bccAddress; }

        public String getSubject() { return subject; }

        public String getBody() { return body; }

        public Collection<Attachment> getAttachments() { return attachments; }

        public void setScheduledMailWhen(ScheduledMailWhen scheduledMailWhen) {
            this.scheduledMailWhen = scheduledMailWhen;
        }

        public void setToAddress(Collection<String> toAddress) {
            this.toAddress = toAddress;
        }

        public void setBccAddress(Collection<String> bccAddress) {
            this.bccAddress = bccAddress;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public void setAttachments(Collection<Attachment> attachments) {
            this.attachments = attachments;
        }

    }

    @JsonIgnore
    public ScheduledMailWhen getWhen() { return content.getScheduledMailWhen(); }

    @JsonIgnore
    public Collection<String> getToAddresses() { return content.getToAddresses(); }

    @JsonIgnore
    public Collection<String> getBccAddresses() { return content.getBccAddresses(); }

    @JsonIgnore
    public String getSubject() { return content.getSubject(); }

    @JsonIgnore
    public String getBody() { return content.getBody(); }

    @JsonIgnore
    public Collection<? extends Attachment> getAttachments() { return content.getAttachments(); }

    public ScheduledMail setRecurrency(String recurrency) {
        this.content.getScheduledMailWhen().setRecurrency(recurrency);
        return this;
    }

    public ScheduledMail setStartDate(LocalDate startDate) {
        this.content.getScheduledMailWhen().setStartDate(startDate);
        return this;
    }

    public ScheduledMail setTimeZone(String timeZone) {
        this.content.getScheduledMailWhen().setTimeZone(timeZone);
        return this;
    }

    public ScheduledMail setTo(Collection<String> toAddresses) {
        this.content.setToAddress(toAddresses);
        return this;
    }

    public ScheduledMail setBcc(Collection<String> bccAddresses) {
        this.content.setBccAddress(bccAddresses);
        return this;
    }

    public ScheduledMail setSubject(String subject) {
        this.content.setSubject(subject);
        return this;
    }

    public ScheduledMail setBody(String body) {
        this.content.setBody(body);
        return this;
    }

    public ScheduledMail setAttachments(List<Attachment> attachments) {
        this.content.setAttachments(attachments);
        return this;
    }

    public ScheduledMail addToAddress(String toAdd) {
        this.content.getToAddresses().add(toAdd);
        return this;
    }

    public ScheduledMail addBccAddress(String bccAdd) {
        this.content.getBccAddresses().add(bccAdd);
        return this;
    }

    public ScheduledMail addReportAttachment(ReportDefinition reportDefinition, Map<String, String> exportOptions, String... formats) {
        notNull(formats, "formats");
        ReportAttachment ra = new ReportAttachment(reportDefinition.getUri(), exportOptions, formats);
        this.content.getAttachments().add(ra);
        return this;
    }

    public ScheduledMail addReportAttachment(ReportDefinition reportDefinition, Map<String, String> exportOptions, ReportExportFormat... formats) {
        return addReportAttachment(reportDefinition, exportOptions, ReportExportFormat.arrayToStringArray(formats));
    }

    public ScheduledMail addDashboardAttachment(String uri, Integer allTabs, String executionContext, String... tabs) {
        notNull(tabs, "tabs");
        DashboardAttachment da = new DashboardAttachment(uri, allTabs, executionContext, tabs);
        this.content.getAttachments().add(da);
        return this;
    }

}
