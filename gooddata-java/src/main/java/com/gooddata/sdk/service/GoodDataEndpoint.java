/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.apache.http.HttpHost;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * GoodData Platform endpoint represented by host, port and protocol
 */
public class GoodDataEndpoint {

    public static final String DEFAULT_PROTOCOL = "https";
    public static final int DEFAULT_PORT = 443;
    public static final String DEFAULT_HOSTNAME = "secure.gooddata.com";

    /**
     * @deprecated use DEFAULT_PROTOCOL instead
     */
    @Deprecated
    public static final String PROTOCOL = "https";
    /**
     * @deprecated use DEFAULT_PORT instead
     */
    @Deprecated
    public static final int PORT = 443;
    /**
     * @deprecated use DEFAULT_HOSTNAME instead
     */
    @Deprecated
    public static final String HOSTNAME = "secure.gooddata.com";

    private final String hostname;
    private final int port;
    private final String protocol;

    /**
     * Create GoodData endpoint for given hostname, port and protocol
     * @param hostname  GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param port      GoodData Platform's API port (e.g. 443)
     * @param protocol  GoodData Platform's API protocol (e.g. https)
     */
    public GoodDataEndpoint(final String hostname, final int port, final String protocol) {
        this.hostname = notEmpty(hostname, "hostname");
        this.port = port;
        this.protocol = notEmpty(protocol, "protocol");
    }

    /**
     * Create GoodData endpoint for given hostname, port using HTTPS protocol
     * @param hostname  GoodData Platform's host name (e.g. secure.gooddata.com)
     * @param port      GoodData Platform's API port (e.g. 443)
     */
    public GoodDataEndpoint(String hostname, int port) {
        this(hostname, port, DEFAULT_PROTOCOL);
    }

    /**
     * Create GoodData endpoint for given hostname using 443 port and HTTPS protocol
     * @param hostname GoodData Platform's host name (e.g. secure.gooddata.com)
     */
    public GoodDataEndpoint(String hostname) {
        this(hostname, DEFAULT_PORT, DEFAULT_PROTOCOL);
    }

    /**
     * Create GoodData endpoint for given hostname using 443 port and HTTPS protocol and secure.gooddata.com hostname
     */
    public GoodDataEndpoint() {
        this(DEFAULT_HOSTNAME, DEFAULT_PORT, DEFAULT_PROTOCOL);
    }

    /**
     * @return the host URI, as a string.
     */
    public String toUri() {
        return new HttpHost(hostname, port, protocol).toURI();
    }

    /**
     * @return GoodData Platform's host name (e.g. secure.gooddata.com)
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return GoodData Platform's API port (e.g. 443)
     */
    public int getPort() {
        return port;
    }

    /**
     * @return GoodData Platform's API protocol (e.g. https)
     */
    public String getProtocol() {
        return protocol;
    }
}
