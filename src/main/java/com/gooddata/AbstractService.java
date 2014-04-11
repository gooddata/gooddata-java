/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;

/**
 */
public abstract class AbstractService {

    public static Integer WAIT_BEFORE_RETRY_IN_MILLIS = 5 * 1000;
    public static Integer MAX_ATTEMPTS = 5;

    protected final RestTemplate restTemplate;

    private final RequestCallback noopRequestCallback = new RequestCallback() {
        @Override
        public void doWithRequest(final ClientHttpRequest request) throws IOException {
        }
    };
    private final ResponseExtractor<ClientHttpResponse> reusableResponseExtractor = new ResponseExtractor<ClientHttpResponse>() {
        @Override
        public ClientHttpResponse extractData(final ClientHttpResponse response) throws IOException {
            return new ReusableClientHttpResponse(response);
        }
    };


    public AbstractService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public <T> T poll(URI pollingUri, Class<T> cls) {
        return poll(pollingUri, new StatusOkConditionCallback(), cls);
    }

    public <T> T poll(URI pollingUri, ConditionCallback condition, Class<T> returnClass) {
        int attempt = 0;

        while (true) {

            final ClientHttpResponse response = restTemplate.execute(pollingUri, GET, noopRequestCallback,
                    reusableResponseExtractor);

            try {
                if (condition.finished(response)) {
                    return new HttpMessageConverterExtractor<>(returnClass, restTemplate.getMessageConverters())
                            .extractData(response);
                } else if (HttpStatus.Series.CLIENT_ERROR.equals(response.getStatusCode().series())) {
                    throw new IllegalStateException(
                            format("Polling returned client error HTTP status %s", response.getStatusCode().value())
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException("I/O error occurred during HTTP response extraction", e);
            }

            if (attempt >= MAX_ATTEMPTS - 1) {
                throw new IllegalStateException(format("Max number of attempts (%s) exceeded", MAX_ATTEMPTS));
            }

            try {
                Thread.sleep(WAIT_BEFORE_RETRY_IN_MILLIS);
            } catch (InterruptedException e) {
                // do nothing
            }
            attempt++;
        }
    }

    protected <T> T extractData(ClientHttpResponse response, Class<T> cls) throws IOException {
        return new HttpMessageConverterExtractor<>(cls, restTemplate.getMessageConverters()).extractData(response);
    }


    public static interface ConditionCallback {
        boolean finished(ClientHttpResponse response) throws IOException;
    }

    public static class StatusOkConditionCallback implements ConditionCallback {
        @Override
        public boolean finished(ClientHttpResponse response) throws IOException {
            return HttpStatus.OK.equals(response.getStatusCode());
        }
    }


    private class ReusableClientHttpResponse implements ClientHttpResponse {

        private final byte[] body;
        private final HttpStatus statusCode;
        private final int rawStatusCode;
        private final String statusText;
        private final HttpHeaders headers;

        public ReusableClientHttpResponse(ClientHttpResponse response) {
            try {
                body = FileCopyUtils.copyToByteArray(response.getBody());
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
            return new ByteArrayInputStream(body);
        }

        @Override
        public void close() {
            //already closed
        }
    }

}
