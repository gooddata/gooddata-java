/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.webdav;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for WebDAV service components
 */
@Configuration
public class WebDavConfiguration {

    @Bean
    public WebDavService webDavService() {
        return new SardineWebDavService();
    }
}