/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageableStreamerTest {

    private final List<String> list1 = asList("a", "b");
    private final List<String> list2 = asList("c", "d");

    @Mock
    private PageableList firstPageableList;

    @Mock
    private PageableList secondPageableList;

    @Mock
    private Page pageTwo;

    @Before
    public void setUp() {
        when(firstPageableList.iterator()).thenReturn(list1.iterator());
        when(firstPageableList.stream()).thenReturn(list1.stream());
        when(secondPageableList.iterator()).thenReturn(list2.iterator());
        when(secondPageableList.stream()).thenReturn(list2.stream());
        when(firstPageableList.getNextPage()).thenReturn(pageTwo);
    }

    @Test
    public void stream() {

        final Map<Page, PageableList<String>> pages = new HashMap<Page, PageableList<String>>() {{
            put(new PageRequest(2), firstPageableList);
            put(pageTwo, secondPageableList);
        }};

        final List<String> expectedResult = asList("a", "b", "c", "d");
        final List<String> actualResult =
                PageableStreamer.stream(pages::get, 2).collect(Collectors.toList());
        assertThat(actualResult, equalTo(expectedResult));
    }
}
