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

import com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.base.ServerConfiguration;

import org.wso2.carbon.device.mgt.analytics.data.publisher.exception.DataPublisherConfigurationException;
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
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.*;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.*;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.*;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

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
    private static final String EVENT_STREAM_DEFINITION = "org.wso2.iot.LocationStream";

    private Gson gson = new Gson();
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ALTITUDE = "altitude";
    private static final String SPEED = "speed";
    private static final String DISTANCE = "distance";
    private static final String BEARING = "bearing";
    private static final String TIME_STAMP = "timeStamp";
    private static final String LOCATION_EVENT_TYPE = "location";

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
            throw new BadRequestException(errorMessage);
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
                        throw new BadRequestException(msg);
                    }
                } else if (AndroidConstants.TenantConfigProperties.NOTIFIER_TYPE.equals(entry.getName())) {
                    if (entry.getValue() != null) {
                        notifierFrequency.setType(Integer.parseInt(entry.getValue().toString()));
                    } else {
                        String msg = "No value specified for notifierType.";
                        log.error(msg);
                        throw new BadRequestException(msg);
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
            responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_OK));
            responseMessage
                    .setResponseMessage("Status of android device that carries the id '" + id + "' is " + status);
            return responseMessage;
        } else {
            Message responseMessage = new Message();
            responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_NOT_FOUND));
            responseMessage.setResponseMessage("No Android device is found upon the id '" + id + "'");
            return responseMessage;
        }
    }

    @Override
    public Activity fileTransfer(FileTransferBeanWrapper fileTransferBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        if (fileTransferBeanWrapper == null || fileTransferBeanWrapper.getOperation() == null
                || fileTransferBeanWrapper.getDeviceIDs() == null) {
            String errorMessage = "The payload of the file transfer operation is incorrect.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
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
            return AndroidDeviceUtils
                    .getOperationResponse(fileTransferBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers ( " + fileTransferBeanWrapper.getDeviceIDs() + " ) found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity configureDeviceLock(DeviceLockBeanWrapper deviceLockBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        if (deviceLockBeanWrapper == null || deviceLockBeanWrapper.getOperation() == null) {
            String errorMessage = "Lock bean is empty.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        DeviceLock lock = deviceLockBeanWrapper.getOperation();
        ProfileOperation operation = new ProfileOperation();
        operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCK);
        operation.setType(Operation.Type.PROFILE);
        operation.setEnabled(true);
        operation.setPayLoad(lock.toJSON());

        try {
            return AndroidDeviceUtils.getOperationResponse(deviceLockBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity configureDeviceUnlock(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_UNLOCK);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity getDeviceLocation(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException{
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCATION);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity removePassword(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CLEAR_PASSWORD);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity configureCamera(CameraBeanWrapper cameraBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (cameraBeanWrapper == null || cameraBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the configure camera operation is incorrect.";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            Camera camera = cameraBeanWrapper.getOperation();
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CAMERA);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(camera.isEnabled());
            return AndroidDeviceUtils.getOperationResponse(cameraBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity getDeviceInformation(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_INFO);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity getDeviceLogcat(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.LOGCAT);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity wipeDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENTERPRISE_WIPE);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity wipeData(WipeDataBeanWrapper wipeDataBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (wipeDataBeanWrapper == null || wipeDataBeanWrapper.getOperation() == null) {
                String errorMessage = "WipeData bean is empty.";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            WipeData wipeData = wipeDataBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WIPE_DATA);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(wipeData.toJSON());
            return AndroidDeviceUtils.getOperationResponse(wipeDataBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity getApplications(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.APPLICATION_LIST);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity ringDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_RING);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity rebootDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_REBOOT);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity changeLockTask(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CHANGE_LOCK_TASK_MODE);
            operation.setType(Operation.Type.COMMAND);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity muteDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_MUTE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            return AndroidDeviceUtils.getOperationResponse(deviceIDs, operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity installApplication(ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (applicationInstallationBeanWrapper == null || applicationInstallationBeanWrapper.getOperation() ==
                    null) {
                String errorMessage = "The payload of the application installing operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }

            ApplicationInstallation applicationInstallation = applicationInstallationBeanWrapper.getOperation();
            validateApplicationUrl(applicationInstallation.getUrl());
            validateApplicationType(applicationInstallation.getType());
            validateScheduleDate(applicationInstallation.getSchedule());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.INSTALL_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationInstallation.toJSON());
            return AndroidDeviceUtils
                    .getOperationResponse(applicationInstallationBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity updateApplication(ApplicationUpdateBeanWrapper applicationUpdateBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (applicationUpdateBeanWrapper == null || applicationUpdateBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the application update operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            ApplicationUpdate applicationUpdate = applicationUpdateBeanWrapper.getOperation();
            validateApplicationUrl(applicationUpdate.getUrl());
            validateApplicationType(applicationUpdate.getType());
            validateScheduleDate(applicationUpdate.getSchedule());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UPDATE_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationUpdate.toJSON());

            return AndroidDeviceUtils
                    .getOperationResponse(applicationUpdateBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity uninstallApplication(ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (applicationUninstallationBeanWrapper == null ||
                    applicationUninstallationBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the application uninstalling operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            ApplicationUninstallation applicationUninstallation = applicationUninstallationBeanWrapper.getOperation();
            validateApplicationType(applicationUninstallation.getType());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UNINSTALL_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationUninstallation.toJSON());
            return AndroidDeviceUtils
                    .getOperationResponse(applicationUninstallationBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity blacklistApplications(BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (blacklistApplicationsBeanWrapper == null || blacklistApplicationsBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the blacklisting apps operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            BlacklistApplications blacklistApplications = blacklistApplicationsBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.BLACKLIST_APPLICATIONS);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(blacklistApplications.toJSON());
            return AndroidDeviceUtils
                    .getOperationResponse(blacklistApplicationsBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity upgradeFirmware(UpgradeFirmwareBeanWrapper upgradeFirmwareBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (upgradeFirmwareBeanWrapper == null || upgradeFirmwareBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the upgrade firmware operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            UpgradeFirmware upgradeFirmware = upgradeFirmwareBeanWrapper.getOperation();
            validateScheduleDate(upgradeFirmware.getSchedule());

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UPGRADE_FIRMWARE);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(upgradeFirmware.toJSON());
            return AndroidDeviceUtils
                    .getOperationResponse(upgradeFirmwareBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity configureVPN(VpnBeanWrapper vpnConfiguration)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (vpnConfiguration == null || vpnConfiguration.getOperation() == null) {
                String errorMessage = "The payload of the VPN operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            Vpn vpn = vpnConfiguration.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.VPN);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(vpn.toJSON());
            return AndroidDeviceUtils.getOperationResponse(vpnConfiguration.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity sendNotification(NotificationBeanWrapper notificationBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException{
        try {
            if (notificationBeanWrapper == null || notificationBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the notification operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            Notification notification = notificationBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.NOTIFICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(notification.toJSON());
            return AndroidDeviceUtils.getOperationResponse(notificationBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity configureAPN(APNBeanWrapper apnBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (apnBeanWrapper == null || apnBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the apn operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            APN apn = apnBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.APN);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(apn.toJSON());
            return AndroidDeviceUtils.getOperationResponse(apnBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }


    @Override
    public Activity configureWifi(WifiBeanWrapper wifiBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (wifiBeanWrapper == null || wifiBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the wifi operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            Wifi wifi = wifiBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WIFI);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(wifi.toJSON());

            return AndroidDeviceUtils.getOperationResponse(wifiBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity encryptStorage(EncryptionBeanWrapper encryptionBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException{
        try {
            if (encryptionBeanWrapper == null || encryptionBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the device encryption operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            DeviceEncryption deviceEncryption = encryptionBeanWrapper.getOperation();
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENCRYPT_STORAGE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(deviceEncryption.isEncrypted());
            return AndroidDeviceUtils.getOperationResponse(encryptionBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity changeLockCode(LockCodeBeanWrapper lockCodeBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (lockCodeBeanWrapper == null || lockCodeBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the change lock code operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            LockCode lockCode = lockCodeBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.CHANGE_LOCK_CODE);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(lockCode.toJSON());
            return AndroidDeviceUtils.getOperationResponse(lockCodeBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity setPasswordPolicy(PasswordPolicyBeanWrapper passwordPolicyBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (passwordPolicyBeanWrapper == null || passwordPolicyBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the change password policy operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            PasscodePolicy passcodePolicy = passwordPolicyBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.PASSCODE_POLICY);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(passcodePolicy.toJSON());

            return AndroidDeviceUtils
                    .getOperationResponse(passwordPolicyBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity setWebClip(WebClipBeanWrapper webClipBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {

            if (webClipBeanWrapper == null || webClipBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the add webclip operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            WebClip webClip = webClipBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WEBCLIP);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(webClip.toJSON());

            return AndroidDeviceUtils.getOperationResponse(webClipBeanWrapper.getDeviceIDs(), operation);
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Activity setRecommendedGlobalProxy(GlobalProxyBeanWrapper globalProxyBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (globalProxyBeanWrapper == null || globalProxyBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the global proxy operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }

            GlobalProxy globalProxy = globalProxyBeanWrapper.getOperation();
            if (globalProxy.validateRequest()) {
                ProfileOperation operation = new ProfileOperation();
                operation.setCode(AndroidConstants.OperationCodes.GLOBAL_PROXY);
                operation.setType(Operation.Type.PROFILE);
                operation.setPayLoad(globalProxy.toJSON());

                return AndroidDeviceUtils
                        .getOperationResponse(globalProxyBeanWrapper.getDeviceIDs(), operation);
            } else {
                String errorMessage = "The payload of the global proxy operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public ProfileOperation sendApplicationConfiguration(ApplicationRestrictionBeanWrapper applicationRestrictionBeanWrapper)
            throws AndroidDeviceMgtPluginException {
        try {
            if (applicationRestrictionBeanWrapper == null || applicationRestrictionBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the application configuration operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            ApplicationRestriction applicationRestriction = applicationRestrictionBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.REMOTE_APP_CONFIG);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationRestriction.toJSON());
            return operation;
        } catch (BadRequestException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }


    @Override
    public Activity configureDisplayMessage(DisplayMessageBeanWrapper displayMessageBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException {
        try {
            if (displayMessageBeanWrapper == null || displayMessageBeanWrapper.getOperation() == null) {
                String errorMessage = "The payload of the display message operation is incorrect";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            DisplayMessage configureDisplayMessage = displayMessageBeanWrapper.getOperation();
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.DISPLAY_MESSAGE_CONFIGURATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(configureDisplayMessage.toJSON());

            return AndroidDeviceUtils.getOperationResponse(displayMessageBeanWrapper.
                    getDeviceIDs(), operation);

        } catch (InvalidDeviceException e) {
            String errorMessage = "Invalid Device Identifiers found.";
            log.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }

    @Override
    public Message updateApplicationList(String id, List<AndroidApplication> androidApplications)
            throws org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException {
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
    public List<? extends Operation> getPendingOperations(DeviceIdentifier deviceIdentifier,
            List<? extends Operation> resultOperations)
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        try {
            if (!AndroidDeviceUtils.isValidDeviceIdentifier(deviceIdentifier)) {
                String msg = "Device not found for identifier '" + deviceIdentifier.getId() + "'";
                log.error(msg);
                throw new InvalidDeviceException(msg);
            }
            if (log.isDebugEnabled()) {
                log.debug("Invoking Android pending operations:" + deviceIdentifier.getId());
            }
            if (resultOperations != null && !resultOperations.isEmpty()) {
                updateOperations(deviceIdentifier.getId(), resultOperations);
            }
        } catch (OperationManagementException e) {
            String msg = "Issue in retrieving operation management service instance";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        } catch (PolicyComplianceException e) {
            String msg = "Issue in updating Monitoring operation";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }  catch (NotificationManagementException e) {
            String msg = "Issue in retrieving Notification management service instance";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        } catch (org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException e) {
            String msg = "Issue in retrieving application management service instance";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        try {
            return AndroidDeviceUtils.getPendingOperations(deviceIdentifier);
        } catch (OperationManagementException e) {
            String msg = "Issue in retrieving operation management service instance";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg, e);
        }
    }

    private void updateOperations(String deviceId, List<? extends Operation> operations)
            throws OperationManagementException, PolicyComplianceException, NotificationManagementException,
            DeviceManagementException, org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException {
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


    @Override
    public Message enrollDevice(AndroidDevice androidDevice)
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
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
                        throw new UnexpectedServerErrorException(msg, e);
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
                responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_OK));
                responseMessage.setResponseMessage("Android device, which carries the id '" +
                        androidDevice.getDeviceIdentifier() + "' has successfully been enrolled");
                return responseMessage;
            } else {
                String msg = "Failed to enroll '" + device.getType() + "' device, which carries the id '" +
                        androidDevice.getDeviceIdentifier() + "'";
                log.error(msg);
                throw new DeviceManagementException(msg);
            }
        } catch (PolicyManagementException | InvalidDeviceException | OperationManagementException e) {
            String msg = "Error occurred while enforcing default enrollment policy upon android " +
                    "', which carries the id '" +
                    androidDevice.getDeviceIdentifier() + "'";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg, e);
        }
    }

    @Override
    public boolean modifyEnrollment(String id, AndroidDevice androidDevice)
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
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
            throw new UnexpectedServerErrorException(msg, e);
        }

        if (androidDevice == null) {
            String errorMessage = "The payload of the android device enrollment is incorrect.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        if (device == null) {
            String errorMessage = "The device to be modified doesn't exist.";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
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
        return AndroidAPIUtils.getDeviceManagementService().modifyEnrollment(device);
    }

    @Override
    public boolean disEnrollDevice(String id) throws DeviceManagementException{
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(id);
        AndroidDeviceUtils.updateDisEnrollOperationStatus(deviceIdentifier);
        return AndroidAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
    }

    @Override
    public Message publishEvents(EventBeanWrapper eventBeanWrapper) throws AndroidDeviceMgtPluginException {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device event logging.");
        }
        Device device;
        try {
            if (!DeviceManagerUtil.isPublishLocationResponseEnabled()) {
                Message responseMessage = new Message();
                responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_ACCEPTED));
                responseMessage.setResponseMessage("Event is publishing has not enabled.");
                return responseMessage;
            }
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(eventBeanWrapper.getDeviceIdentifier(),
                    AndroidConstants.DEVICE_TYPE_ANDROID);
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
            if (device != null && EnrolmentInfo.Status.ACTIVE != device.getEnrolmentInfo().getStatus()){
                Message responseMessage = new Message();
                responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_ACCEPTED));
                responseMessage.setResponseMessage("Device is not in Active state.");
                return responseMessage;
            } else if (device == null){
                Message responseMessage = new Message();
                responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_ACCEPTED));
                responseMessage.setResponseMessage("Device is not enrolled yet.");
                return responseMessage;
            }
        } catch (DeviceManagementException e) {
            log.error("Error occurred while checking Operation Analytics is Enabled.", e);
            Message responseMessage = new Message();
            responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_SERVER_ERROR));
            responseMessage.setResponseMessage(e.getMessage());
            return responseMessage;
        }
        String eventType = eventBeanWrapper.getType();
        if (!LOCATION_EVENT_TYPE.equals(eventType)) {
            String msg = "Dropping Android " + eventType + " Event.Only Location Event Type is supported.";
            log.warn(msg);
            Message responseMessage = new Message();
            responseMessage.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_BAD_REQUEST));
            responseMessage.setResponseMessage(msg);
            return responseMessage;
        }
        Message message = new Message();
        Object[] metaData = {eventBeanWrapper.getDeviceIdentifier(), device.getEnrolmentInfo().getOwner(),
                AndroidConstants.DEVICE_TYPE_ANDROID};
        String eventPayload = eventBeanWrapper.getPayload();
        JsonObject jsonObject = gson.fromJson(eventPayload, JsonObject.class);
        Object[] payload = {
                jsonObject.get(TIME_STAMP).getAsLong(),
                jsonObject.get(LATITUDE).getAsDouble(),
                jsonObject.get(LONGITUDE).getAsDouble(),
                jsonObject.get(ALTITUDE).getAsDouble(),
                jsonObject.get(SPEED).getAsFloat(),
                jsonObject.get(BEARING).getAsFloat(),
                jsonObject.get(DISTANCE).getAsDouble()
        };
        try {
            if (AndroidAPIUtils.getEventPublisherService().publishEvent(
                    EVENT_STREAM_DEFINITION, "1.0.0", metaData, new Object[0], payload)) {
                message.setResponseCode("Event is published successfully.");
                return message;
            } else {
                log.warn("Error occurred while trying to publish the event. This could be due to unavailability " +
                        "of the publishing service. Please make sure that analytics server is running and accessible " +
                        "by this server");
                String errorMessage = "Error occurred due to " +
                        "unavailability of the publishing service.";
                message.setResponseCode(String.valueOf(HttpStatusCodes.STATUS_CODE_SERVICE_UNAVAILABLE));
                return message;
            }
        } catch (DataPublisherConfigurationException e) {
            String msg = "Error occurred while getting the Data publisher Service instance.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg);
        }

    }

    @Override
    public List<DeviceState> retrieveAlerts(String deviceId,
                                            long from,
                                            long to,
                                            String type,
                                            String ifModifiedSince) throws AndroidDeviceMgtPluginException {
        if (from != 0l && to != 0l && deviceId != null){
            return retrieveAlertFromDate(deviceId, from, to);
        } else if (deviceId != null && type != null) {
            return retrieveAlertByType(deviceId, type);
        } else if (deviceId != null) {
            return retrieveAlert(deviceId);
        } else {
            String errorMessage = "Request must contain " +
                    "the device identifier. Optionally, both from and to value should be present to get " +
                    "alerts between times.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }

    private List<DeviceState> retrieveAlert(String deviceId) throws NotFoundException, UnexpectedServerErrorException {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device Identifier.");
        }
        String query = "deviceIdentifier:" + deviceId;
        List<DeviceState> deviceStates;
        try {
            deviceStates = AndroidDeviceUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
            if (deviceStates == null) {
                String errorMessage = "No any alerts are " +
                        "published for Device: " + deviceId + ".";
                throw new NotFoundException(errorMessage);
            } else {
                return deviceStates;
            }
        } catch (AnalyticsException e) {
            String msg = "Error occurred while getting published events for specific device: " + deviceId + ".";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg, e);
        }
    }

    private List<DeviceState> retrieveAlertFromDate(String deviceId, long from, long to) throws NotFoundException,
            UnexpectedServerErrorException {
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
                String errorMessage = "No any alerts are " +
                        "published on given date for given Device: " + deviceId + ".";
                throw new NotFoundException(errorMessage);

            } else {
                return deviceStates;
            }
        } catch (AnalyticsException e) {
            String msg = "Error occurred while getting published events for specific " +
                    "Device: " + deviceId + " on given Date.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg, e);
        }
    }

    private List<DeviceState> retrieveAlertByType(String deviceId, String type)
            throws NotFoundException, UnexpectedServerErrorException {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device identifier and type.");
        }
        String query = "deviceIdentifier:" + deviceId + " AND type:" + type;
        List<DeviceState> deviceStates;
        try {
            deviceStates = AndroidDeviceUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
            if (deviceStates == null) {
                String errorMessage = "No any alerts are " +
                        "published for given Device: '" + deviceId + "' and given specific Type.";
                throw new NotFoundException(errorMessage);

            } else {
                return deviceStates;
            }
        } catch (AnalyticsException e) {
            String msg = "Error occurred while getting published events for specific " +
                    "Device: " + deviceId + "and given specific Type.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg, e);
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

    private static void validateApplicationUrl(String apkUrl) throws BadRequestException {
        try {
            URL url = new URL(apkUrl);
            URLConnection conn = url.openConnection();
            if (((HttpURLConnection) conn).getResponseCode() != HttpURLConnection.HTTP_OK) {
                String errorMessage = "URL is not pointed to a downloadable file.";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
        } catch (MalformedURLException e) {
            String errorMessage = "Malformed application url.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        } catch (IOException e) {
            String errorMessage = "Invalid application url.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }

    private static void validateApplicationType(String type) throws BadRequestException {
        if (type != null) {
            if (!"enterprise".equalsIgnoreCase(type)
                    && !"public".equalsIgnoreCase(type)
                    && !"webapp".equalsIgnoreCase(type)) {
                String errorMessage = "Invalid application type.";
                log.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
        } else {
            String errorMessage = "Application type is missing.";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }

    private static void validateScheduleDate(String dateString) throws BadRequestException {
        try {
            if (dateString != null && !dateString.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                sdf.setLenient(false);
                sdf.parse(dateString);
            }
        } catch (ParseException e) {
            String errorMessage = "Issue in validating the schedule date";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }
}
