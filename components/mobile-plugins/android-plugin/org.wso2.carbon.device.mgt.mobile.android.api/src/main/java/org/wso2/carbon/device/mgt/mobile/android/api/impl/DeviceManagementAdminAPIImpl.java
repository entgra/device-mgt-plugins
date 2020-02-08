/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.carbon.device.mgt.mobile.android.api.impl;

import com.google.api.client.http.HttpStatusCodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.device.mgt.mobile.android.api.DeviceManagementAdminAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.ApplicationInstallationBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.ApplicationRestrictionBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.ApplicationUninstallationBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.ApplicationUpdateBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.BlacklistApplicationsBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.CameraBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.DeviceLockBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.DisplayMessageBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EncryptionBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.FileTransferBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.GlobalProxyBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.LockCodeBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.NotificationBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.PasswordPolicyBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.UpgradeFirmwareBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.VpnBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.WebClipBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.WifiBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.WipeDataBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceManagementAdminAPIImpl implements DeviceManagementAdminAPI {

    private static final Log log = LogFactory.getLog(DeviceManagementAdminAPIImpl.class);

    @POST
    @Path("/file-transfer")
    @Override
    public Response fileTransfer(FileTransferBeanWrapper fileTransferBeanWrapper) {
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.fileTransfer(fileTransferBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance for file transfer operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e) {
            String errorMessage = "Invalid Device Identifiers ( " + fileTransferBeanWrapper.getDeviceIDs() + " ) found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing file tranfer operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureDeviceLock(deviceLockBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing device lock operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureDeviceUnlock(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing device unlock operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.getDeviceLocation(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                    .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while retrieving device location";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.removePassword(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing remove password operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureCamera(cameraBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing control camera operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.getDeviceInformation(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while retrieving device information";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.getDeviceLogcat(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while retrieving device logcat";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.wipeDevice(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing enterprice wipe device operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.wipeData(wipeDataBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing Android wipe-data device operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.getApplications(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing Android getApplicationList device operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.ringDevice(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing Android ring device operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.rebootDevice(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing Android reboot device operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/change-LockTask")
    @Override
    public Response changeLockTask(List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android change LockTask mode operation");
        }

        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.changeLockTask(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing Android change LockTask mode operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.muteDevice(deviceIDs);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing mute device operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/install-application")
    @Override
    public Response installApplication(
            ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'InstallApplication' operation");
        }

        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.installApplication(applicationInstallationBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing install application operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.updateApplication(applicationUpdateBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing update application operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/uninstall-application")
    @Override
    public Response uninstallApplication(
            ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'UninstallApplication' operation");
        }

        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.uninstallApplication(applicationUninstallationBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing uninstall application operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.blacklistApplications(blacklistApplicationsBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing blacklist application operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.upgradeFirmware(upgradeFirmwareBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing upgrade firmware operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureVPN(vpnConfiguration);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing configure vpn operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.sendNotification(notificationBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing send notification operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/configure-apn")
    @Override
    public Response configureAPN(APNBeanWrapper apnBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'apn' operation");
        }
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureAPN(apnBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();

        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occur while executing configure APN operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureWifi(wifiBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing configure wifi operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.encryptStorage(encryptionBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing encrypt operation operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.changeLockCode(lockCodeBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing change lock code operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.setPasswordPolicy(passwordPolicyBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing set password policy operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
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
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.setWebClip(webClipBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing set webclip operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/configure-global-proxy")
    @Override
    public Response setRecommendedGlobalProxy(GlobalProxyBeanWrapper globalProxyBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Applying 'configure-global-proxy' operation: " +
                    globalProxyBeanWrapper.getOperation().toJSON() + " for Devices: ["
                    + String.join(",", globalProxyBeanWrapper.getDeviceIDs()) + "]");
        }

        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.setRecommendedGlobalProxy(globalProxyBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while applying 'configure-global-proxy' operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }
    @POST
    @Path("/send-app-conf")
    @Override
    public Response sendApplicationConfiguration(
            ApplicationRestrictionBeanWrapper applicationRestrictionBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'send application configuration' operation");
        }

        try{
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            ProfileOperation operation = androidService.sendApplicationConfiguration(applicationRestrictionBeanWrapper);
            Response response = AndroidAPIUtils.getOperationResponse(applicationRestrictionBeanWrapper.getDeviceIDs(),
                    operation);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Issue in retrieving device management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while sending app configuration";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/configure-display-message")
    @Override
    public Response configureDisplayMessage(DisplayMessageBeanWrapper displayMessageBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'configure-display-message' operation");
        }

        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            Activity activity = androidService.configureDisplayMessage(displayMessageBeanWrapper);
            return Response.status(Response.Status.CREATED).entity(activity).build();

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        } catch (BadRequestException e){
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing configure-display-message operation";
            log.error(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(errorMessage).build()).build();
        }
    }

}
