/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static net.jadler.Jadler.onRequest;

public class PollHandlerIT extends AbstractGoodDataIT {

    private static final String PATH = "/foo";
    private static final String PARAM = "q";
    private static final String VALUE = "eAEdizEOgCAQBL9CtraBwsLOR1gZC5QzuUROA2csiH8X7DYzswXKehAGTPKvYBJdZ1ITaGdh5VPQ%0AIZIm3jKGuUB8bP34%2BERCOVfNoQLbO%2BvwLh281nq9ldpheT%2FgtSHo%0A";
    private static final String URI = PATH + "?" + PARAM + "=" + VALUE;

    private PollingService service;

    @BeforeMethod
    public void setUp() throws Exception {
        service = new PollingService(gd.getRestTemplate());

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PATH)
                .havingParameterEqualTo(PARAM, VALUE)
            .respond()
                .withStatus(200)
        ;
    }

    @Test
    public void shouldPollOnEncodedUri() throws Exception {
        service.test(URI).get();
    }

    private static class PollingService extends AbstractService {

        PollingService(final RestTemplate restTemplate) {
            super(restTemplate);
        }

        FutureResult<Void> test(final String uri) {
            return new PollResult<>(this, new SimplePollHandler<Void>(uri, Void.class) {
                @Override
                public void handlePollException(final GoodDataRestException e) {
                    throw e;
                }
            });
        }
    }
}