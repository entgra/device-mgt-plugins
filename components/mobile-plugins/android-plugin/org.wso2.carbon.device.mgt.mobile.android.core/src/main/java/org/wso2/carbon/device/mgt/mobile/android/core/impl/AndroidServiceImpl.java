/* Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.android.core.impl;

import com.google.api.services.androidenterprise.model.ProductsListResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.policy.mgt.Policy;
import org.wso2.carbon.device.mgt.common.policy.mgt.ProfileFeature;
import org.wso2.carbon.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.GoogleAPIInvoker;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.*;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.*;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.*;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidEnterpriseUtils;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.hazelcast.aws.impl.Constants.DATE_FORMAT;

public class AndroidServiceImpl implements AndroidService {

    private static final Log log = LogFactory.getLog(AndroidServiceImpl.class);

    private static final String OPERATION_ERROR_STATUS = "ERROR";
    public static final String GOOGLE_AFW_EMM_ANDROID_ID = "googleEMMAndroidId";
    public static final String GOOGLE_AFW_DEVICE_ID = "googleEMMDeviceId";

    private static final String EVENT_STREAM_DEFINITION = "org.wso2.iot.LocationStream";

    @Override
    public PlatformConfiguration getPlatformConfig() throws DeviceManagementException {
        List<ConfigurationEntry> configs;
        PlatformConfiguration platformConfiguration;

        DeviceManagementProviderService deviceManagementProviderService = AndroidAPIUtils.getDeviceManagementService();
        platformConfiguration = deviceManagementProviderService.
                getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        if (platformConfiguration != null) {
            configs = platformConfiguration.getConfiguration();
        } else {
            platformConfiguration = new PlatformConfiguration();
            configs = new ArrayList<>();
        }

        if (configs != null) {
            ConfigurationEntry versionEntry = new ConfigurationEntry();
            versionEntry.setContentType(AndroidConstants.TenantConfigProperties.CONTENT_TYPE_TEXT);
            versionEntry.setName(AndroidConstants.TenantConfigProperties.SERVER_VERSION);
            versionEntry.setValue(ServerConfiguration.getInstance().getFirstProperty("Version"));
            configs.add(versionEntry);

            License license = deviceManagementProviderService
                    .getLicense(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID,
                            AndroidConstants.
                                    TenantConfigProperties.LANGUAGE_US);
            if (license != null) {
                ConfigurationEntry entry = new ConfigurationEntry();
                entry.setContentType(AndroidConstants.TenantConfigProperties.CONTENT_TYPE_TEXT);
                entry.setName(AndroidConstants.TenantConfigProperties.LICENSE_KEY);
                entry.setValue(license.getText());
                configs.add(entry);
            }
            platformConfiguration.setConfiguration(configs);
        }
        return platformConfiguration;
    }

    @Override
    public void updateConfiguration(AndroidPlatformConfiguration androidPlatformConfiguration) throws
            AndroidDeviceMgtPluginException {
        ConfigurationEntry licenseEntry = null;
        PlatformConfiguration configuration = new PlatformConfiguration();
        if (androidPlatformConfiguration == null) {
            String errorMessage = "The payload of the android platform configuration is null.";
            log.error(errorMessage);
            throw new BadRequestExceptionDup(errorMessage);
        }
        configuration.setConfiguration(androidPlatformConfiguration.getConfiguration());
        try {
            DeviceManagementProviderService deviceManagementProviderService = AndroidAPIUtils
                    .getDeviceManagementService();
            configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            List<ConfigurationEntry> configs = configuration.getConfiguration();
            NotifierFrequency notifierFrequency = new NotifierFrequency();
            for (ConfigurationEntry entry : configs) {
                if (AndroidConstants.TenantConfigProperties.LICENSE_KEY.equals(entry.getName())) {
                    License license = new License();
                    license.setName(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
                    license.setLanguage(AndroidConstants.TenantConfigProperties.LANGUAGE_US);
                    license.setVersion("1.0.0");
                    license.setText(entry.getValue().toString());
                    deviceManagementProviderService.addLicense(DeviceManagementConstants.
                            MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID, license);
                    licenseEntry = entry;
                } else if (AndroidConstants.TenantConfigProperties.NOTIFIER_FREQUENCY.equals(entry.getName())) {
                    if (entry.getValue() != null) {
                        notifierFrequency.setValue(Integer.parseInt(entry.getValue().toString()));
                    } else {
                        String msg = "No value specified for notifierFrequency.";
                        log.error(msg);
                        throw new BadRequestExceptionDup(msg);
                    }
                } else if (AndroidConstants.TenantConfigProperties.NOTIFIER_TYPE.equals(entry.getName())) {
                    if (entry.getValue() != null) {
                        notifierFrequency.setType(Integer.parseInt(entry.getValue().toString()));
                    } else {
                        String msg = "No value specified for notifierType.";
                        log.error(msg);
                        throw new BadRequestExceptionDup(msg);
                    }
                }
            }

            if (licenseEntry != null) {
                configs.remove(licenseEntry);
            }
            configuration.setConfiguration(configs);
            deviceManagementProviderService.saveConfiguration(configuration);
            notifyDevices(notifierFrequency);
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while getting device management service to modify Android config settings";
            log.error(msg, e);
            throw new AndroidDeviceMgtPluginException(msg, e);
        } catch (NumberFormatException e) {
            String msg = "Error occurred while reading notification frequency.";
            log.error(msg, e);
            throw new AndroidDeviceMgtPluginException(msg, e);
        } catch (OperationManagementException e) {
            String msg = "Error occurred while modifying configuration settings of Android platform.";
            log.error(msg, e);
            throw new AndroidDeviceMgtPluginException(msg, e);
        } catch (InvalidDeviceException e) {
            String msg = "Error occurred with the device.";
            log.error(msg, e);
            throw new AndroidDeviceMgtPluginException(msg, e);
        }
    }

    private void notifyDevices(NotifierFrequency notifierFrequency) throws DeviceManagementException,
            OperationManagementException, InvalidDeviceException {
        List<Device> deviceList = AndroidAPIUtils.
                getDeviceManagementService().
                getAllDevices(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID, false);
        List<DeviceIdentifier> deviceIdList = new ArrayList<>();
        for (Device device : deviceList) {
            if (EnrolmentInfo.Status.REMOVED != device.getEnrolmentInfo().getStatus()) {
                deviceIdList.add(new DeviceIdentifier(device.getDeviceIdentifier(), device.getType()));
            }
        }
        if (!deviceIdList.isEmpty()) {
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.NOTIFIER_FREQUENCY);
            operation.setPayLoad(notifierFrequency.toJSON());
            operation.setEnabled(true);
            AndroidAPIUtils.getDeviceManagementService().addOperation(
                    DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID,
                    operation, deviceIdList);
        }
    }

    @Override
    public Message isEnrolled(String id, DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        Device device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
        if (device != null) {
            String status = String.valueOf(device.getEnrolmentInfo().getStatus());
            Message responseMessage = new Message();
            responseMessage.setResponseCode(Response.Status.OK.toString());
            responseMessage
                    .setResponseMessage("Status of android device that carries the id '" + id + "' is " + status);
            return responseMessage;
        } else {
            Message responseMessage = new Message();
            responseMessage.setResponseCode(Response.Status.NOT_FOUND.toString());
            responseMessage.setResponseMessage("No Android device is found upon the id '" + id + "'");
            return responseMessage;
        }
    }

    @Override
    public Activity fileTransfer(FileTransferBeanWrapper fileTransferBeanWrapper) throws OperationManagementException {
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

        try {
            Activity activity = AndroidDeviceUtils
                    .getOperationResponse(fileTransferBeanWrapper.getDeviceIDs(), operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers ( " + fileTransferBeanWrapper.getDeviceIDs() + " ) found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity configureDeviceLock(DeviceLockBeanWrapper deviceLockBeanWrapper) throws OperationManagementException {
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

        try {
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceLockBeanWrapper.getDeviceIDs(), operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity configureDeviceUnlock(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_UNLOCK);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity getDeviceLocation(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCATION);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity removePassword(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CLEAR_PASSWORD);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity configureCamera(CameraBeanWrapper cameraBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity getDeviceInformation(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_INFO);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity getDeviceLogcat(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.LOGCAT);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity wipeDevice(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENTERPRISE_WIPE);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity wipeData(WipeDataBeanWrapper wipeDataBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity getApplications(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.APPLICATION_LIST);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity ringDevice(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_RING);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity rebootDevice(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_REBOOT);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity changeLockTask(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CHANGE_LOCK_TASK_MODE);
            operation.setType(Operation.Type.COMMAND);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity muteDevice(List<String> deviceIDs) throws OperationManagementException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_MUTE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            Activity activity = AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity installApplication(ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper)
            throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity updateApplication(ApplicationUpdateBeanWrapper applicationUpdateBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity uninstallApplication(ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity blacklistApplications(BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity upgradeFirmware(UpgradeFirmwareBeanWrapper upgradeFirmwareBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity configureVPN(VpnBeanWrapper vpnConfiguration) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity sendNotification(NotificationBeanWrapper notificationBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity configureWifi(WifiBeanWrapper wifiBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity encryptStorage(EncryptionBeanWrapper encryptionBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity changeLockCode(LockCodeBeanWrapper lockCodeBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity setPasswordPolicy(PasswordPolicyBeanWrapper passwordPolicyBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity setWebClip(WebClipBeanWrapper webClipBeanWrapper) throws OperationManagementException {
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
            return activity;
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
    }

    @Override
    public Activity setRecommendedGlobalProxy(GlobalProxyBeanWrapper globalProxyBeanWrapper) throws OperationManagementException {
        try {
            if (globalProxyBeanWrapper == null || globalProxyBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the global proxy operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400L).setMessage(errorMessage).build());
            }

            GlobalProxy globalProxy = globalProxyBeanWrapper.getOperation();
            if (globalProxy.validateRequest()) {
                ProfileOperation operation = new ProfileOperation();
                operation.setCode(AndroidConstants.OperationCodes.GLOBAL_PROXY);
                operation.setType(Operation.Type.PROFILE);
                operation.setPayLoad(globalProxy.toJSON());

                Activity activity = AndroidDeviceUtils
                        .getOperationResponse(globalProxyBeanWrapper.getDeviceIDs(), operation);
                return activity;
            } else {
                String errorMessage = "The payload of the global proxy operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(400L).setMessage(errorMessage).build());
            }
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400L).setMessage(errorMessage).build());
        }
    }


    @Override
    public Activity configureDisplayMessage(DisplayMessageBeanWrapper displayMessageBeanWrapper) throws OperationManagementException {
        try {
            if (displayMessageBeanWrapper == null || displayMessageBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the display message operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatus.SC_BAD_REQUEST).
                                setMessage(errorMessage).build());
            }
            DisplayMessage configureDisplayMessage = displayMessageBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.DISPLAY_MESSAGE_CONFIGURATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(configureDisplayMessage.toJSON());

            Activity activity = AndroidDeviceUtils.getOperationResponse(displayMessageBeanWrapper.
                    getDeviceIDs(), operation);
            return activity;

        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatus.SC_BAD_REQUEST).
                            setMessage(errorMessage).build());
        }
    }



    @Override
    public Message updateApplicationList(String id, List<AndroidApplication> androidApplications) throws org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException {
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
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(id);
        deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);

        AndroidAPIUtils.getApplicationManagerService().
                updateApplicationListInstalledInDevice(deviceIdentifier, applications);
        Message responseMessage = new Message();
        responseMessage.setResponseMessage("Device information has modified successfully.");
        return responseMessage;
    }

    @Override
    public void getPendingOperations(String id, List<? extends Operation> resultOperations) throws DeviceManagementException {
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        if (!AndroidDeviceUtils.isValidDeviceIdentifier(deviceIdentifier)) {
            String msg = "Device not found for identifier '" + id + "'";
            log.error(msg);
            throw new NotFoundException(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build());
        }
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android pending operations:" + id);
        }
        if (resultOperations != null && !resultOperations.isEmpty()) {
            try {
                updateOperations(id, resultOperations);
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
            } catch (org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException e) {
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
        }
    }

    @Override
    public Response enrollDevice(AndroidDevice androidDevice) throws DeviceManagementException {
        try {
            String token = null;
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

            String googleEMMAndroidId = null;
            String googleEMMDeviceId = null;
            if (androidDevice.getProperties() != null) {
                for (Device.Property property : androidDevice.getProperties()) {
                    if (property.getName().equals(GOOGLE_AFW_EMM_ANDROID_ID)) {
                        googleEMMAndroidId = property.getValue();
                    } else if (property.getName().equals(GOOGLE_AFW_DEVICE_ID)) {
                        googleEMMDeviceId = property.getValue();
                    }
                }

                if (googleEMMAndroidId != null && googleEMMDeviceId != null) {
                    EnterpriseUser user = new EnterpriseUser();
                    user.setAndroidPlayDeviceId(googleEMMAndroidId);
                    user.setEmmDeviceIdentifier(googleEMMDeviceId);
                    try {
                        token = insertUser(user);
                    } catch (EnterpriseServiceException e) {

                    }
                }
            }


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
                Policy effectivePolicy = policyManagerService.
                        getEffectivePolicy(new DeviceIdentifier(androidDevice.getDeviceIdentifier(), device.getType()));

                if (effectivePolicy != null) {
                    List<ProfileFeature> effectiveProfileFeatures = effectivePolicy.getProfile().
                            getProfileFeaturesList();
                    for (ProfileFeature feature : effectiveProfileFeatures) {
                        if (AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_FEATURE_CODE
                                .equals(feature.getFeatureCode())) {
                            AndroidDeviceUtils.installEnrollmentApplications(feature, deviceIdentifier);
                            break;
                        }
                    }
                }

                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.OK.toString());
                if (token == null) {
                    responseMessage.setResponseMessage("Android device, which carries the id '" +
                            androidDevice.getDeviceIdentifier() + "' has successfully been enrolled");
                } else {
                    responseMessage.setResponseMessage("Google response token" + token);
                }
                return Response.status(Response.Status.OK).entity(responseMessage).build();
            } else {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
                responseMessage.setResponseMessage("Failed to enroll '" +
                        device.getType() + "' device, which carries the id '" +
                        androidDevice.getDeviceIdentifier() + "'");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage).build();
            }
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

    @Override
    public Device modifyEnrollment(String id, AndroidDevice androidDevice){
        Device device;
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(id);
        deviceIdentifier.setType(AndroidConstants.DEVICE_TYPE_ANDROID);
        try {
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
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
        return device;
    }

    @Override
    public boolean disEnrollDevice(String id) throws DeviceManagementException{
        boolean result;
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        AndroidDeviceUtils.updateDisEnrollOperationStatus(deviceIdentifier);
        result = AndroidAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
        return result;
    }

    @Override
    public Device publishEvents(EventBeanWrapper eventBeanWrapper) throws DeviceManagementException{
        Device device;
            if (!DeviceManagerUtil.isPublishLocationResponseEnabled()) {
                String msg = "Event is publishing has not enabled.";
                log.error(msg);
                throw new DeviceManagementException(msg);            }
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(eventBeanWrapper.getDeviceIdentifier(),
                    AndroidConstants.DEVICE_TYPE_ANDROID);
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
            if (device != null && EnrolmentInfo.Status.ACTIVE != device.getEnrolmentInfo().getStatus()){
                String msg = "Device is not in Active state.";
                log.error(msg);
                throw new DeviceManagementException(msg);
            } else if (device == null){
                String msg = "Device is not enrolled yet.";
                log.error(msg);
                throw new DeviceManagementException(msg);
            }

        return device;
    }

    @Override
    public Response retrieveAlerts(String deviceId,
                               long from,
                               long to,
                               String type,
                               String ifModifiedSince) {
        if (from != 0l && to != 0l && deviceId != null) {
            return retrieveAlertFromDate(deviceId, from, to);
        } else if (deviceId != null && type != null) {
            return retrieveAlertByType(deviceId, type);
        } else if (deviceId != null) {
            return retrieveAlert(deviceId);
        } else {
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage("Request must contain " +
                            "the device identifier. Optionally, both from and to value should be present to get " +
                            "alerts between times.").build());
        }
    }

    private Response retrieveAlert(String deviceId) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device Identifier.");
        }
        String query = "deviceIdentifier:" + deviceId;
        List<DeviceState> deviceStates;
        try {
            deviceStates = AndroidDeviceUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
            if (deviceStates == null) {
                throw new NotFoundException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage("No any alerts are " +
                                "published for Device: " + deviceId + ".").build());
            } else {
                return Response.status(Response.Status.OK).entity(deviceStates).build();
            }
        } catch (AnalyticsException e) {
            String msg = "Error occurred while getting published events for specific device: " + deviceId + ".";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    private Response retrieveAlertFromDate(String deviceId, long from, long to) {
        String fromDate = String.valueOf(from);
        String toDate = String.valueOf(to);
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device Identifier and time period.");
        }

        String query = "deviceIdentifier:" + deviceId + " AND _timestamp: [" + fromDate + " TO " + toDate + "]";
        List<DeviceState> deviceStates;
        try {
            deviceStates = AndroidDeviceUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
            if (deviceStates == null) {
                throw new NotFoundException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage("No any alerts are " +
                                "published on given date for given Device: " + deviceId + ".").build());

            } else {
                return Response.status(Response.Status.OK).entity(deviceStates).build();
            }
        } catch (AnalyticsException e) {
            String msg = "Error occurred while getting published events for specific " +
                    "Device: " + deviceId + " on given Date.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    private Response retrieveAlertByType(String deviceId, String type) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device identifier and type.");
        }
        String query = "deviceIdentifier:" + deviceId + " AND type:" + type;
        List<DeviceState> deviceStates;
        try {
            deviceStates = AndroidDeviceUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
            if (deviceStates == null) {
                throw new NotFoundException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage("No any alerts are " +
                                "published for given Device: '" + deviceId + "' and given specific Type.").build());

            } else {
                return Response.status(Response.Status.OK).entity(deviceStates).build();
            }
        } catch (AnalyticsException e) {
            String msg = "Error occurred while getting published events for specific " +
                    "Device: " + deviceId + "and given specific Type.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    private void updateOperations(String deviceId, List<? extends Operation> operations)
            throws OperationManagementException, PolicyComplianceException,
            org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException, NotificationManagementException, DeviceManagementException {
        for (org.wso2.carbon.device.mgt.common.operation.mgt.Operation operation : operations) {
            AndroidDeviceUtils.updateOperation(deviceId, operation);
            if (OPERATION_ERROR_STATUS.equals(operation.getStatus().toString())) {
                org.wso2.carbon.device.mgt.common.notification.mgt.Notification notification = new
                        org.wso2.carbon.device.mgt.common.notification.mgt.Notification();
                DeviceIdentifier id = new DeviceIdentifier();
                id.setId(deviceId);
                id.setType(AndroidConstants.DEVICE_TYPE_ANDROID);
                String deviceName = AndroidAPIUtils.getDeviceManagementService().getDevice(id, false).getName();
                notification.setOperationId(operation.getId());
                notification.setStatus(org.wso2.carbon.device.mgt.common.notification.mgt.Notification.
                        Status.NEW.toString());
                notification.setDescription(operation.getCode() + " operation failed to execute on device " +
                        deviceName + " (ID: " + deviceId + ")");
                AndroidAPIUtils.getNotificationManagementService().addNotification(id, notification);
            }
            if (log.isDebugEnabled()) {
                log.debug("Updating operation '" + operation.toString() + "'");
            }
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
        String latitude = "", longitude = "", altitude = "", speed = "", bearing = "", distance = "";

        if (properties == null) return null;

        for (Device.Property property : properties) {
            String propertyName = property.getName();
            if (propertyName == null) continue;
            if (propertyName.equals("LATITUDE")) {
                latitude = property.getValue();
            }
            if (propertyName.equals("LONGITUDE")) {
                longitude = property.getValue();
            }
            if (propertyName.equals("ALTITUDE")) {
                altitude = property.getValue();
            }
            if (propertyName.equals("SPEED")) {
                speed = property.getValue();
            }
            if (propertyName.equals("BEARING")) {
                bearing = property.getValue();
            }
            if (propertyName.equals("DISTANCE")) {
                distance = property.getValue();
            }
        }

        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude) &&
                StringUtils.isNotBlank(altitude) && StringUtils.isNotBlank(speed) &&
                StringUtils.isNotBlank(bearing) && StringUtils.isNotBlank(distance)) {
            location = new DeviceLocation();
            location.setLatitude(Double.valueOf(latitude));
            location.setLongitude(Double.valueOf(longitude));
            location.setAltitude(Double.valueOf(altitude));
            location.setSpeed(Float.valueOf(speed));
            location.setBearing(Float.valueOf(bearing));
            location.setDistance(Double.valueOf(distance));
            location.setDeviceIdentifier(deviceIdentifier);
            Device savedDevice = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier, false);
            location.setDeviceId(savedDevice.getId());
        }
        return location;
    }

    private int recursiveSync(GoogleAPIInvoker googleAPIInvoker, String enterpriseId, ProductsListResponse
            productsListResponse) throws EnterpriseServiceException, ApplicationManagementException {
        // Are there more pages
        if (productsListResponse == null || productsListResponse.getTokenPagination() == null
                || productsListResponse.getTokenPagination().getNextPageToken() == null) {
            return 0;
        }

        // Get next page
        ProductsListResponse productsListResponseNext = googleAPIInvoker.listProduct(enterpriseId,
                productsListResponse.getTokenPagination().getNextPageToken());
        AndroidEnterpriseUtils.persistApp(productsListResponseNext);
        if (productsListResponseNext != null && productsListResponseNext.getTokenPagination() != null &&
                productsListResponseNext.getTokenPagination().getNextPageToken() != null) {
            return recursiveSync(googleAPIInvoker, enterpriseId, productsListResponseNext)
                    + productsListResponseNext.getProduct().size();
        } else {
            return productsListResponseNext.getProduct().size();
        }
    }

    public String insertUser(EnterpriseUser enterpriseUser) throws EnterpriseServiceException {
        EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
        String token;
        boolean deviceIdExist = false;

        String googleUserId;
        List<AndroidEnterpriseUser> androidEnterpriseUsers = AndroidAPIUtils.getAndroidPluginService()
                .getEnterpriseUser(CarbonContext.getThreadLocalCarbonContext().getUsername());
        GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
        if (androidEnterpriseUsers != null && androidEnterpriseUsers.size() > 0) {
            googleUserId = androidEnterpriseUsers.get(0).getGoogleUserId();
            // If this device is also present, only need to provide a token for this request.
            for (AndroidEnterpriseUser enterprise : androidEnterpriseUsers) {
                if (enterprise.getEmmDeviceId() != null
                        && enterprise.getEmmDeviceId().equals(enterpriseUser.getAndroidPlayDeviceId())) {
                    deviceIdExist = true;
                }
            }
        } else {
            googleUserId = googleAPIInvoker.insertUser(enterpriseConfigs.getEnterpriseId(), CarbonContext
                    .getThreadLocalCarbonContext()
                    .getUsername());
        }
        // Fetching an auth token from Google EMM API
        token = googleAPIInvoker.getToken(enterpriseConfigs.getEnterpriseId(), googleUserId);

        if (!deviceIdExist) {
            AndroidEnterpriseUser androidEnterpriseUser = new AndroidEnterpriseUser();
            androidEnterpriseUser.setEmmUsername(CarbonContext.getThreadLocalCarbonContext().getUsername());
            androidEnterpriseUser.setTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantId());
            androidEnterpriseUser.setAndroidPlayDeviceId(enterpriseUser.getAndroidPlayDeviceId());
            androidEnterpriseUser.setEnterpriseId(enterpriseConfigs.getEnterpriseId());
            androidEnterpriseUser.setEmmDeviceId(enterpriseUser.getEmmDeviceIdentifier());
            androidEnterpriseUser.setGoogleUserId(googleUserId);

            AndroidAPIUtils.getAndroidPluginService().addEnterpriseUser(androidEnterpriseUser);
        }

        return token;

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
