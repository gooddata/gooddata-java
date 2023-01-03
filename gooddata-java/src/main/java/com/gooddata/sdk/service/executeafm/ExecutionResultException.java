/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.executeafm;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;
import org.springframework.http.HttpStatus;

/**
 * Signals problem while fetching {@link ExecutionResult} including it's computation
 */
public class ExecutionResultException extends GoodDataException {

    /**
     * Creates new instance
     * @param cause cause
     */
    ExecutionResultException(GoodDataRestException cause) {
        super(computeMessage(cause), cause);
    }

    private static String computeMessage(GoodDataRestException cause) {
        switch (HttpStatus.valueOf(cause.getStatusCode())) {
            case BAD_REQUEST:
                return "AFM execution is not computable";
            case GONE:
                return "AFM execution result no longer available";
            case PAYLOAD_TOO_LARGE:
                return "AFM execution result is too large";
            default:
                return "AFM execution failed for unknown reason";
        }
    }
}
