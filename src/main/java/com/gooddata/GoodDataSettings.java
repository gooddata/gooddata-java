package com.gooddata;

import java.util.concurrent.TimeUnit;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GoodDataSettings that = (GoodDataSettings) o;

        if (maxConnections != that.maxConnections) return false;
        if (connectionTimeout != that.connectionTimeout) return false;
        if (connectionRequestTimeout != that.connectionRequestTimeout) return false;
        return socketTimeout == that.socketTimeout;

    }

    @Override
    public int hashCode() {
        int result = maxConnections;
        result = 31 * result + connectionTimeout;
        result = 31 * result + connectionRequestTimeout;
        result = 31 * result + socketTimeout;
        return result;
    }

    @Override
    public String toString() {
        return "GoodDataSettings{" +
                "connectionRequestTimeout=" + connectionRequestTimeout +
                ", maxConnections=" + maxConnections +
                ", connectionTimeout=" + connectionTimeout +
                ", socketTimeout=" + socketTimeout +
                '}';
    }

    private static int secondsToMillis(int seconds) {
        return (int) TimeUnit.SECONDS.toMillis(seconds);
    }


}
