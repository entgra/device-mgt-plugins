/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.wso2.carbon.device.mgt.mobile.android.api.impl;

import com.google.api.client.http.HttpStatusCodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.android.api.EventReceiverAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.DeviceState;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EventBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.*;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.api.util.AndroidAPIUtils;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/events")
public class EventReceiverAPIImpl implements EventReceiverAPI {

    private static final Log log = LogFactory.getLog(EventReceiverAPIImpl.class);

    @POST
    @Path("/publish")
    @Override
    public Response publishEvents(@Valid EventBeanWrapper eventBeanWrapper) {
        try{
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Message message = androidService.publishEvents(eventBeanWrapper);
            return Response.status(Integer.parseInt(message.getResponseCode())).entity(message.getResponseMessage()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Error occurred while getting the Data publisher Service instance.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occurred while publishing events.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (DeviceManagementException e) {
            String errorMessage = "Error occurred while checking Operation Analytics is Enabled.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @GET
    @Override
    public Response retrieveAlerts(@QueryParam("id")
                                       @Size(min = 2, max = 45)
                                       String deviceId,
                                   @QueryParam("from") long from,
                                   @QueryParam("to") long to,
                                       @Size(min = 2, max = 45)
                                   @QueryParam("type") String type,
                                   @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        try{
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            List<DeviceState> deviceStates = androidService.retrieveAlerts(deviceId, from, to, type, ifModifiedSince);
            return Response.status(Response.Status.OK).entity(deviceStates).build();
        } catch (BadRequestException e){
            String errorMessage = "Request must contain " +
                    "the device identifier. Optionally, both from and to value should be present to get " +
                    "alerts between times.";
            log.error(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Class not found";
            log.error(errorMessage, e);
            return Response.status(Response.Status.NOT_FOUND).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_NOT_FOUND)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while retrieving alerts";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }
}
