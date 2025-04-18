/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.plugins.mobile.android.api.exception;

import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidConstants;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidDeviceUtils;

import javax.validation.ConstraintViolation;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Set;

public class ParameterValidationException extends WebApplicationException {

    private String message;
    public <T> ParameterValidationException(Set<ConstraintViolation<T>> violations) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(AndroidDeviceUtils.getConstraintViolationErrorDTO(violations))
                .header(AndroidConstants.HEADER_CONTENT_TYPE, AndroidConstants.APPLICATION_JSON)
                .build());

        //Set the error message
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            stringBuilder.append(violation.getRootBeanClass().getSimpleName());
            stringBuilder.append(".");
            stringBuilder.append(violation.getPropertyPath());
            stringBuilder.append(": ");
            stringBuilder.append(violation.getMessage());
            stringBuilder.append(", ");
        }
        message = stringBuilder.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }

}
