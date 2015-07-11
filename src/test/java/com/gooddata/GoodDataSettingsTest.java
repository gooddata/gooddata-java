package com.gooddata;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GoodDataSettingsTest {

    private GoodDataSettings settings;

    @BeforeMethod
    public void setUp() throws Exception {
        settings = new GoodDataSettings();
    }

    @Test
    public void testHasDefaults() throws Exception {
        assertTrue(settings.getMaxConnections() > 0);
        assertTrue(settings.getConnectionTimeout() >= 0);
        assertTrue(settings.getConnectionRequestTimeout() >= 0);
        assertTrue(settings.getSocketTimeout() >= 0);
    }

    @Test
    public void testSetSeconds() throws Exception {
        settings.setConnectionTimeoutSeconds(53);
        settings.setConnectionRequestTimeoutSeconds(69);
        settings.setSocketTimeoutSeconds(71);

        assertEquals(53000, settings.getConnectionTimeout());
        assertEquals(69000, settings.getConnectionRequestTimeout());
        assertEquals(71000, settings.getSocketTimeout());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void setNegativeConnectionTimeoutFails() throws Exception {
        settings.setConnectionTimeout(-3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void setNegativeConnectionRequestTimeoutFails() throws Exception {
        settings.setConnectionRequestTimeout(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void setNegativeSocketTimeoutFails() throws Exception {
        settings.setSocketTimeout(-5);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void setZeroMaxConnectionsFails() throws Exception {
        settings.setMaxConnections(0);
    }
}