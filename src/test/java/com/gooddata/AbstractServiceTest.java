package com.gooddata;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractServiceTest {

    private AbstractService service;

    @Mock
    private RestTemplate restTemplate;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new AbstractService(restTemplate) {};
        final ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(restTemplate.execute(anyString(), any(HttpMethod.class), any(RequestCallback.class), any(ResponseExtractor.class)))
                .thenReturn(response);
    }

    @Test
    public void pollShouldSucceedWhenUnderTimeout() throws Exception {
        PollHandler<?, ?> handler = mock(PollHandler.class);
        when(handler.isFinished(any(ClientHttpResponse.class))).thenReturn(false);
        when(handler.isDone()).thenReturn(false, true);

        service.poll(handler, 5, TimeUnit.SECONDS);
    }

    @Test(expectedExceptions = GoodDataException.class, expectedExceptionsMessageRegExp = ".*timeout.*")
    public void pollShouldThrowExceptionWhenOverTimeout() throws Exception {
        PollHandler<?, ?> handler = mock(PollHandler.class);
        service.poll(handler, 5, TimeUnit.SECONDS);
    }
}
