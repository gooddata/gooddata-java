/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GDLocalDate;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents the start date and cron-like expression for {@link ScheduledMail} mail schedule.
 */
public class ScheduledMailWhen implements Serializable {

    private static final long serialVersionUID = 1203170008606357967L;

    /**
     * Cron like recurrency pattern. Example: "0:0:0:1*12:0:0".
     * For details, please see <a href="http://search.cpan.org/~sbeck/Date-Manip-6.60/lib/Date/Manip/Recur.pod">this comprehensive documentation</a>.
     */
    private String recurrency;
    @GDLocalDate
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate startDate;
    private String timeZone;

    @JsonCreator
    protected ScheduledMailWhen(@JsonProperty("recurrency") String recurrency,
                                @JsonProperty("startDate") LocalDate startDate,
                                @JsonProperty("timeZone") String timeZone) {
        this.recurrency = recurrency;
        this.startDate = startDate;
        this.timeZone = timeZone;
    }

    public ScheduledMailWhen() {}

    public String getRecurrency() { return recurrency; }

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
