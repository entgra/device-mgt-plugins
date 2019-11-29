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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.policy.mgt.Policy;
import org.wso2.carbon.device.mgt.common.policy.mgt.ProfileFeature;
import org.wso2.carbon.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.mobile.android.api.DeviceManagementAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidApplication;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidDevice;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.NotFoundException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

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
import java.util.ArrayList;
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
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            androidService.getPendingOperations(id, resultOperations);
        } catch (DeviceManagementException e) {
            String msg = "Issue in retrieving device management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }

        List<? extends Operation> pendingOperations;
        try {
            pendingOperations = AndroidDeviceUtils.getPendingOperations(deviceIdentifier, !disableGoogleApps);
        } catch (OperationManagementException e) {
            String msg = "Issue in retrieving operation management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
        return Response.status(Response.Status.CREATED).entity(pendingOperations).build();
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
            return Response.status(Response.Status.NOT_FOUND).entity(responseMessage).build();

        } catch (DeviceManagementException e) {
            String msg = "Error occurred while checking enrollment status of the device.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    @PUT
    @Path("/{id}")
    @Override
    public Response modifyEnrollment(@PathParam("id") String id, @Valid AndroidDevice androidDevice) {
        AndroidService androidService = AndroidAPIUtils.getAndroidService();
        Device device = androidService.modifyEnrollment(id, androidDevice);
        boolean result;
        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = AndroidAPIUtils.getDeviceManagementService().modifyEnrollment(device);
            if (result) {
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
        }
    }

    @DELETE
    @Path("/{id}")
    @Override
    public Response disEnrollDevice(@PathParam("id") String id) {

        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            boolean result = androidService.disEnrollDevice(id);
            if (result) {
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
