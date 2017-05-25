/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Serializes Joda's {@link DateTime} fields to the ISO date time format in the UTC timezone (yyyy-MM-dd'T'HH:mm:ss.SSSZZ).
 */
public class ISODateTimeSerializer extends JsonSerializer<DateTime> {

    static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(FORMATTER.print(value));
    }
}