/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.common.gdc.Header;
import com.gooddata.sdk.service.retry.RetrySettings;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.VersionInfo;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static org.apache.http.util.VersionInfo.loadVersionInfo;
import static org.springframework.util.Assert.isTrue;

/**
 * Gather various additional settings of {@link GoodData}. Can be passed to the {@link GoodData} constructor to tune up
 * it's behaviour.
 * <p>
 * Settings are applied only once at the beginning. Changing this bean after it's passed to {@link GoodData} has
 * no effect.
 */
public class GoodDataSettings {

    private int maxConnections = 20;
    private int connectionTimeout = secondsToMillis(10);
    private int connectionRequestTimeout = secondsToMillis(10);
    private int socketTimeout = secondsToMillis(60);
    private int pollSleep = secondsToMillis(5);
    private String userAgent;
    private RetrySettings retrySettings;
    private Map<String, String> presetHeaders = new HashMap<>(2);

    private static final String UNKNOWN_VERSION = "UNKNOWN";

    public GoodDataSettings() {
        presetHeaders.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        presetHeaders.put(Header.GDC_VERSION, readApiVersion());
    }

    /**
     * Set maximum number of connections used. This applies same for connections per host as for total connections.
     * (As we assume GoodData connects to single host).
     * <p>
     * The default value is 20.
     *
     * @param maxConnections maximum number of connections used.
     */
    public void setMaxConnections(int maxConnections) {
        isTrue(maxConnections > 0, "maxConnections must be greater than zero");
        this.maxConnections = maxConnections;
    }

    /**
     * Maximum number of connection used
     *
     * @return maximum number of connection used
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * Set timeout milliseconds until connection established.
     * <p>
     * The default value is 10 seconds (10000 ms).
     * <p>
     * Set to 0 for infinite.
     *
     * @param connectionTimeout connection timeout milliseconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        isTrue(connectionTimeout >= 0, "connectionTimeout must be not negative");
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Set timeout seconds until connection established.
     * <p>
     * The default value is 10 seconds.
     * <p>
     * Set to 0 for infinite.
     *
     * @param connectionTimeout connection timeout seconds
     */
    public void setConnectionTimeoutSeconds(int connectionTimeout) {
        setConnectionTimeout(secondsToMillis(connectionTimeout));
    }

