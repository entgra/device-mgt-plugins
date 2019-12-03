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
 *
 *   Copyright (c) 2018, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */
package org.wso2.carbon.device.mgt.mobile.android.api.impl;

import com.google.api.client.http.HttpStatusCodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.mobile.android.api.DeviceManagementAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidApplication;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidDevice;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestExceptionDup;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceManagementAPIImpl implements DeviceManagementAPI {

    private static final Log log = LogFactory.getLog(DeviceManagementAPIImpl.class);

    @PUT
    @Path("/{id}/applications")
    @Override
    public Response updateApplicationList(@PathParam("id")
                                          @NotNull
                                          @Size(min = 2, max = 45)
                                          @Pattern(regexp = "^[A-Za-z0-9]*$")
                                          String id, List<AndroidApplication> androidApplications) {
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Message responseMessage = androidService.updateApplicationList(id, androidApplications);
            return Response.status(Response.Status.ACCEPTED).entity(responseMessage).build();
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while modifying the application list.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    @PUT
    @Path("/{id}/pending-operations")
    @Override
    public Response getPendingOperations(@QueryParam("disableGoogleApps") boolean disableGoogleApps,
                                         @PathParam("id") String id,
                                         @HeaderParam("If-Modified-Since") String ifModifiedSince,
                                         List<? extends Operation> resultOperations) {
        if (id == null || id.isEmpty()) {
            String msg = "Device identifier is null or empty, hence returning device not found";
            log.error(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            List<? extends Operation> pendingOperations = androidService
                    .getPendingOperations(id, resultOperations, disableGoogleApps);
            return Response.status(Response.Status.CREATED).entity(pendingOperations).build();
        } catch (InvalidDeviceException e) {
            String msg = "Device identifier is invalid. Device identifier " + id;
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while getting pending operations of the device.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build());
        }
    }

    @POST
    @Override
    public Response enrollDevice(@Valid AndroidDevice androidDevice) {
        if (androidDevice == null) {
            String errorMessage = "The payload of the android device enrollment is incorrect.";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Response response = androidService.enrollDevice(androidDevice);
            return response;
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while enrolling the android, which carries the id '" +
                    androidDevice.getDeviceIdentifier() + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    @GET
    @Path("/{id}/status")
    @Override
    public Response isEnrolled(@PathParam("id") String id, @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Message responseMessage = androidService.isEnrolled(id, deviceIdentifier);
            return Response.status(Integer.parseInt(responseMessage.getResponseCode())).entity(responseMessage).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while checking enrollment status of the device.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build());
        }
    }

    @PUT
    @Path("/{id}")
    @Override
    public Response modifyEnrollment(@PathParam("id") String id, @Valid AndroidDevice androidDevice) {
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            if (androidService.modifyEnrollment(id, androidDevice)) {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.ACCEPTED.toString());
                responseMessage.setResponseMessage("Enrollment of Android device that " +
                        "carries the id '" + id + "' has successfully updated");
                return Response.status(Response.Status.ACCEPTED).entity(responseMessage).build();
            } else {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.NOT_MODIFIED.toString());
                responseMessage.setResponseMessage("Enrollment of Android device that " +
                        "carries the id '" + id + "' has not been updated");
                return Response.status(Response.Status.NOT_MODIFIED).entity(responseMessage).build();
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while modifying enrollment of the Android device that carries the id '" +
                    id + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (BadRequestExceptionDup e){
            String msg = "Invalid request";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(msg).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Override
    public Response disEnrollDevice(@PathParam("id") String id) {
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            if (androidService.disEnrollDevice(id)) {
                String msg = "Android device that carries id '" + id + "' is successfully ";
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.OK.toString());
                responseMessage.setResponseMessage(msg + "dis-enrolled");
                return Response.status(Response.Status.OK).entity(responseMessage).build();
            } else {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.NOT_FOUND.toString());
                responseMessage.setResponseMessage("Android device that carries id '" + id + "' is not available");
                return Response.status(Response.Status.NOT_FOUND).entity(responseMessage).build();
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while %s the Android device that carries the id '" + id + "'";
            msg = String.format(msg, "dis-enrolling");
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }


}
