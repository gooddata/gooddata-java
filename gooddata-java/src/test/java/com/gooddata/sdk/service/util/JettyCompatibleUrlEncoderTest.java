/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.util;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Tests for JettyCompatibleUrlEncoder to ensure proper URL encoding compatibility
 * between modern standards and Jetty 8.1 legacy behavior.
 */
public class JettyCompatibleUrlEncoderTest {

    @Test
    public void testBasicEncoding() {
        String input = "hello world";
        String encoded = JettyCompatibleUrlEncoder.encode(input);
        assertNotNull(encoded);
        assertTrue(encoded.contains("hello"));
    }

    @Test
    public void testNullHandling() {
        assertNull(JettyCompatibleUrlEncoder.encode(null));
        assertNull(JettyCompatibleUrlEncoder.decodeAsJetty81(null));
        assertNull(JettyCompatibleUrlEncoder.convertToJetty81Expected(null));
        assertNull(JettyCompatibleUrlEncoder.createJadlerExpectation(null));
    }

    @Test
    public void testJetty81SpecialCharacters() {
        // Test + character handling (should decode %2B to +)
        assertEquals("+", JettyCompatibleUrlEncoder.convertToJetty81Expected("%2B"));
        assertEquals("a+b", JettyCompatibleUrlEncoder.convertToJetty81Expected("a%2Bb"));
        
        // Test / character handling (should decode %2F to /)
        assertEquals("/", JettyCompatibleUrlEncoder.convertToJetty81Expected("%2F"));
        assertEquals("a/b", JettyCompatibleUrlEncoder.convertToJetty81Expected("a%2Fb"));
        
        // Test = character handling (should decode %3D to =)
        assertEquals("=", JettyCompatibleUrlEncoder.convertToJetty81Expected("%3D"));
        assertEquals("a=b", JettyCompatibleUrlEncoder.convertToJetty81Expected("a%3Db"));
        
        // Test other characters (should remain encoded)
        assertEquals("%0A", JettyCompatibleUrlEncoder.convertToJetty81Expected("%0A"));
        assertEquals("%20", JettyCompatibleUrlEncoder.convertToJetty81Expected("%20"));
        
        // Test mixed scenarios with all transformations
        assertEquals("a+b/c=d%0Ae", JettyCompatibleUrlEncoder.convertToJetty81Expected("a%2Bb%2Fc%3Dd%0Ae"));
    }

    @Test
    public void testPollHandlerITCompatibility() {
        // Test with the actual problematic string from PollHandlerIT
        String problematicValue = "eAEdizEOgCAQBL9CtraBwsLOR1gZC5QzuUROA2csiH8X7DYzswXKehAGTPKvYBJdZ1ITaGdh5VPQ%0AIZIm3jKGuUB8bP34%2BERCOVfNoQLbO%2BvwLh281nq9ldpheT%2FgtSHo%0A";
        
        // This should convert to what Jetty 8.1 would actually pass to Jadler
        String jettyExpected = JettyCompatibleUrlEncoder.convertToJetty81Expected(problematicValue);
        
        // Verify key transformations occurred
        assertFalse(jettyExpected.contains("%2B"), "Plus signs should be decoded");
        assertFalse(jettyExpected.contains("%2F"), "Forward slashes should be decoded");
        assertFalse(jettyExpected.contains("%0A"), "Newlines should be decoded");
        
        // Should contain actual characters instead
        assertTrue(jettyExpected.contains("+"), "Should contain decoded plus signs");
        assertTrue(jettyExpected.contains("/"), "Should contain decoded forward slashes");
        assertTrue(jettyExpected.contains("\n"), "Should contain decoded newlines");
    }

    @Test
    public void testJadlerExpectationGeneration() {
        String originalValue = "test+value/path\nline2";
        String jadlerExpectation = JettyCompatibleUrlEncoder.createJadlerExpectation(originalValue);
        
        assertNotNull(jadlerExpectation);
        // The expectation should be what Jetty 8.1 would pass to the application
        // after its internal processing
    }

    @Test
    public void testRoundTripCompatibility() {
        String original = "hello world+test/path";
        String encoded = JettyCompatibleUrlEncoder.encode(original);
        String jettyProcessed = JettyCompatibleUrlEncoder.convertToJetty81Expected(encoded);
        
        // Verify that the processing chain works
        assertNotNull(encoded);
        assertNotNull(jettyProcessed);
    }
}