    /**
     * Milliseconds until connection established.
     *
     * @return milliseconds until connection established
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set timeout in milliseconds used when requesting a connection from the connection manager.
     * <p>
     * The default value is 10 seconds (10000 ms).
     * <p>
     * Set to 0 for infinite.
     *
     * @param connectionRequestTimeout connection request timeout milliseconds
     */
    public void setConnectionRequestTimeout(final int connectionRequestTimeout) {
        isTrue(connectionRequestTimeout >= 0, "connectionRequestTimeout must not be negative");
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    /**
     * Set timeout in seconds used when requesting a connection from the connection manager.
     * <p>
     * The default value is 10 seconds.
     * <p>
     * Set to 0 for infinite.
     * <p>
     *
     * @param connectionRequestTimeout connection request timeout seconds
     */
    public void setConnectionRequestTimeoutSeconds(final int connectionRequestTimeout) {
        setConnectionRequestTimeout(secondsToMillis(connectionRequestTimeout));
    }

    /**
     * Returns the timeout in milliseconds used when requesting a connection from the connection manager.
     *
     * @return milliseconds used as timeout when requesting a connection from the connection manager
     */
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    /**
     * Set socket timeout (maximum period inactivity between two consecutive data packets) milliseconds.
     * <p>
     * The default value is 60 seconds (60000 ms).
     * <p>
     * Set to 0 for infinite.
     *
     * @param socketTimeout socket timeout milliseconds
     */
    public void setSocketTimeout(int socketTimeout) {
        isTrue(socketTimeout >= 0, "socketTimeout must be not negative");
        this.socketTimeout = socketTimeout;
    }

    /**
     * Set socket timeout (maximum period inactivity between two consecutive data packets) seconds.
     * <p>
     * The default value is 60 seconds.
     * <p>
     * Set to 0 for infinite.
     *
     * @param socketTimeout socket timeout seconds
     */
    public void setSocketTimeoutSeconds(int socketTimeout) {
        setSocketTimeout(secondsToMillis(socketTimeout));
    }

    /**
     * Milliseconds for inactivity between two consecutive data packets.
     *
     * @return milliseconds for inactivity between two consecutive data packets
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Get sleep time in milliseconds between poll retries
     *
     * @see AbstractService#poll(PollHandler, long, TimeUnit)
     */
    public int getPollSleep() {
        return pollSleep;
    }

    /**
     * Set sleep time between poll retries
     *
     * @param pollSleep sleep milliseconds
     * @see AbstractService#poll(PollHandler, long, TimeUnit)
     */
    public void setPollSleep(final int pollSleep) {
        isTrue(pollSleep >= 0, "pollSleep must be not negative");
        this.pollSleep = pollSleep;
    }

    /**
     * Set sleep time between poll retries
     *
     * @param pollSleep sleep seconds
     * @see AbstractService#poll(PollHandler, long, TimeUnit)
     */
    public void setPollSleepSeconds(final int pollSleep) {
        setPollSleep(secondsToMillis(pollSleep));
    }

    /**
     * GoodData User agent
     * @return user agent string formatted with default suffix (identifying the SDK)
     */
    public String getGoodDataUserAgent() {
        return StringUtils.isNotBlank(userAgent) ? String.format("%s %s", userAgent, getDefaultUserAgent()) : getDefaultUserAgent();
    }

    /**
     * User agent
     * @return user agent string
     */
    public String getUserAgent() {
        return StringUtils.isNotBlank(userAgent) ? userAgent : getDefaultUserAgent();
    }

    /**
     * Set custom user agent as prefix for default user agent
     * @param userAgent user agent string
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public RetrySettings getRetrySettings() {
        return retrySettings;
    }

    /**
     * Set retry settings
     * @param retrySettings retry settings
     */
    public void setRetrySettings(RetrySettings retrySettings) {
        this.retrySettings = retrySettings;
    }

    /**
     * Set preset header
     * @param header header name
     * @param value header value
     */
    public void setPresetHeader(String header, String value) {
        presetHeaders.put(notNull(header, "header"), notNull(value, "value"));
    }

    /**
     * Preset headers
     * @return preset headers set by SDK on each HTTP call
     */
    public Map<String, String> getPresetHeaders() {
        return presetHeaders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GoodDataSettings that = (GoodDataSettings) o;
        return maxConnections == that.maxConnections
                && connectionTimeout == that.connectionTimeout
                && connectionRequestTimeout == that.connectionRequestTimeout
                && socketTimeout == that.socketTimeout
                && pollSleep == that.pollSleep
                && Objects.equals(userAgent, that.userAgent)
                && Objects.equals(retrySettings, that.retrySettings)
                && Objects.equals(presetHeaders, that.presetHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxConnections, connectionTimeout, connectionRequestTimeout, socketTimeout, pollSleep,
                userAgent, retrySettings, presetHeaders);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    private static int secondsToMillis(int seconds) {
        return (int) TimeUnit.SECONDS.toMillis(seconds);
    }

    private String getDefaultUserAgent() {
        final Package pkg = Package.getPackage("com.gooddata.sdk.service");
        final String clientVersion = pkg != null && pkg.getImplementationVersion() != null
                ? pkg.getImplementationVersion() : UNKNOWN_VERSION;

        final VersionInfo vi = loadVersionInfo("org.apache.http.client", HttpClientBuilder.class.getClassLoader());
        final String apacheVersion = vi != null ? vi.getRelease() : UNKNOWN_VERSION;

        return String.format("%s/%s (%s; %s) %s/%s", "GoodData-Java-SDK", clientVersion,
                System.getProperty("os.name"), System.getProperty("java.specification.version"),
                "Apache-HttpClient", apacheVersion);
    }

    private static String readApiVersion() {
        try {
            return StreamUtils.copyToString(GoodData.class.getResourceAsStream("/GoodDataApiVersion"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read GoodDataApiVersion from classpath", e);
        }
    }



}
