/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URL encoding/decoding utility that provides compatibility between modern URL encoding standards
 * and legacy Jetty 8.1 behavior used by Jadler 1.3.1.
 * 
 * This bridge handles the differences in how Jetty 8.1 processes encoded characters compared
 * to modern RFC 3986 compliant implementations.
 */
public class JettyCompatibleUrlEncoder {

    /**
     * Encodes a string for use in URL parameters, compatible with Jetty 8.1 expectations.
     * 
     * @param value the string to encode
     * @return encoded string compatible with Jetty 8.1
     */
    public static String encode(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            // Standard URL encoding first
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
            
            // Apply Jetty 8.1 specific transformations
            return applyJettyCompatibilityRules(encoded);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * Decodes a URL-encoded string using Jetty 8.1 compatible rules.
     * This simulates how Jetty 8.1 would decode the parameter.
     * 
     * @param encodedValue the encoded string
     * @return decoded string as Jetty 8.1 would process it
     */
    public static String decodeAsJetty81(String encodedValue) {
        if (encodedValue == null) {
            return null;
        }
        
        try {
            // Simulate Jetty 8.1 decoding behavior
            String jettyProcessed = simulateJetty81Processing(encodedValue);
            return URLDecoder.decode(jettyProcessed, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * Converts a URL-encoded string to match Jetty 8.1 expected behavior.
     * Jetty 8.1 selectively decodes certain URL-encoded characters in parameters:
     * - %2B → + (plus sign)
     * - %2F → / (forward slash) 
     * - %3D → = (equals sign)
     * But leaves other encoded chars unchanged (%0A, %20, etc.).
     * This differs from modern RFC 3986 compliant implementations which decode all percent-encoded characters.
     * 
     * @param encoded the URL-encoded string
     * @return the string with selective decoding to match Jetty 8.1 behavior
     */
    public static String convertToJetty81Expected(String encoded) {
        if (encoded == null) {
            return null;
        }
        return encoded.replace("%2B", "+").replace("%2F", "/").replace("%3D", "=");
    }    /**
     * Applies Jetty 8.1 specific compatibility rules to encoded strings.
     */
    private static String applyJettyCompatibilityRules(String encoded) {
        // Jetty 8.1 has specific handling for certain characters
        // Keep most encoding intact, but handle edge cases
        return encoded;
    }

    /**
     * Simulates how Jetty 8.1 would process an incoming encoded parameter.
     * This helps predict what the mock server should expect.
     */
    private static String simulateJetty81Processing(String encodedValue) {
        // Jetty 8.1 processes certain encoded characters automatically
        // before passing them to the application layer
        String processed = encodedValue;
        
        // Jetty 8.1 automatically converts + to space (legacy web form behavior)
        processed = processed.replace("+", " ");
        
        return processed;
    }

    /**
     * Creates a Jadler-compatible parameter expectation from a modern encoded string.
     * This is the main method for test compatibility.
     * 
     * @param originalValue the original unencoded value
     * @return what Jadler should expect to receive from Jetty 8.1
     */
    public static String createJadlerExpectation(String originalValue) {
        if (originalValue == null) {
            return null;
        }
        
        // First encode using standard rules
        String standardEncoded = encode(originalValue);
        
        // Then convert to what Jetty 8.1 would actually pass to the application
        return convertToJetty81Expected(standardEncoded);
    }
}