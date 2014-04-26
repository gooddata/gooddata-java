/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.gooddata.Validate.notNull;
import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;

/**
 */
public abstract class AbstractService {

    public static Integer WAIT_BEFORE_RETRY_IN_MILLIS = 5 * 1000;

    protected final RestTemplate restTemplate;

    protected final ObjectMapper mapper = new ObjectMapper();

    protected final RequestCallback noopRequestCallback = new RequestCallback() {
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
        this.restTemplate = notNull(restTemplate, "restTemplate");
    }

    final <T> T poll(final PollHandler<T> handler, long timeout, final TimeUnit unit) {
        notNull(handler, "handler");
        final long start = System.currentTimeMillis();
        while (true) {
            if (pollOnce(handler)) {
                return handler.getResult();
            }
            if (unit != null && start + unit.toMillis(timeout) > System.currentTimeMillis()) {
                throw new GoodDataException("timeout");
            }

            try {
                Thread.sleep(WAIT_BEFORE_RETRY_IN_MILLIS);
            } catch (InterruptedException e) {
                throw new GoodDataException("interrupted");
            }
        }
    }

    final <T> boolean pollOnce(final PollHandler<T> handler) {
        notNull(handler, "handler");
        final ClientHttpResponse response = restTemplate.execute(handler.getPollingUri(), GET, noopRequestCallback,
                reusableResponseExtractor);

        try {
            if (handler.isFinished(response)) {
                final T data = extractData(response, handler.getResultClass());
                handler.setResult(data);
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
