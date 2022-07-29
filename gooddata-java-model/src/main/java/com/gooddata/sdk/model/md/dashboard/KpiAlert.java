/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.md.Queryable;
import com.gooddata.sdk.model.md.Updatable;

import java.io.Serializable;

/**
 * Represents KPI alert set for some KPI on analytical dashboard.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("kpiAlert")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KpiAlert extends AbstractObj implements Queryable, Updatable {

    private static final long serialVersionUID = 4771232690549128988L;

    private final Content content;

    /**
     * Constructor.
     *
     * @param title KPI alert title
     * @param kpiUri URI of the KPI for which the alert is defined
     * @param dashboardUri URI of the KPI where the KPI alert is located
     * @param threshold KPI alert threshold
     * @param triggerCondition condition for triggering KPI alert
     * @param filterContextUri URI of filter context used for computation of KPI alert (optional)
     */
    public KpiAlert(
            final String title,
            final String kpiUri,
            final String dashboardUri,
            final double threshold,
            final String triggerCondition,
            final String filterContextUri) {
        this(new Meta(title), new Content(
                notEmpty(kpiUri, "kpiUri"),
                notEmpty(dashboardUri, "dashboardUri"),
                threshold, false,
                notEmpty(triggerCondition, "triggerCondition"),
                filterContextUri));
    }

    @JsonCreator
    private KpiAlert(@JsonProperty("meta") final Meta meta, @JsonProperty("content") final Content content) {
        super(meta);
        this.content = content;
    }

    @JsonProperty
    private Content getContent() {
        return content;
    }

    /**
     * @return if the KPI alert was already triggered
     */
    @JsonIgnore
    public boolean wasTriggered() {
        return getContent().isTriggered();
    }

    /**
     * @return KPI value threshold for triggering KPI alert
     */
    @JsonIgnore
    public double getThreshold() {
        return getContent().getThreshold();
    }

    /**
     * @return filters used for computation of KPI alert
     */
    @JsonIgnore
    public String getFilterContextUri() {
        return getContent().getFilterContext();
    }

    /**
     * @return condition for triggering KPI alert (e.g. {@code "above_threshold"})
     */
    @JsonIgnore
    public String getTriggerCondition() {
        return getContent().getWhenTriggered();
    }

    /**
     * @return URI of the analytical dashboard where the alert is located
     */
    @JsonIgnore
    public String getDashboardUri() {
        return getContent().getDashboard();
    }

    /**
     * @return URI of the KPI for which is the alert configured
     */
    @JsonIgnore
    public String getKpiUri() {
        return getContent().getKpi();
    }

    /**
     * Creates new copy of KPI alert with a given triggered state changed.
     *
     * @param wasTriggered triggered state
     * @return new KPI alert
     */
    public KpiAlert withTriggeredState(final boolean wasTriggered) {
        return new KpiAlert(meta, new Content(getKpiUri(), getDashboardUri(), getThreshold(), wasTriggered, getTriggerCondition(),
                getFilterContextUri()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content implements Serializable {

        private static final long serialVersionUID = 1L;

        private final boolean isTriggered;
        private final double threshold;
        private final String filterContext;
        private final String whenTriggered;
        private final String dashboard;
        private final String kpi;

        @JsonCreator
        private Content(
                @JsonProperty("kpi") final String kpi,
                @JsonProperty("dashboard") final String dashboard,
                @JsonProperty("threshold") final double threshold,
                @JsonProperty("isTriggered") final boolean isTriggered,
                @JsonProperty("whenTriggered") final String whenTriggered,
                @JsonProperty("filterContext") final String filterContext) {
            this.isTriggered = isTriggered;
            this.threshold = threshold;
            this.filterContext = filterContext;
            this.whenTriggered = whenTriggered;
            this.dashboard = dashboard;
            this.kpi = kpi;
        }

        @JsonProperty("isTriggered")
        public boolean isTriggered() {
            return isTriggered;
        }

        public double getThreshold() {
            return threshold;
        }

        public String getFilterContext() {
            return filterContext;
        }

        public String getWhenTriggered() {
            return whenTriggered;
        }

        public String getDashboard() {
            return dashboard;
        }

        public String getKpi() {
            return kpi;
        }
    }
}
