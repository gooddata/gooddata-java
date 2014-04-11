package com.gooddata;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

/**
* TODO
*/
public class StatusOkConditionCallback<T, R> implements AbstractService.ConditionCallback<T, R> {
    @Override
    public boolean finished(ClientHttpResponse response) throws IOException {
        return HttpStatus.OK.equals(response.getStatusCode());
    }
}
