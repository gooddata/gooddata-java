/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.util.GDDateDeserializer;
import com.gooddata.util.GDDateSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;

/**
 * Represents the start date and cron-like expression for {@link com.gooddata.md.ScheduledMail} mail schedule.
 */
public class ScheduledMailWhen {

    /**
     * Cron like recurrency pattern. Example: "0:0:0:1*12:0:0".
     * For details, please see <a href="http://search.cpan.org/~sbeck/Date-Manip-6.49/lib/Date/Manip/Recur.pod">this comprehensive documentation</a>.
     */
    private String recurrency;
    private LocalDate startDate;
    private String timeZone;

    @JsonCreator
    protected ScheduledMailWhen(@JsonProperty("recurrency") String recurrency,
                                @JsonProperty("startDate") @JsonDeserialize(using = GDDateDeserializer.class) LocalDate startDate,
                                @JsonProperty("timeZone") String timeZone) {
        this.recurrency = recurrency;
        this.startDate = startDate;
        this.timeZone = timeZone;
    }

    public ScheduledMailWhen() {}

    public String getRecurrency() { return recurrency; }

    @JsonSerialize(using = GDDateSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    public LocalDate getStartDate() { return startDate; }

    public String getTimeZone() { return timeZone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduledMailWhen scheduledMailWhen = (ScheduledMailWhen) o;

        if (recurrency != null ? !recurrency.equals(scheduledMailWhen.recurrency) : scheduledMailWhen.recurrency != null) return false;
        if (startDate != null ? !startDate.equals(scheduledMailWhen.startDate) : scheduledMailWhen.startDate != null) return false;
        return !(timeZone != null ? !timeZone.equals(scheduledMailWhen.timeZone) : scheduledMailWhen.timeZone != null);

    }

    @Override
    public int hashCode() {
        int result = recurrency != null ? recurrency.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
        return result;
    }

    public void setRecurrency(String recurrency) {
        this.recurrency = recurrency;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
