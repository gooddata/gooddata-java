package com.gooddata.collections;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.fail;

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

}