/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GdcErrorTest {

    @Test
    public void testDeserialization() throws Exception {
        final GdcError err = readObjectFromResource("/gdc/gdcError.json", GdcError.class);

        assertThat(err, is(notNullValue()));
        assertThat(err.getErrorClass(), is("CLASS"));
        assertThat(err.getTrace(), is("TRACE"));
        assertThat(err.getMessage(), is("MSG"));
        assertThat(err.getComponent(), is("COMPONENT"));
        assertThat(err.getErrorId(), is("ID"));
        assertThat(err.getErrorCode(), is("CODE"));
        assertThat(err.getRequestId(), is("REQ"));

        assertThat(err.getParameters(), is(notNullValue()));
        assertThat(err.getParameters().length, is(2));
        assertThat(err.getParameters()[0].toString(), is("PARAM1"));
        assertThat(err.getParameters()[1].toString(), is("PARAM2"));
    }
}