package com.gooddata.gdc;

import com.gooddata.AbstractGoodDataIT;
import org.testng.annotations.Test;

import static net.jadler.Jadler.onRequest;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GdcServiceIT extends AbstractGoodDataIT {

    @Test
    public void shouldReturnGdc() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
            .respond()
                .withBody(readResource("/gdc/gdc.json"))
                .withStatus(200);

        final Gdc gdc = gd.getGdcService().getGdc();
        assertThat(gdc, is(notNullValue()));
        assertThat(gdc.getUserStagingLink(), is("/uploads"));
    }

    @Test
    public void shouldUseProperVersionHeader() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
                .havingHeaderEqualTo("Accept", "application/json;version=1")
                .respond()
                .withBody(readResource("/gdc/gdc.json"))
                .withStatus(200);

        final Gdc gdc = gd.getGdcService().getGdc();
        assertThat(gdc, is(notNullValue()));
    }
}
