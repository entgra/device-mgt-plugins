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
package io.entgra.device.mgt.plugins.mobile.android.api.services.impl;

import io.entgra.device.mgt.plugins.mobile.android.api.exception.BadRequestException;
import io.entgra.device.mgt.plugins.mobile.android.api.exception.UnexpectedServerErrorException;
import io.entgra.device.mgt.plugins.mobile.android.api.services.DeviceManagementAdminService;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidConstants;
import io.entgra.device.mgt.plugins.mobile.android.api.util.AndroidDeviceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.InvalidDeviceException;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Activity;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.OperationManagementException;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.CommandOperation;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.ProfileOperation;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.ApplicationInstallation;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.ApplicationUninstallation;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.ApplicationUpdate;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.BlacklistApplications;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.Camera;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.DeviceEncryption;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.DeviceLock;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.ErrorResponse;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.FileTransfer;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.LockCode;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.Notification;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.PasscodePolicy;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.UpgradeFirmware;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.Vpn;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.WebClip;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.Wifi;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.WipeData;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.ApplicationInstallationBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.ApplicationUninstallationBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.ApplicationUpdateBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.BlacklistApplicationsBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.CameraBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.DeviceLockBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.EncryptionBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.FileTransferBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.LockCodeBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.NotificationBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.PasswordPolicyBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.UpgradeFirmwareBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.VpnBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.WebClipBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.WifiBeanWrapper;
import io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper.WipeDataBeanWrapper;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Path("/admin/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceManagementAdminServiceImpl implements DeviceManagementAdminService {

    private static final Log log = LogFactory.getLog(DeviceManagementAdminServiceImpl.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    @POST
    @Path("/file-transfer")
    @Override
    public Response fileTransfer(FileTransferBeanWrapper fileTransferBeanWrapper) {
        try {
            if (fileTransferBeanWrapper == null || fileTransferBeanWrapper.getOperation() == null
                    || fileTransferBeanWrapper.getDeviceIDs() == null) {
                String errorMessage = "The payload of the file transfer operation is incorrect.";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            if (log.isDebugEnabled()) {
                log.debug("Invoking Android file transfer operation for " + fileTransferBeanWrapper.getDeviceIDs());
            }
            FileTransfer file = fileTransferBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            if (fileTransferBeanWrapper.isUpload()) {
                operation.setCode(AndroidConstants.OperationCodes.FILE_DOWNLOAD);
            } else {
                operation.setCode(AndroidConstants.OperationCodes.FILE_UPLOAD);
            }
            operation.setType(Operation.Type.PROFILE);
            operation.setEnabled(true);
            operation.setPayLoad(file.toJSON());
            Activity activity = AndroidDeviceUtils.getOperationResponse(fileTransferBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers ( " + fileTransferBeanWrapper.getDeviceIDs() + " ) found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance for file transfer operation";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/lock-devices")
    @Override
    public Response configureDeviceLock(DeviceLockBeanWrapper deviceLockBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device lock operation");
        }

        try {
            if (deviceLockBeanWrapper == null || deviceLockBeanWrapper.getOperation() == null) {
                String errorMessage = "Lock bean is empty.";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            DeviceLock lock = deviceLockBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCK);
            operation.setType(Operation.Type.PROFILE);
            operation.setEnabled(true);
            operation.setPayLoad(lock.toJSON());
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceLockBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/unlock-devices")
    @Override
    public Response configureDeviceUnlock(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device unlock operation.");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_UNLOCK);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/location")
    @Override
    public Response getDeviceLocation(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device location operation.");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCATION);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/clear-password")
    @Override
    public Response removePassword(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android clear password operation.");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CLEAR_PASSWORD);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance.";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/control-camera")
    @Override
    public Response configureCamera(CameraBeanWrapper cameraBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android Camera operation");
        }

        try {
            if (cameraBeanWrapper == null || cameraBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the configure camera operation is incorrect.";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            Camera camera = cameraBeanWrapper.getOperation();
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CAMERA);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(camera.isEnabled());
            Activity activity = AndroidDeviceUtils.getOperationResponse(cameraBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/info")
    @Override
    public Response getDeviceInformation(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking get Android device information operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_INFO);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        }
    }

    @POST
    @Path("/logcat")
    @Override
    public Response getDeviceLogcat(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking get Android device logcat operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.LOGCAT);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        }
    }

    @POST
    @Path("/enterprise-wipe")
    @Override
    public Response wipeDevice(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking enterprise-wipe device operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENTERPRISE_WIPE);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/wipe")
    @Override
    public Response wipeData(WipeDataBeanWrapper wipeDataBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android wipe-data device operation");
        }

        try {
            if (wipeDataBeanWrapper == null || wipeDataBeanWrapper.getOperation() == null) {
                String errorMessage = "WipeData bean is empty.";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            WipeData wipeData = wipeDataBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WIPE_DATA);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(wipeData.toJSON());
            Activity activity = AndroidDeviceUtils.getOperationResponse(wipeDataBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/applications")
    @Override
    public Response getApplications(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android getApplicationList device operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.APPLICATION_LIST);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/ring")
    @Override
    public Response ringDevice(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android ring-device device operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_RING);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/reboot")
    @Override
    public Response rebootDevice(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android reboot-device device operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_REBOOT);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/mute")
    @Override
    public Response muteDevice(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking mute device operation");
        }

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_MUTE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/install-application")
    @Override
    public Response installApplication(ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'InstallApplication' operation");
        }

        try {
            if (applicationInstallationBeanWrapper == null || applicationInstallationBeanWrapper.getOperation() ==
                    null) {
                String errorMessage = "The payload of the application installing operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }

            ApplicationInstallation applicationInstallation = applicationInstallationBeanWrapper.getOperation();
            validateApplicationUrl(applicationInstallation.getUrl());
            validateApplicationType(applicationInstallation.getType());
            validateScheduleDate(applicationInstallation.getSchedule());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.INSTALL_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationInstallation.toJSON());
            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(applicationInstallationBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (JSONException e) {
            String errorMessage = "Invalid payload for the operation.";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/update-application")
    @Override
    public Response updateApplication(ApplicationUpdateBeanWrapper applicationUpdateBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'UpdateApplication' operation");
        }

        try {
            if (applicationUpdateBeanWrapper == null || applicationUpdateBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the application update operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            ApplicationUpdate applicationUpdate = applicationUpdateBeanWrapper.getOperation();
            validateApplicationUrl(applicationUpdate.getUrl());
            validateApplicationType(applicationUpdate.getType());
            validateScheduleDate(applicationUpdate.getSchedule());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UPDATE_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationUpdate.toJSON());

            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(applicationUpdateBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/uninstall-application")
    @Override
    public Response uninstallApplication(ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'UninstallApplication' operation");
        }

        try {
            if (applicationUninstallationBeanWrapper == null ||
                    applicationUninstallationBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the application uninstalling operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            ApplicationUninstallation applicationUninstallation = applicationUninstallationBeanWrapper.getOperation();
            validateApplicationType(applicationUninstallation.getType());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UNINSTALL_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationUninstallation.toJSON());
            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(applicationUninstallationBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/blacklist-applications")
    @Override
    public Response blacklistApplications(@Valid BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'Blacklist-Applications' operation");
        }

        try {
            if (blacklistApplicationsBeanWrapper == null || blacklistApplicationsBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the blacklisting apps operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            BlacklistApplications blacklistApplications = blacklistApplicationsBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.BLACKLIST_APPLICATIONS);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(blacklistApplications.toJSON());
            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(blacklistApplicationsBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/upgrade-firmware")
    @Override
    public Response upgradeFirmware(UpgradeFirmwareBeanWrapper upgradeFirmwareBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android upgrade-firmware device operation");
        }

        try {
            if (upgradeFirmwareBeanWrapper == null || upgradeFirmwareBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the upgrade firmware operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            UpgradeFirmware upgradeFirmware = upgradeFirmwareBeanWrapper.getOperation();
            validateScheduleDate(upgradeFirmware.getSchedule());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UPGRADE_FIRMWARE);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(upgradeFirmware.toJSON());
            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(upgradeFirmwareBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/configure-vpn")
    @Override
    public Response configureVPN(VpnBeanWrapper vpnConfiguration) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android VPN device operation");
        }

        try {
            if (vpnConfiguration == null || vpnConfiguration.getOperation() == null) {
                String errorMessage = "The payload of the VPN operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            Vpn vpn = vpnConfiguration.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.VPN);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(vpn.toJSON());
            Activity activity = AndroidDeviceUtils.getOperationResponse(vpnConfiguration.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/send-notification")
    @Override
    public Response sendNotification(NotificationBeanWrapper notificationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'notification' operation");
        }

        try {
            if (notificationBeanWrapper == null || notificationBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the notification operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            Notification notification = notificationBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.NOTIFICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(notification.toJSON());
            Activity activity = AndroidDeviceUtils.getOperationResponse(notificationBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/configure-wifi")
    @Override
    public Response configureWifi(WifiBeanWrapper wifiBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'configure wifi' operation");
        }

        try {
            if (wifiBeanWrapper == null || wifiBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the wifi operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            Wifi wifi = wifiBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WIFI);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(wifi.toJSON());

            Activity activity = AndroidDeviceUtils.getOperationResponse(wifiBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/encrypt-storage")
    @Override
    public Response encryptStorage(EncryptionBeanWrapper encryptionBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'encrypt' operation");
        }

        try {
            if (encryptionBeanWrapper == null || encryptionBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the device encryption operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            DeviceEncryption deviceEncryption = encryptionBeanWrapper.getOperation();
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENCRYPT_STORAGE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(deviceEncryption.isEncrypted());
            Activity activity = AndroidDeviceUtils.getOperationResponse(encryptionBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/change-lock-code")
    @Override
    public Response changeLockCode(LockCodeBeanWrapper lockCodeBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'change lock code' operation");
        }

        try {
            if (lockCodeBeanWrapper == null || lockCodeBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the change lock code operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            LockCode lockCode = lockCodeBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.CHANGE_LOCK_CODE);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(lockCode.toJSON());
            Activity activity = AndroidDeviceUtils.getOperationResponse(lockCodeBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("/set-password-policy")
    @Override
    public Response setPasswordPolicy(PasswordPolicyBeanWrapper passwordPolicyBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'password policy' operation");
        }

        try {
            if (passwordPolicyBeanWrapper == null || passwordPolicyBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the change password policy operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            PasscodePolicy passcodePolicy = passwordPolicyBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.PASSCODE_POLICY);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(passcodePolicy.toJSON());

            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(passwordPolicyBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    @POST
    @Path("set-webclip")
    @Override
    public Response setWebClip(WebClipBeanWrapper webClipBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'webclip' operation");
        }

        try {

            if (webClipBeanWrapper == null || webClipBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the add webclip operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
            WebClip webClip = webClipBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WEBCLIP);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(webClip.toJSON());

            Activity activity = AndroidDeviceUtils.getOperationResponse(webClipBeanWrapper.getDeviceIDs(), operation);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());
        }
    }

    private static void validateApplicationUrl(String apkUrl) {
        try {
            URL url = new URL(apkUrl);
            URLConnection conn = url.openConnection();
            if (((HttpURLConnection) conn).getResponseCode() != HttpURLConnection.HTTP_OK) {
                String errorMessage = "URL is not pointed to a downloadable file.";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
        } catch (MalformedURLException e) {
            String errorMessage = "Malformed application url.";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        } catch (IOException e) {
            String errorMessage = "Invalid application url.";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    private static void validateApplicationType(String type) {
        if (type != null) {
            if (!"enterprise".equalsIgnoreCase(type)
                    && !"public".equalsIgnoreCase(type)
                    && !"webapp".equalsIgnoreCase(type)) {
                String errorMessage = "Invalid application type.";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
            }
        } else {
            String errorMessage = "Application type is missing.";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    private static void validateScheduleDate(String dateString) {
        try {
            if (dateString != null && !dateString.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                sdf.setLenient(false);
                sdf.parse(dateString);
            }
        } catch (ParseException e) {
            String errorMessage = "Issue in validating the schedule date";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

}
