/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.ISODateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static com.gooddata.util.Validate.notNullState;

/**
 * Schedule.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("schedule")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schedule {

    public static final String URI = "/gdc/projects/{projectId}/schedules/{scheduleId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";

    private static final String MSETL_TYPE = "MSETL";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String EXECUTABLE = "EXECUTABLE";

    private final String type;
    private String state;
    private String cron;
    private String timezone;
    private Integer reschedule;
    private String triggerScheduleId;
    private String name;
    private final DateTime nextExecutionTime;
    private final int consecutiveFailedExecutionCount;
    private final Map<String, String> params;
    private final Map<String,String> links;

    public Schedule(final DataloadProcess process, final String executable, final String cron) {
        this(process, executable);
        this.cron = notEmpty(cron, "cron");
    }

    /**
     * Creates schedule, which is triggered by execution of different schedule
     * @param process process to create schedule for
     * @param executable executable to be scheduled for execution
     * @param triggerSchedule schedule, which will trigger created schedule
     */
    public Schedule(final DataloadProcess process, final String executable, final Schedule triggerSchedule) {
        this(process, executable);
        this.triggerScheduleId = notEmpty(notNull(triggerSchedule, "triggerSchedule").getId(), "triggerSchedule ID");
    }

    private Schedule(final DataloadProcess process, final String executable) {
        notNull(process, "process");

        this.type = MSETL_TYPE;
        this.state = ScheduleState.ENABLED.name();
        this.params = new HashMap<>();
        this.params.put(PROCESS_ID, process.getId());
        process.validateExecutable(executable);
        this.nextExecutionTime = null;
        this.consecutiveFailedExecutionCount = 0;
        this.params.put(EXECUTABLE, executable);
        this.links = Collections.emptyMap();
    }

    @JsonCreator
    private Schedule(@JsonProperty("type") final String type,
                     @JsonProperty("state") final String state,
                     @JsonProperty("cron") final String cron,
                     @JsonProperty("timezone") final String timezone,
                     @JsonProperty("nextExecutionTime") @JsonDeserialize(using = ISODateTimeDeserializer.class) DateTime nextExecutionTime,
                     @JsonProperty("consecutiveFailedExecutionCount") final int consecutiveFailedExecutionCount,
                     @JsonProperty("params") final Map<String, String> params,
                     @JsonProperty("reschedule") final Integer reschedule,
                     @JsonProperty("triggerScheduleId") final String triggerScheduleId,
                     @JsonProperty("name") final String name,
                     @JsonProperty("links") final Map<String, String> links) {
        this.type = type;
        this.state = state;
        this.cron = cron;
        this.timezone = timezone;
        this.nextExecutionTime = nextExecutionTime;
        this.consecutiveFailedExecutionCount = consecutiveFailedExecutionCount;
        this.params = params;
        this.reschedule = reschedule;
        this.triggerScheduleId = triggerScheduleId;
        this.name = name;
        this.links = links;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = notEmpty(state, "state");
    }

    @JsonIgnore
    public void setState(final ScheduleState state) {
        this.state = notNull(state, "state").name();
    }

    @JsonIgnore
    public boolean isEnabled() {
        return ScheduleState.ENABLED.name().equals(state);
    }

    @JsonIgnore
    public String getProcessId() {
        return params.get(PROCESS_ID);
    }

    public void setProcessId(final DataloadProcess process) {
        params.put(PROCESS_ID, process.getId());
    }

    @JsonIgnore
    public String getExecutable() {
        return params.get(EXECUTABLE);
    }

    public void setExecutable(final DataloadProcess process, final String executable) {
        notNull(executable, "executable");
        notNull(process, "process").validateExecutable(executable);
        params.put(EXECUTABLE, executable);
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public void addParam(String key, String value) {
        notEmpty(key, "param");
        notNull(value, "value");
        params.put(key, value);
    }

    public void removeParam(String paramKey) {
        notEmpty(paramKey, "paramKey!");
        params.remove(paramKey);
    }


    public String getCron() {
        return cron;
    }

    public void setCron(final String cron) {
        this.cron = notEmpty(cron, "cron");
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    /**
     * Duration after a failed execution of the schedule is executed again
     * @return reschedule duration in minutes
     */
    @JsonProperty("reschedule")
    public Integer getRescheduleInMinutes() {
        return reschedule;
    }

    /**
     * Duration after a failed execution of the schedule is executed again
     * @return reschedule duration in minutes
     */
    @JsonIgnore
    public Duration getReschedule() {
        return reschedule != null ? Duration.standardMinutes(getRescheduleInMinutes()) : null;
    }

    /**
     * Duration after a failed execution of the schedule is executed again
     * @param reschedule this duration should not be too low, because it can be rejected by REST API (e.g. 15 minutes or more)
     */
    public void setReschedule(Duration reschedule) {
        this.reschedule = notNull(reschedule, "reschedule").toStandardMinutes().getMinutes();
    }

    public String getTriggerScheduleId() {
        return triggerScheduleId;
    }

    public void setTriggerScheduleId(final String triggerScheduleId) {
        this.triggerScheduleId = triggerScheduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setTimezone(final DateTimeZone timezone) {
        this.timezone = notNull(timezone, "timezone").getID();
    }

    @JsonIgnore
    public DateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    @JsonIgnore
    public int getConsecutiveFailedExecutionCount() {
        return consecutiveFailedExecutionCount;
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get("scheduleId");
    }

}
