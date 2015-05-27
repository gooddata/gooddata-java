package com.gooddata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.Test;


public class GoodDataSettingsTest {

    @Test
    public void testHasDefaults() throws Exception {
        final GoodDataSettings settings = new GoodDataSettings();
        assertTrue(settings.getMaxConnections() > 0);
        assertTrue(settings.getConnectionTimeout() >= 0);
        assertTrue(settings.getSocketTimeout() >= 0);
    }

    @Test
    public void testSetSeconds() throws Exception {
        final GoodDataSettings settings = new GoodDataSettings();
        settings.setConnectionTimeoutSeconds(53);
        settings.setSocketTimeoutSeconds(71);

        assertEquals(53000, settings.getConnectionTimeout());
        assertEquals(71000, settings.getSocketTimeout());
    }
}