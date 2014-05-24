package com.gooddata;

import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.internal.Diff;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;
import static net.javacrumbs.jsonunit.core.internal.Diff.create;

/**
 * Contains Hamcrest matchers to be used with Hamcrest assertThat and other tools.
 */
public class JsonMatchers {

    /**
     * Just hide constructor of this utility class.
     */
    private JsonMatchers() {
    }

    /**
     * Creates a matcher that matches if examined object serialized to JSON matches JSON in given resource file.
     *
     * @param expectedResourceName name of resource (relative patch in resource folder)
     * @return matcher that matches if examined object serialized to JSON matches JSON in given resource file
     */
    @Factory
    public static <T> Matcher<T> serializesToJson(final String expectedResourceName) {
        return new JsonFileMatcher<>(expectedResourceName);
    }

    private static class JsonFileMatcher<T> extends BaseMatcher<T> {

        private String expectedResourceName;
        private String expectedJsonString;
        private String actualJsonString;
        private String errorString;
        private String differences;

        private JsonFileMatcher(final String expectedResourceName) {
            super();
            this.expectedResourceName = expectedResourceName;
        }

        @Override
        public boolean matches(final Object actual) {
            String expectedJsonString;
            try {
                actualJsonString = new ObjectMapper().writeValueAsString(actual);

                final URL resourceUrl = getClass().getResource(expectedResourceName);
                if (resourceUrl == null) {
                    errorString = "Resource not found. Check resource name and path: " + expectedResourceName;
                    return false;
                }
                expectedJsonString = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
            } catch (Exception e) {
                errorString = format("Exception thrown during matching. Class: %s Message: %s", e.getClass(),
                        e.getMessage());
                return false;
            }

            this.expectedJsonString = expectedJsonString;
            final Diff diff = create(expectedJsonString, actual, "fullJson", "", JsonAssert.getIgnorePlaceholder(),
                    JsonAssert.getTolerance(), JsonAssert.getTreatNullAsAbsent());
            if (!diff.similar()) {
                differences = diff.differences();
            }
            return diff.similar();
        }

        @Override
        public void describeTo(final Description description) {
            if (errorString != null) {
                description.appendText("\nError: " + errorString);
            } else {
                description.appendText(
                        format("%s in %s", safeToString(expectedJsonString), safeToString(expectedResourceName)));
                description.appendText("\n     got: ");
                description.appendText(safeToString(actualJsonString));
                description.appendText("\n    diff: ");
                description.appendText(safeToString(differences));
            }
        }

        private String safeToString(final String str) {
            return str != null ? str : "null";
        }
    }
}
