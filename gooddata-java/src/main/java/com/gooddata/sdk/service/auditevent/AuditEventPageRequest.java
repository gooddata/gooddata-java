/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.collections.PageRequest;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Class to encapsulate time filtering and paging parameters
 */
public final class AuditEventPageRequest extends PageRequest {

    private ZonedDateTime from;

    private ZonedDateTime to;

    private String type;

    public AuditEventPageRequest() {
    }

    public ZonedDateTime getFrom() {
        return from;
    }

    /**
     * Specify lower bound of interval
     */
    public void setFrom(final ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    /**
     * Specify upper bound of interval
     */
    public void setTo(final ZonedDateTime to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    /**
     * Specify event type for filtering purposes
     *
     * @param type event type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Copy constructor
     *
     * @param source source object (not null) to create copy of
     * @return new instance, which fields has same value as fields of <code>source</code>
     */
    public static AuditEventPageRequest copy(final AuditEventPageRequest source) {
        notNull(source, "source cannot be null");

        final AuditEventPageRequest copy = new AuditEventPageRequest();
        BeanUtils.copyProperties(source, copy);

        return copy;
    }

    /**
     * Copy this request parameters and increment request parameter limit.
     * If Limit is negative, than sanitized limit is taken and incremented.
     *
     * @return new instance with incremented limit
     */
    public AuditEventPageRequest withIncrementedLimit() {
        final AuditEventPageRequest copy = AuditEventPageRequest.copy(this);
        copy.setLimit(this.getSanitizedLimit() + 1);
        return copy;
    }

    @Override
    public UriComponentsBuilder updateWithPageParams(final UriComponentsBuilder builder) {
        UriComponentsBuilder builderWithPaging = super.updateWithPageParams(builder);
        if (from != null) {
            builderWithPaging.queryParam("from", from.withZoneSameInstant(UTC));
        }
        if (to != null) {
            builderWithPaging.queryParam("to", to.withZoneSameInstant(UTC));
        }
        if (type != null) {
            builderWithPaging.queryParam("type", type);
        }

        return builderWithPaging;
    }

    @Override
    protected boolean canEqual(final Object o) {
        return o instanceof AuditEventPageRequest;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditEventPageRequest)) return false;
        if (!super.equals(o)) return false;

        final AuditEventPageRequest that = (AuditEventPageRequest) o;
        if (!that.canEqual(this)) return false;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
