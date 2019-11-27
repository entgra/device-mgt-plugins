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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.AndroidPlatformConfiguration;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.NotifierFrequency;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestExceptionDup;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class AndroidServiceImpl implements AndroidService {

    private static final Log log = LogFactory.getLog(AndroidServiceImpl.class);

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
}
