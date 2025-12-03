/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.auditevent;

import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.MutableUri;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Class to encapsulate time filtering and paging parameters
 */
public final class AuditEventPageRequest extends TimeFilterPageRequest {

    private String type;

    public AuditEventPageRequest() {
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
     * Copy this request parameters and increment request parameter limit.
     * If Limit is negative, then sanitized limit is taken and incremented.
     *
     * @return new instance with incremented limit
     */
    public AuditEventPageRequest withIncrementedLimit() {
        final AuditEventPageRequest copy = AuditEventPageRequest.copy(this);
        copy.setLimit(this.getSanitizedLimit() + 1);
        return copy;
    }

    @Override
    public MutableUri updateWithPageParams(final MutableUri builder) {
        MutableUri builderWithPaging = super.updateWithPageParams(builder);

        if (type != null) {
            builderWithPaging.replaceQueryParam("type", type);
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
        if (!(o instanceof AuditEventPageRequest that)) return false;
        if (!super.equals(o)) return false;

        if (!that.canEqual(this)) return false;

        if (!Objects.equals(from, that.from)) return false;
        if (!Objects.equals(to, that.to)) return false;
        return Objects.equals(type, that.type);
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
