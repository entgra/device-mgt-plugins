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

import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.MDMAppConstants;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.common.spi.DeviceTypePluginService;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AndroidPluginServiceImpl implements DeviceTypePluginService {

    @Override
    public Map<EnrolmentInfo.OwnerShip, String> getEnrollmentQRCode() throws DeviceManagementException {
        return null;
    }

//    private void getPlatformConfig() {
//        PlatformConfiguration platformConfiguration;
//        List<ConfigurationEntry> configs;
//        try {
//            platformConfiguration = AndroidAPIUtils.getDeviceManagementService().
//                    getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
//            if (platformConfiguration != null) {
//                configs = platformConfiguration.getConfiguration();
//            } else {
//                platformConfiguration = new PlatformConfiguration();
//                configs = new ArrayList<>();
//            }
//            ConfigurationEntry entry = new ConfigurationEntry();
//            License license = AndroidAPIUtils.getDeviceManagementService().getLicense(
//                    DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID, MDMAppConstants.AndroidConstants.
//                            TenantConfigProperties.LANGUAGE_US);
//
//            if (configs != null) {
//                ConfigurationEntry versionEntry = new ConfigurationEntry();
//                versionEntry.setContentType(AndroidConstants.TenantConfigProperties.CONTENT_TYPE_TEXT);
//                versionEntry.setName(AndroidConstants.TenantConfigProperties.SERVER_VERSION);
//                versionEntry.setValue(ServerConfiguration.getInstance().getFirstProperty("Version"));
//                configs.add(versionEntry);
//                if (license != null) {
//                    entry.setContentType(AndroidConstants.TenantConfigProperties.CONTENT_TYPE_TEXT);
//                    entry.setName(AndroidConstants.TenantConfigProperties.LICENSE_KEY);
//                    entry.setValue(license.getText());
//                    configs.add(entry);
//                }
//                platformConfiguration.setConfiguration(configs);
//            }
//    }
}
