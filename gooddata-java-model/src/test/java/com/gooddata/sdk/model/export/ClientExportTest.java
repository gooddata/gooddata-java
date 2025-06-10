/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.export;

import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientExportTest {

    @Test
    public void shouldSerializeSimple() throws Exception {
        final ClientExport invitations = new ClientExport("http://foo", "bar");
        assertThat(invitations, jsonEquals(resource("export/client-export-simple.json")));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ClientExport invitations = new ClientExport("https://secure.gooddata.com:443", "/gdc/projects/PROJECT", "/gdc/md/PROJECT/obj/123", "abc");
        assertThat(invitations, jsonEquals(resource("export/client-export.json")));
    }
}