package com.gooddata.dataload.processes;

import com.gooddata.util.ISODateTimeDeserializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Schedule.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("schedule")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Schedule {

    public static final String URI = "/gdc/projects/{projectId}/schedules/{scheduleId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";

    private static final String MSETL_TYPE = "MSETL";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String EXECUTABLE = "EXECUTABLE";

    private String type;
    private String state;
    private String cron;
    private String timezone;
    private final DateTime nextExecutionTime;
    private final int consecutiveFailedExecutionCount;
    private final Map<String, String> params;
    private final Map<String,String> links;

    public Schedule(final DataloadProcess process, final String executable, final String cron) {
        notNull(process, "process");

        this.type = MSETL_TYPE;
        this.state = ScheduleState.ENABLED.name();
        this.cron = notEmpty(cron, "cron");
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
                     @JsonProperty("links") final Map<String, String> links) {
        this.type = type;
        this.state = state;
        this.cron = cron;
        this.timezone = timezone;
        this.nextExecutionTime = nextExecutionTime;
        this.consecutiveFailedExecutionCount = consecutiveFailedExecutionCount;
        this.params = params;
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
        notEmpty(key, "param cannot be empty!");
        notNull(value, "value cannot be null!");
        params.put(key, value);
    }

    public void removeParam(String paramKey) {
        notEmpty(paramKey, "paramKey cannot be empty!");
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
        return links != null ? links.get(SELF_LINK) : null;
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get("scheduleId");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Schedule))
            return false;

        Schedule schedule = (Schedule) o;

        if (cron != null ? !cron.equals(schedule.cron) : schedule.cron != null)
            return false;
        if (getUri() != null ? !getUri().equals(schedule.getUri()) : schedule.getUri() != null)
            return false;
        if (params != null ? !params.equals(schedule.params) : schedule.params != null)
            return false;
        if (state != null ? !state.equals(schedule.state) : schedule.state != null)
            return false;
        if (type != null ? !type.equals(schedule.type) : schedule.type != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (cron != null ? cron.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (getUri() != null ? getUri().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Schedule{");
        sb.append("type='").append(type).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", cron='").append(cron).append('\'');
        sb.append(", params=").append(params);
        sb.append(", uri=").append(getUri());
        sb.append('}');
        return sb.toString();
    }
}
