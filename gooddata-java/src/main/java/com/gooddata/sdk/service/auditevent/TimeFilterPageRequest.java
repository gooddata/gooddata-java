/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTime;
import com.gooddata.sdk.common.util.MutableUri;
import com.gooddata.sdk.common.util.Validate;
import org.springframework.beans.BeanUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Class to encapsulate time filtering
 */
public class TimeFilterPageRequest extends CustomPageRequest {
    protected ZonedDateTime from;
    protected ZonedDateTime to;

    public TimeFilterPageRequest() {
    }

    public static TimeFilterPageRequest copy(TimeFilterPageRequest source) {
        Validate.notNull(source, "source cannot be null");
        TimeFilterPageRequest copy = new TimeFilterPageRequest();
        BeanUtils.copyProperties(source, copy);
        return copy;
    }

    public ZonedDateTime getFrom() {
        return this.from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return this.to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public TimeFilterPageRequest withIncrementedLimit() {
        TimeFilterPageRequest copy = copy(this);
        copy.setLimit(this.getSanitizedLimit() + 1);
        return copy;
    }

    @Override
    public MutableUri updateWithPageParams(MutableUri builder) {
        MutableUri builderWithPaging = super.updateWithPageParams(builder);
        if (this.from != null) {
            builderWithPaging.replaceQueryParam("from", ISOZonedDateTime.FORMATTER.format(this.from.withZoneSameInstant(ZoneOffset.UTC)));
        }

        if (this.to != null) {
            builderWithPaging.replaceQueryParam("to", ISOZonedDateTime.FORMATTER.format(this.to.withZoneSameInstant(ZoneOffset.UTC)));
        }

        return builderWithPaging;
    }

    @Override
    protected boolean canEqual(Object o) {
        return o instanceof TimeFilterPageRequest;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeFilterPageRequest that)) return false;
        if (!super.equals(o)) return false;

        if (!that.canEqual(this)) return false;

        if (!Objects.equals(from, that.from)) return false;
        return Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.from != null ? this.from.hashCode() : 0);
        result = 31 * result + (this.to != null ? this.to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this, new String[0]);
    }

}

