/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.model.connector.ConnectorType.ZENDESK4;

/**
 * Zendesk 4 (Insights) connector settings.
 */
public class Zendesk4Settings implements Settings {

    public static final String URL = "/gdc/projects/{project}/connectors/zendesk4/integration/settings";
    private final String type;
    private final String syncTime;
    private final String syncTimeZone;
    private String apiUrl;
    private String zopimUrl;
    private String account;

    public Zendesk4Settings(final String apiUrl) {
        this(apiUrl, null, null, null, null, null);
    }

    @JsonCreator
    public Zendesk4Settings(@JsonProperty("apiUrl") String apiUrl,
                            @JsonProperty("zopimUrl") String zopimUrl,
                            @JsonProperty("account") String account,
                            @JsonProperty("type") String type,
                            @JsonProperty("syncTime") String syncTime,
                            @JsonProperty("syncTimeZone") String syncTimeZone) {
        this.apiUrl = notEmpty(apiUrl, "apiUrl");
        this.zopimUrl = zopimUrl;
        this.account = account;
        this.type = type;
        this.syncTime = syncTime;
        this.syncTimeZone = syncTimeZone;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(final String apiUrl) {
        this.apiUrl = notEmpty(apiUrl, "apiUrl");
    }

    public String getZopimUrl() {
        return zopimUrl;
    }

    public void setZopimUrl(final String zopimUrl) {
        this.zopimUrl = zopimUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public String getSyncTimeZone() {
        return syncTimeZone;
    }

    @Override
    public ConnectorType getConnectorType() {
        return ZENDESK4;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    /**
     * Type of Zendesk account.
     */
    public enum Zendesk4Type {plus, enterprise}
}
