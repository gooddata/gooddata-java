/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

public class BulkGetUrisTest {

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(BulkGetUris.class)
                .usingGetClass()
                .withNonnullFields("items")
                .verify();
    }

}