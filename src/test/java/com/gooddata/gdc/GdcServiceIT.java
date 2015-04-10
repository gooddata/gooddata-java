package com.gooddata.gdc;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.util.ResourceUtils;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readFromResource;
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
                .withBody(readFromResource("/gdc/gdc.json"))
                .withStatus(200);

        final Gdc gdc = gd.getGdcService().getGdc();
        assertThat(gdc, is(notNullValue()));
        assertThat(gdc.getUserStagingLink(), is("/uploads"));
    }
}
