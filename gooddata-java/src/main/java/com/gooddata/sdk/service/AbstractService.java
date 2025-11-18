/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Parent for GoodData services providing helpers for REST API calls and polling.
 */
public abstract class AbstractService {

    protected final RestTemplate restTemplate;
    protected final HttpClientAdapter httpClientAdapter;

    private final GoodDataSettings settings;

    protected final ObjectMapper mapper = new ObjectMapper();

    private final ResponseExtractor<ClientHttpResponse> reusableResponseExtractor = ReusableClientHttpResponse::new;

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public AbstractService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        this.restTemplate = notNull(restTemplate, "restTemplate");
        this.httpClientAdapter = new RestTemplateHttpClientAdapter(restTemplate);
        this.settings = notNull(settings, "settings");
    }

    /**
     * Constructor using HttpClientAdapter for modern HTTP client support.
     *
     * @param httpClientAdapter HTTP client adapter
     * @param settings settings
     */
    public AbstractService(final HttpClientAdapter httpClientAdapter, final GoodDataSettings settings) {
        this.httpClientAdapter = notNull(httpClientAdapter, "httpClientAdapter");
        this.restTemplate = httpClientAdapter instanceof RestTemplateHttpClientAdapter 
            ? ((RestTemplateHttpClientAdapter) httpClientAdapter).getRestTemplate() 
            : null;
        this.settings = notNull(settings, "settings");
    }

    final <R> R poll(final PollHandler<?,R> handler, long timeout, final TimeUnit unit) {
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

    final <P> boolean pollOnce(final PollHandler<P,?> handler) {
        notNull(handler, "handler");
        final ClientHttpResponse response;
        try {
            response = httpClientAdapter.execute(handler.getPolling(), GET, null, reusableResponseExtractor);
        } catch (GoodDataRestException e) {
            handler.handlePollException(e);
            throw new GoodDataException("Handler " + handler.getClass().getName() + " didn't handle exception", e);
        }

        try {
            if (handler.isFinished(response)) {
                final P data = extractData(response, handler.getPollClass());
                handler.handlePollResult(data);
            } else if (HttpStatus.Series.CLIENT_ERROR.equals(HttpStatus.Series.resolve(response.getStatusCode().value()))) {
                throw new GoodDataException(
                        format("Polling returned client error HTTP status %s", response.getStatusCode().value())
                );
            }
        } catch (IOException e) {
            throw new GoodDataException("I/O error occurred during HTTP response extraction", e);
        }
        return handler.isDone();
    }

    protected final <T> T extractData(ClientHttpResponse response, Class<T> cls) throws IOException {
        notNull(response, "response");
        notNull(cls, "cls");
        if (Void.class.isAssignableFrom(cls)) {
            return null;
        }
        return new HttpMessageConverterExtractor<>(cls, restTemplate.getMessageConverters()).extractData(response);
    }

    private static class ReusableClientHttpResponse implements ClientHttpResponse {

        private byte[] body;
        private final HttpStatus statusCode;
        private final int rawStatusCode;
        private final String statusText;
        private final HttpHeaders headers;

        public ReusableClientHttpResponse(ClientHttpResponse response) {
            try {
                final InputStream bodyStream = response.getBody();
                if (bodyStream != null) {
                    body = FileCopyUtils.copyToByteArray(bodyStream);
                }
                statusCode = HttpStatus.resolve(response.getStatusCode().value());
                rawStatusCode = response.getStatusCode().value();
                statusText = response.getStatusText();
                headers = response.getHeaders();
            } catch (IOException e) {
                throw new RuntimeException("Unable to read from HTTP response", e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }

        @Override
        public HttpStatus getStatusCode() {
            return statusCode;
        }

        @Override
        public int getRawStatusCode() {
            return rawStatusCode;
        }

        @Override
        public String getStatusText() {
            return statusText;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public InputStream getBody() {
            return body != null ? new ByteArrayInputStream(body) : new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public void close() {
            //already closed
        }
    }

    protected static class OutputStreamResponseExtractor implements ResponseExtractor<Integer> {
        private final OutputStream output;

        public OutputStreamResponseExtractor(OutputStream output) {
            this.output = output;
        }

        @Override
        public Integer extractData(ClientHttpResponse response) throws IOException {
            return FileCopyUtils.copy(response.getBody(), output);
        }
    }

}

