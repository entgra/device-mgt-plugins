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

import com.google.gson.JsonParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidDeviceUtils;

import javax.naming.AuthenticationException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Handle the cxf level exceptions.
 */
public class GlobalThrowableMapper implements ExceptionMapper {
    private static final Log log = LogFactory.getLog(GlobalThrowableMapper.class);

    private ErrorDTO e500 = new ErrorDTO();

    GlobalThrowableMapper() {
        e500.setCode((long) 500);
        e500.setMessage("Internal server error.");
        e500.setMoreInfo("");
        e500.setDescription("The server encountered an internal error. Please contact administrator.");

    }

    @Override
    public Response toResponse(Throwable e) {

        if (e instanceof JsonParseException) {
            String errorMessage = "Malformed request body.";
            if (log.isDebugEnabled()) {
                log.debug(errorMessage, e);
            }
            return AndroidDeviceUtils.buildBadRequestException(errorMessage).getResponse();
        }
        if (e instanceof NotFoundException) {
                return ((NotFoundException) e).getResponse();
        }
        if (e instanceof BadRequestException) {
            return ((BadRequestException) e).getResponse();
        }
        if (e instanceof UnexpectedServerErrorException) {
            if (log.isDebugEnabled()) {
                log.debug("Unexpected server error", e);
            }
            return ((UnexpectedServerErrorException) e).getResponse();
        }
        if (e instanceof ConstraintViolationException) {
            if (log.isDebugEnabled()) {
                log.debug("Constraint violation issue.", e);
                return ((ParameterValidationException) e).getResponse();
            }
        }
        if (e instanceof IllegalArgumentException) {
            ErrorDTO errorDetail = new ErrorDTO();
            errorDetail.setCode((long) 400);
            errorDetail.setMoreInfo("");
            errorDetail.setMessage("");
            errorDetail.setDescription(e.getMessage());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorDetail)
                    .build();
        }
        if (e instanceof ClientErrorException) {
            if (log.isDebugEnabled()) {
                log.debug("Client error", e);
            }
            return ((ClientErrorException) e).getResponse();
        }
        if (e instanceof AuthenticationException) {
            ErrorDTO errorDetail = new ErrorDTO();
            errorDetail.setCode((long) 401);
            errorDetail.setMoreInfo("");
            errorDetail.setMessage("");
            errorDetail.setDescription(e.getMessage());
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(errorDetail)
                    .build();
        }
        if (e instanceof ForbiddenException) {
            if (log.isDebugEnabled()) {
                log.debug("Resource forbidden", e);
            }
            return ((ForbiddenException) e).getResponse();
        }
        //unknown exception log and return
            log.error("An Unknown exception has been captured by global exception mapper.", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
                .entity(e500).build();
    }
}
