/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * two nested classes have toString() method implemented using {@link GoodDataToStringBuilder} and check if
 * output of their toString() methods are as expected
 */
public class GoodDataToStringBuilderTest {

    //static nested classes are denoted with outer class, we need a name of outer class in this test
    private static final String OUTER_CLASS_NAME =  GoodDataToStringBuilderTest.class.getSimpleName();

    private static final String TEST_CLASS_NAME =  TestClass.class.getSimpleName();

    private static final String ANOTHER_TEST_CLASS_NAME =  AnotherTestClass.class.getSimpleName();

    private static final String CLASS_WITH_ADDITIONAL_PARAM = ClassWithAdditionalParam.class.getSimpleName();

    private static final String STRING_FIELD_VALUE = "STRING_FIELD";

    private static final Long LONG_FIELD_VALUE = 10L;

    private static final List<Integer> LIST_VALUE = Arrays.asList(1, 2, 3);

    private static final String ANOTHER_TEST_CLASS_STRING_EXPECTED = format("%s.%s[stringField=%s]",
            OUTER_CLASS_NAME, ANOTHER_TEST_CLASS_NAME, STRING_FIELD_VALUE);

    @Test
    public void testDefaultToStringNonRecursive() throws Exception {
        final AnotherTestClass anotherTestClass = new AnotherTestClass();

        final String toStringResult = anotherTestClass.toString();

        assertThat(toStringResult, is(notNullValue()));
        assertThat(toStringResult, is(ANOTHER_TEST_CLASS_STRING_EXPECTED));

    }

    /**
     * TestClass have field reference to {@link AnotherTestClass}
     */
    @Test
    public void testDefaultToStringRecursive() {
        final TestClass testClass = new TestClass();

        final String toStringResult = testClass.toString();

        assertThat(toStringResult, is(notNullValue()));
        assertThat(toStringResult, is(format("%s.%s[stringField=%s,longField=%d,list=%s,anotherTestClass=%s]",
                OUTER_CLASS_NAME, TEST_CLASS_NAME, STRING_FIELD_VALUE, LONG_FIELD_VALUE, LIST_VALUE.toString(),
                ANOTHER_TEST_CLASS_STRING_EXPECTED)));
    }

    @Test
    public void testToStringIncludeAdditionalParam() {
        final ClassWithAdditionalParam classWithAdditionalParam = new ClassWithAdditionalParam();

        final String toStringResult = classWithAdditionalParam.toString();

        assertThat(toStringResult, is(notNullValue()));
        assertThat(toStringResult, is(format("%s.%s[newField=%s]",
                OUTER_CLASS_NAME, CLASS_WITH_ADDITIONAL_PARAM, STRING_FIELD_VALUE)));
    }

    static class TestClass {
        private Map<String, String> links = new LinkedHashMap<String, String>() {{
            put("self", "/gdc/link");
        }};
        private String stringField = STRING_FIELD_VALUE;
        private Long longField = LONG_FIELD_VALUE;
        private String toBeExcludedField = "SHOULD_NOT_BE_THERE";
        private String anotherToBeExcludedField = "ALSO_SHOULD_NOT_BE_THERE";
        private List<Integer> list = LIST_VALUE;
        private AnotherTestClass anotherTestClass = new AnotherTestClass();

        public Map<String, String> getLinks() {
            return links;
        }

        public String getStringField() {
            return stringField;
        }

        public Long getLongField() {
            return longField;
        }

        public String getToBeExcludedField() {
            return toBeExcludedField;
        }

        public String getAnotherToBeExcludedField() {
            return anotherToBeExcludedField;
        }

        public List<Integer> getList() {
            return list;
        }

        public AnotherTestClass getTestClass2() {
            return anotherTestClass;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this, "toBeExcludedField", "anotherToBeExcludedField");
        }
    }

    static class AnotherTestClass {
        private Map<String, String> links = new LinkedHashMap<String, String>() {{
            put("self", "/gdc/link2");
        }};

        private String stringField = STRING_FIELD_VALUE;

        public String getStringField() {
            return stringField;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    static class ClassWithAdditionalParam {
        @Override
        public String toString() {
            return new GoodDataToStringBuilder(this).append("newField", STRING_FIELD_VALUE).toString();
        }
    }
}
