package com.gooddata;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.gooddata.GoodDataSettings.Header;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
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
        assertTrue(settings.getHttpHeaders().isEmpty());
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

    @Test
    public void setHttpHeader() {
        settings.setHttpHeader("X-GDC-HOST", "secure.gooddata.com");

        assertFalse(settings.getHttpHeaders().isEmpty());
    }

    @Test
    public void setHttpHeaders() {
        settings.setHttpHeader("X-GDC-HOST", "secure.gooddata.com");

        settings.setHttpHeaders(Collections.singleton(new Header("XXX", "yyy")));

        assertTrue(settings.getHttpHeaders().size() == 1);
        assertEquals(settings.getHttpHeaders().iterator().next().getName(), "XXX");
    }

}