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
package io.entgra.device.mgt.plugins.mobile.android.api.services.impl;

import io.entgra.device.mgt.plugins.mobile.android.api.exception.BadRequestException;
import io.entgra.device.mgt.plugins.mobile.android.api.exception.NotFoundException;
import io.entgra.device.mgt.plugins.mobile.android.api.exception.UnexpectedServerErrorException;
import io.entgra.device.mgt.plugins.mobile.android.api.services.DeviceManagementService;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidAPIUtils;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidConstants;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidDeviceUtils;
import io.entgra.device.mgt.plugins.mobile.android.api.util.Message;
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
import org.wso2.carbon.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.ErrorResponse;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.AndroidApplication;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.AndroidDevice;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceManagementServiceImpl implements DeviceManagementService {

    private static final String OPERATION_ERROR_STATUS = "ERROR";
    private static final Log log = LogFactory.getLog(DeviceManagementServiceImpl.class);

    @PUT
    @Path("/{id}/applications")
    @Override
    public Response updateApplicationList(@PathParam("id")
                                          @NotNull
                                          @Size(min = 2, max = 45)
                                          @Pattern(regexp = "^[A-Za-z0-9]*$")
                                          String id, List<AndroidApplication> androidApplications) {
        Application application;
        List<Application> applications = new ArrayList<>();
        for (AndroidApplication androidApplication : androidApplications) {
            application = new Application();
            application.setPlatform(androidApplication.getPlatform());
            application.setCategory(androidApplication.getCategory());
            application.setName(androidApplication.getName());
            application.setLocationUrl(androidApplication.getLocationUrl());
            application.setImageUrl(androidApplication.getImageUrl());
            application.setVersion(androidApplication.getVersion());
            application.setType(androidApplication.getType());
            application.setAppProperties(androidApplication.getAppProperties());
            application.setApplicationIdentifier(androidApplication.getApplicationIdentifier());
            application.setMemoryUsage(androidApplication.getMemoryUsage());
            applications.add(application);
        }
        Message responseMessage = new Message();
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(id);
        deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        try {
            AndroidAPIUtils.getApplicationManagerService().
                    updateApplicationListInstalledInDevice(deviceIdentifier, applications);
            responseMessage.setResponseMessage("Device information has modified successfully.");
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
    public Response getPendingOperations(@PathParam("id") String id,
                                         @HeaderParam("If-Modified-Since") String ifModifiedSince,
                                         List<? extends Operation> resultOperations) {
        if (id == null || id.isEmpty()) {
            String msg = "Device identifier is null or empty, hence returning device not found";
            log.error(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        Device device;
        try {
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier, false);
            if (!AndroidDeviceUtils.isValidDeviceIdentifier(device)) {
                String msg = "Device not found for identifier '" + id + "'";
                log.error(msg);
                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
            }
            if (log.isDebugEnabled()) {
                log.debug("Invoking Android pending operations:" + id);
            }
            if (resultOperations != null && !resultOperations.isEmpty()) {
                updateOperations(device, resultOperations);
            }
        } catch (OperationManagementException e) {
            String msg = "Issue in retrieving operation management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (PolicyComplianceException e) {
            String msg = "Issue in updating Monitoring operation";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (DeviceManagementException e) {
            String msg = "Issue in retrieving device management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (ApplicationManagementException e) {
            String msg = "Issue in retrieving application management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (NotificationManagementException e) {
            String msg = "Issue in retrieving Notification management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }

        List<? extends Operation> pendingOperations;
        try {
            pendingOperations = AndroidDeviceUtils.getPendingOperations(device);
        } catch (OperationManagementException e) {
            String msg = "Issue in retrieving operation management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
        return Response.status(Response.Status.CREATED).entity(pendingOperations).build();
    }

    @Override
    public Response getOperations(String id, String status, String ifModifiedSince) {
        if (id == null || id.isEmpty()) {
            String msg = "Device identifier is null or empty, hence returning device not found";
            log.error(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        Device device;
        try {
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier, false);
            if (!AndroidDeviceUtils.isValidDeviceIdentifier(device)) {
                String msg = "Device not found for identifier '" + id + "'";
                log.error(msg);
                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
            }
            if (log.isDebugEnabled()) {
                log.debug("Invoking Android pending operations:" + id);
            }

            List<? extends Operation> operations;
            if (status != null && !status.isEmpty()) {
                operations = AndroidAPIUtils.getDeviceManagementService()
                        .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.valueOf(status));
            } else {
                operations = AndroidAPIUtils.getDeviceManagementService()
                        .getOperations(deviceIdentifier);
            }
            return Response.status(Response.Status.OK).entity(operations).build();
        } catch (OperationManagementException e) {
            String msg = "Issue in retrieving operation management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (DeviceManagementException e) {
            String msg = "Issue in retrieving device management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    private void updateOperations(Device device, List<? extends Operation> operations)
            throws OperationManagementException, PolicyComplianceException,
            ApplicationManagementException, NotificationManagementException {
        String deviceName = device.getName();
        for (Operation operation : operations) {
            AndroidDeviceUtils.updateOperation(device, operation);
            if (OPERATION_ERROR_STATUS.equals(operation.getStatus().toString())) {
                org.wso2.carbon.device.mgt.common.notification.mgt.Notification notification = new
                        org.wso2.carbon.device.mgt.common.notification.mgt.Notification();
                notification.setOperationId(operation.getId());
                notification.setStatus(org.wso2.carbon.device.mgt.common.notification.mgt.Notification.
                        Status.NEW.toString());
                notification.setDescription(operation.getCode() + " operation failed to execute on device " +
                        deviceName + " (ID: " + device.getDeviceIdentifier() + ")");
                AndroidAPIUtils.getNotificationManagementService().addNotification(device, notification);
            }
            if (log.isDebugEnabled()) {
                log.debug("Updating operation '" + operation.toString() + "'");
            }
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
            Device device = new Device();
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            device.setEnrolmentInfo(androidDevice.getEnrolmentInfo());
            device.getEnrolmentInfo().setOwner(AndroidAPIUtils.getAuthenticatedUser());
            device.setDeviceInfo(androidDevice.getDeviceInfo());
            device.setDeviceIdentifier(androidDevice.getDeviceIdentifier());
            device.setDescription(androidDevice.getDescription());
            device.setName(androidDevice.getName());
            device.setFeatures(androidDevice.getFeatures());
            device.setProperties(androidDevice.getProperties());

            boolean status = AndroidAPIUtils.getDeviceManagementService().enrollDevice(device);
            if (status) {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier(androidDevice.getDeviceIdentifier(),
                                                                         device.getType());

                //Immediately update location information from initial payload
                DeviceLocation deviceLocation = extractLocation(deviceIdentifier, androidDevice.getProperties());
                if (deviceLocation != null) {
                    try {
                        DeviceInformationManager informationManager = AndroidAPIUtils
                                .getDeviceInformationManagerService();
                        informationManager.addDeviceLocation(deviceLocation);
                    } catch (DeviceDetailsMgtException e) {
                        String msg = "Error occurred while updating the device location upon android " +
                                "', which carries the id '" + androidDevice.getDeviceIdentifier() + "'";
                        log.error(msg, e);
                        throw new UnexpectedServerErrorException(
                                new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
                    }
                }

                //Adding Tasks to get device information
                List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
                deviceIdentifiers.add(deviceIdentifier);

                 List<String> taskOperaions = new ArrayList<>();
                 taskOperaions.add(AndroidConstants.OperationCodes.APPLICATION_LIST);
                 taskOperaions.add(AndroidConstants.OperationCodes.DEVICE_INFO);
                 taskOperaions.add(AndroidConstants.OperationCodes.DEVICE_LOCATION);

                for (String str : taskOperaions) {
                    CommandOperation operation = new CommandOperation();
                    operation.setEnabled(true);
                    operation.setType(Operation.Type.COMMAND);
                    operation.setCode(str);
                    AndroidAPIUtils.getDeviceManagementService().
                            addOperation(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID,
                                    operation, deviceIdentifiers);
                }
                PolicyManagerService policyManagerService = AndroidAPIUtils.getPolicyManagerService();
                policyManagerService.getEffectivePolicy(new DeviceIdentifier(androidDevice.getDeviceIdentifier(), device.getType()));

                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.OK.toString());
                responseMessage.setResponseMessage("Android device, which carries the id '" +
                        androidDevice.getDeviceIdentifier() + "' has successfully been enrolled");
                return Response.status(Response.Status.OK).entity(responseMessage).build();
            } else {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
                responseMessage.setResponseMessage("Failed to enroll '" +
                        device.getType() + "' device, which carries the id '" +
                        androidDevice.getDeviceIdentifier() + "'");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage).build();
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while enrolling the android, which carries the id '" +
                    androidDevice.getDeviceIdentifier() + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (PolicyManagementException e) {
            String msg = "Error occurred while enforcing default enrollment policy upon android " +
                    "', which carries the id '" +
                    androidDevice.getDeviceIdentifier() + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (OperationManagementException e) {
            String msg = "Error occurred while enforcing default enrollment policy upon android " +
                    "', which carries the id '" +
                    androidDevice.getDeviceIdentifier() + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        } catch (InvalidDeviceException e) {
            String msg = "Error occurred while enforcing default enrollment policy upon android " +
                    "', which carries the id '" +
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
            Device device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier, false);
            if (device != null) {
                String status = String.valueOf(device.getEnrolmentInfo().getStatus());
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.OK.toString());
                responseMessage
                        .setResponseMessage("Status of android device that carries the id '" + id + "' is " + status);
                return Response.status(Response.Status.OK).entity(responseMessage).build();
            } else {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.NOT_FOUND.toString());
                responseMessage.setResponseMessage("No Android device is found upon the id '" + id + "'");
                return Response.status(Response.Status.NOT_FOUND).entity(responseMessage).build();
            }
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
        Device device;
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(id);
        deviceIdentifier.setType(AndroidConstants.DEVICE_TYPE_ANDROID);
        try {
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier, false);
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while getting enrollment details of the Android device that carries the id '" +
                    id + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }

        if (androidDevice == null) {
            String errorMessage = "The payload of the android device enrollment is incorrect.";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
        if (device == null) {
            String errorMessage = "The device to be modified doesn't exist.";
            log.error(errorMessage);
            throw new NotFoundException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage(errorMessage).build());
        }
        if(androidDevice.getEnrolmentInfo() != null){
            device.setEnrolmentInfo(device.getEnrolmentInfo());
        }
        device.getEnrolmentInfo().setOwner(AndroidAPIUtils.getAuthenticatedUser());
        if(androidDevice.getDeviceInfo() != null) {
            device.setDeviceInfo(androidDevice.getDeviceInfo());
        }
        device.setDeviceIdentifier(androidDevice.getDeviceIdentifier());
        if(androidDevice.getDescription() != null) {
            device.setDescription(androidDevice.getDescription());
        }
        if(androidDevice.getName() != null) {
            device.setName(androidDevice.getName());
        }
        if(androidDevice.getFeatures() != null) {
            device.setFeatures(androidDevice.getFeatures());
        }
        if(androidDevice.getProperties() != null) {
            device.setProperties(androidDevice.getProperties());
        }
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
        boolean result;
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        try {
            result = AndroidAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
            if (result) {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.OK.toString());
                responseMessage.setResponseMessage("Android device that carries id '" + id +
                        "' has successfully dis-enrolled");
                return Response.status(Response.Status.OK).entity(responseMessage).build();
            } else {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.NOT_FOUND.toString());
                responseMessage.setResponseMessage("Android device that carries id '" + id +
                        "' has not been dis-enrolled");
                return Response.status(Response.Status.NOT_FOUND).entity(responseMessage).build();
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while dis-enrolling the Android device that carries the id '" + id + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    /**
     * Extracts the device location
     *
     * @param deviceIdentifier
     * @param properties
     * @return returns null when location not found
     */
    private DeviceLocation extractLocation(DeviceIdentifier deviceIdentifier, List<Device.Property> properties)
            throws DeviceManagementException {

        DeviceLocation location = null;
        String latitude = "", longitude = "";

        if (properties == null) return null;

        for (Device.Property property : properties) {
            String propertyName = property.getName();
            if (propertyName == null) continue;
            if (propertyName.equals("LATITUDE")) {
                latitude = property.getValue();
                if (!longitude.isEmpty()) break;
            } else if (propertyName.equals("LONGITUDE")) {
                longitude = property.getValue();
                if (!latitude.isEmpty()) break;
            }
        }

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            location = new DeviceLocation();
            location.setLatitude(Double.valueOf(latitude));
            location.setLongitude(Double.valueOf(longitude));
            location.setDeviceIdentifier(deviceIdentifier);
            Device savedDevice = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier, false);
            location.setDeviceId(savedDevice.getId());
        }
        return location;
    }
}
