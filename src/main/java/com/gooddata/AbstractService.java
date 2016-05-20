/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Parent for GoodData services providing helpers for REST API calls and polling.
 */
public abstract class AbstractService {

    public static final Integer WAIT_BEFORE_RETRY_IN_MILLIS = 5 * 1000;

    protected final RestTemplate restTemplate;

    protected final ObjectMapper mapper = new ObjectMapper();

    private final ResponseExtractor<ClientHttpResponse> reusableResponseExtractor = new ResponseExtractor<ClientHttpResponse>() {
        @Override
        public ClientHttpResponse extractData(final ClientHttpResponse response) throws IOException {
            return new ReusableClientHttpResponse(response);
        }
    };


    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     *
     * @param restTemplate RESTful HTTP Spring template
     */
    public AbstractService(RestTemplate restTemplate) {
        this.restTemplate = notNull(restTemplate, "restTemplate");
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
                Thread.sleep(WAIT_BEFORE_RETRY_IN_MILLIS);
            } catch (InterruptedException e) {
                throw new GoodDataException("interrupted");
            }
        }
    }

    final <P> boolean pollOnce(final PollHandler<P,?> handler) {
        notNull(handler, "handler");
        final ClientHttpResponse response;
        try {
            response = restTemplate.execute(handler.getPollingUri(), GET, null, reusableResponseExtractor);
        } catch (GoodDataRestException e) {
            handler.handlePollException(e);
            throw new GoodDataException("Handler " + handler.getClass().getName() + " didn't handle exception", e);
        }

        try {
            if (handler.isFinished(response)) {
                final P data = extractData(response, handler.getPollClass());
                handler.handlePollResult(data);
            } else if (HttpStatus.Series.CLIENT_ERROR.equals(response.getStatusCode().series())) {
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
                statusCode = response.getStatusCode();
                rawStatusCode = response.getRawStatusCode();
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
        public HttpStatus getStatusCode() throws IOException {
            return statusCode;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return rawStatusCode;
        }

        @Override
        public String getStatusText() throws IOException {
            return statusText;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            return body != null ? new ByteArrayInputStream(body) : null;
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

    protected URI expandUri(UriTemplate template, Object... variables) {
        return expandUri(template.toString(), variables);
    }

    protected URI expandUri(String template, Object... variables) {
        return restTemplate.getUriTemplateHandler().expand(template, variables);
    }

}
