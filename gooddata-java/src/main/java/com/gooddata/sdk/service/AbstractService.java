/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Parent for GoodData services providing helpers for REST API calls and polling.
 */
public abstract class AbstractService {
    protected final WebClient webClient;
    protected final GoodDataSettings settings;
    protected final ObjectMapper mapper = new ObjectMapper();

    public AbstractService(WebClient webClient, GoodDataSettings settings) {
        this.webClient = notNull(webClient, "webClient");
        this.settings = settings;
    }

    final <R> R poll(final PollHandler<?, R> handler, long timeout, final TimeUnit unit) {
        notNull(handler, "handler");
        final long start = System.currentTimeMillis();
        while (true) {
            if (pollOnce(handler)) {
                return handler.getResult();
            }
            if (unit != null && start + unit.toMillis(timeout) < System.currentTimeMillis()) {
                throw new GoodDataException("timeout");
            }

            try {
                Thread.sleep(settings.getPollSleep());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new GoodDataException("interrupted");
            }
        }
    }

    final <P> boolean pollOnce(final PollHandler<P, ?> handler) {
        notNull(handler, "handler");

        try {
            ClientResponse response = webClient.get()
                    .uri(handler.getPolling())
                    .exchangeToMono(resp -> Mono.just(resp))
                    .block();

            if (response == null) {
                throw new GoodDataException("No response received for polling request");
            }

            int statusCode = response.statusCode().value();

            if (handler.isFinished(response)) {
                P data = extractData(response, handler.getPollClass());
                handler.handlePollResult(data);
            } else if (statusCode >= 400 && statusCode < 500) {
                throw new GoodDataException(
                        format("Polling returned client error HTTP status %s", statusCode)
                );
            } else if (statusCode >= 500) {
                throw new GoodDataException(
                        format("Polling returned server error HTTP status %s", statusCode)
                );
            }
        } catch (Exception e) {
            if (e instanceof GoodDataRestException) {
                handler.handlePollException((GoodDataRestException) e);
                throw new GoodDataException("Handler " + handler.getClass().getName() + " didn't handle exception", e);
            } else {
                throw new GoodDataException("Error during polling", e);
            }
        }
        return handler.isDone();
    }

    protected final <T> T extractData(ClientResponse response, Class<T> cls) {
        notNull(response, "response");
        notNull(cls, "cls");
        if (Void.class.isAssignableFrom(cls)) {
            return null;
        }
        // CHANGED: get response body as class instance using WebClient API
        return response.bodyToMono(cls).block();
    }

    protected static class OutputStreamResponseExtractor {
        private final OutputStream output;

        public OutputStreamResponseExtractor(OutputStream output) {
            this.output = output;
        }

        // CHANGED: get response body as bytes from ClientResponse and write to OutputStream
        public int extractData(ClientResponse response) throws IOException {
            byte[] bytes = response.bodyToMono(byte[].class).block();
            if (bytes != null) {
                output.write(bytes);
                return bytes.length;
            }
            return 0;
        }
    }
}
