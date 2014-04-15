/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * TODO
 */
public class DatasetManifestTest {

    private static final String SERIALIZED = "{\n"
            + "   \"dataSetSLIManifest\" : {\n"
            + "      \"parts\" : [\n"
            + "         {\n"
            + "            \"columnName\" : \"f_person.f_shoesize\",\n"
            + "            \"populates\" : [\n"
            + "               \"fact.person.shoesize\"\n"
            + "            ],\n"
            + "            \"mode\" : \"FULL\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"columnName\" : \"f_person.nm_name\",\n"
            + "            \"populates\" : [\n"
            + "               \"attr.person.id.name\"\n"
            + "            ],\n"
            + "            \"mode\" : \"FULL\",\n"
            + "            \"referenceKey\" : 1\n"
            + "         },\n"
            + "         {\n"
            + "            \"columnName\" : \"d_person_role.nm_xrole\",\n"
            + "            \"populates\" : [\n"
            + "               \"attr.person.xrole\"\n"
            + "            ],\n"
            + "            \"mode\" : \"FULL\",\n"
            + "            \"referenceKey\" : 1\n"
            + "         },\n"
            + "         {\n"
            + "            \"columnName\" : \"f_person.f_age\",\n"
            + "            \"populates\" : [\n"
            + "               \"fact.person.age\"\n"
            + "            ],\n"
            + "            \"mode\" : \"FULL\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"columnName\" : \"d_person_department.nm_xdepartment\",\n"
            + "            \"populates\" : [\n"
            + "               \"attr.person.xdepartment\"\n"
            + "            ],\n"
            + "            \"mode\" : \"FULL\",\n"
            + "            \"referenceKey\" : 1\n"
            + "         }\n"
            + "      ],\n"
            + "      \"file\" : \"dataset.person.csv\",\n"
            + "      \"dataSet\" : \"dataset.person\"\n"
            + "   }\n"
            + "}\n";

    @Test
    public void testDeser() throws Exception {
        final DatasetManifest manifest = new ObjectMapper().readValue(SERIALIZED, DatasetManifest.class);
        assertEquals("dataset.person", manifest.getDataSet());
        assertEquals("dataset.person.csv", manifest.getFile());
        assertEquals(5, manifest.getParts().size());

        final DatasetManifest.Part part = manifest.getParts().get(4);
        assertEquals("d_person_department.nm_xdepartment", part.getColumnName());
        assertArrayEquals(new String[]{"attr.person.xdepartment"}, part.getPopulates().toArray());
        assertEquals("FULL", part.getUploadMode());
        assertEquals(Integer.valueOf(1), part.getReferenceKey());

    }
}
