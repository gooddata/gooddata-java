/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.auditevent;

import com.gooddata.collections.PageRequest;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeanUtils;
import org.springframework.web.util.UriComponentsBuilder;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Class to encapsulate time filtering and paging parameters
 */
public class AuditEventPageRequest extends PageRequest {

    private DateTime from;

    private DateTime to;

    private String type;

    public AuditEventPageRequest() {
    }

    public DateTime getFrom() {
        return from;
    }

    /**
     * Specify lower bound of interval
     */
    public void setFrom(final DateTime from) {
        this.from = from;
    }

    public DateTime getTo() {
        return to;
    }

    /**
     * Specify upper bound of interval
     */
    public void setTo(final DateTime to) {
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
            builderWithPaging.queryParam("from", from.toDateTime(DateTimeZone.UTC));
        }
        if (to != null) {
            builderWithPaging.queryParam("to", to.toDateTime(DateTimeZone.UTC));
        }
        if (type != null) {
            builderWithPaging.queryParam("type", type);
        }

        return builderWithPaging;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
