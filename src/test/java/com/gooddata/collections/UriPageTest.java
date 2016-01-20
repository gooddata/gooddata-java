package com.gooddata.collections;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class UriPageTest {

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*pageUri can't be null.*"
    )
    public void testNullConstructorArgument() {
        new UriPage(null);
    }

    @Test
    public void testGetPageUri() throws Exception {
        final UriPage uri = new UriPage("uri");
        assertThat(uri.getPageUri(null).toString(), is("uri"));
    }

    @Test
    public void test() throws Exception {
        final UriPage uri = new UriPage("uri?offset=god&limit=10");
        final UriComponentsBuilder builder = fromUriString("/this/is/{template}").query("other=false");

        uri.updateWithPageParams(builder);

        final UriComponents components = builder.build();
        assertThat(components, is(notNullValue()));
        assertThat(components.getPath(), is("/this/is/{template}"));
        assertThat(components.getQueryParams(), hasEntry("other", singletonList("false")));
        assertThat(components.getQueryParams(), hasEntry("offset", singletonList("god")));
        assertThat(components.getQueryParams(), hasEntry("limit", singletonList("10")));
    }

}
