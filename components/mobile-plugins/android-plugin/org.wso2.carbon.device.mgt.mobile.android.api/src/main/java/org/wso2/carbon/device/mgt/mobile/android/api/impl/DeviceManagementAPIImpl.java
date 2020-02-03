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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.application.mgt.common.SubAction;
import org.wso2.carbon.device.application.mgt.common.SubscriptionType;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.device.mgt.mobile.android.api.DeviceManagementAPI;
import org.wso2.carbon.device.mgt.mobile.android.api.invoker.GoogleAPIInvoker;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidPluginConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseConfigs;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidApplication;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidDevice;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseApp;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.*;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidEnterpriseUtils;

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
import java.util.Arrays;
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build()).build();
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
            DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
            List<? extends Operation> pendingOperations = androidService
                    .getPendingOperations(deviceIdentifier, resultOperations);
            if (pendingOperations != null && !disableGoogleApps) {
                handleEnrollmentGoogleApps(pendingOperations, deviceIdentifier);
            }
            return Response.status(Response.Status.CREATED).entity(pendingOperations).build();
        } catch (InvalidDeviceException e) {
            String msg = "Device identifier is invalid. Device identifier " + id;
            log.error(msg, e);
            return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing get pending operations";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while getting pending operations of the device.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build()).build();
        }
    }

    @POST
    @Override
    public Response enrollDevice(@Valid AndroidDevice androidDevice) {
        if (androidDevice == null) {
            String errorMessage = "The payload of the android device enrollment is incorrect.";
            log.error(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        }
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            String token = null;
            String googleEMMAndroidId = null;
            String googleEMMDeviceId = null;
            if (androidDevice.getProperties() != null) {
                for (Device.Property property : androidDevice.getProperties()) {
                    if (property.getName().equals(AndroidPluginConstants.GOOGLE_AFW_EMM_ANDROID_ID)) {
                        googleEMMAndroidId = property.getValue();
                    } else if (property.getName().equals(AndroidPluginConstants.GOOGLE_AFW_DEVICE_ID)) {
                        googleEMMDeviceId = property.getValue();
                    }
                }
                if (googleEMMAndroidId != null && googleEMMDeviceId != null) {
                    EnterpriseUser user = new EnterpriseUser();
                    user.setAndroidPlayDeviceId(googleEMMAndroidId);
                    user.setEmmDeviceIdentifier(googleEMMDeviceId);
                    try {
                        AndroidEnterpriseAPIImpl enterpriseService = new AndroidEnterpriseAPIImpl();
                        token = enterpriseService.insertUser(user);
                    } catch (EnterpriseServiceException e) {
                        //todo
                    }
                }
            }
            Message message = androidService.enrollDevice(androidDevice);
            if (token != null) {
                message.setResponseMessage("Google response token" + token);
            }
            return Response.status(Integer.parseInt(message.getResponseCode()))
                    .entity(message.getResponseMessage()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while enrolling device";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while enrolling the android, which carries the id '" + androidDevice
                    .getDeviceIdentifier() + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build()).build();
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build()).build();
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
        } catch (BadRequestException e) {
            String errorMessage = "The payload of the android device enrollment is incorrect.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "The device to be modified doesn't exist.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_NOT_FOUND)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while modifying enrollment";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (DeviceManagementException e) {
            String msg =
                    "Error occurred while modifying enrollment of the Android device that carries the id '" + id + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build()).build();
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build()).build();
        }
    }

    private void handleEnrollmentGoogleApps(List<? extends Operation> operations, DeviceIdentifier
            deviceIdentifier) {
        boolean containsGoogleAppPolicy = false;
        for (int x = 0; x < operations.size() && !containsGoogleAppPolicy; x++) {
            Operation operation = operations.get(x);

            // Check if the operation has a policy bundle inside.
            if (operation.getCode().equals(AndroidConstants.OperationCodes.POLICY_BUNDLE)) {
                ArrayList operationPayLoad = (ArrayList) operation.getPayLoad();


                // If there is a policy bundle, read its payload
                for (int i = 0; i < operationPayLoad.size() && !containsGoogleAppPolicy; i++) {
                    Object policy = operationPayLoad.get(i);
                    ProfileOperation profileOperation = (ProfileOperation) policy;
                    String code = profileOperation.getCode();

                    // Find if there is an ENROLLMENT_APP_INSTALL payload
                    if (code.equals(AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_FEATURE_CODE)) {
                        String payload = profileOperation.getPayLoad().toString();
                        JsonElement appListElement = new JsonParser().parse(payload).getAsJsonObject()
                                .get(AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_CODE);
                        JsonArray appListArray = appListElement.getAsJsonArray();

                        // Find if there are Apps with Work profile configurations
                        boolean alreadySendToGoogle = false;
                        for (JsonElement appElement : appListArray) {
                            JsonElement googlePolicyPayload = appElement.getAsJsonObject().
                                    get(AndroidConstants.ApplicationInstall.GOOGLE_POLICY_PAYLOAD);
                            if (googlePolicyPayload != null) {
                                String uuid = appElement.getAsJsonObject().get("uuid").toString();
                                containsGoogleAppPolicy = true;// breaking out of outer for loop
                                try {
                                    uuid = uuid.replace("\"", "");
                                    if (alreadySendToGoogle) {
                                        sendPayloadToGoogle(uuid, payload, deviceIdentifier, false);
                                    } else {
                                        sendPayloadToGoogle(uuid, payload, deviceIdentifier, true);
                                        alreadySendToGoogle = true;
                                    }
                                } catch (org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException e) {
                                    String errorMessage = "App install failed for device " + deviceIdentifier.getId();
                                    log.error(errorMessage, e);
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Sends the app install policy to Google
     * @param payload policy profile
     * @param deviceIdentifier device to apply policy
     */
    private void sendPayloadToGoogle(String uuid, String payload, DeviceIdentifier deviceIdentifier,
            boolean requireSendingToGoogle) throws
            org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException {
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigsFromGoogle();
            if (enterpriseConfigs.getErrorResponse() == null) {
                GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
                AndroidEnterpriseUser userDetail = AndroidAPIUtils.getAndroidPluginService()
                        .getEnterpriseUserByDevice(deviceIdentifier.getId());
                if (userDetail != null && userDetail.getEnterpriseId() != null && !userDetail.getEnterpriseId()
                        .isEmpty() && userDetail.getEmmUsername() != null && payload != null) {
                    EnterpriseInstallPolicy enterpriseInstallPolicy = AndroidEnterpriseUtils
                            .getDeviceAppPolicy(payload, null, userDetail);

                    List<String> apps = new ArrayList<>();
                    for (EnterpriseApp enterpriseApp : enterpriseInstallPolicy.getApps()) {
                        apps.add(enterpriseApp.getProductId());
                    }
                    if (requireSendingToGoogle) {
                        googleAPIInvoker.approveAppsForUser(enterpriseConfigs.getEnterpriseId(), userDetail
                                .getGoogleUserId(), apps, enterpriseInstallPolicy.getProductSetBehavior());
                        googleAPIInvoker.updateAppsForUser(enterpriseConfigs.getEnterpriseId(), userDetail.getGoogleUserId(),
                                AndroidEnterpriseUtils.convertToDeviceInstance(enterpriseInstallPolicy));
                    }
                    AndroidEnterpriseUtils.getAppSubscriptionService().performEntAppSubscription(uuid,
                            Arrays.asList(CarbonContext.getThreadLocalCarbonContext().getUsername()),
                            SubscriptionType.USER.toString(), SubAction.INSTALL.toString(), false);
                }
            }
        } catch (EnterpriseServiceException e) {
            String errorMessage = "App install failed for device " + deviceIdentifier.getId();
            log.error(errorMessage);
        }
    }
}
