/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 * Copyright (c) 2018, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.android.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.policy.mgt.ProfileFeature;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class for holding Android device related util methods.
 */
public class AndroidDeviceUtils {

    private static Log log = LogFactory.getLog(AndroidDeviceUtils.class);

    private AndroidDeviceUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
        DeviceIdentifier identifier = new DeviceIdentifier();
        identifier.setId(deviceId);
        identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        return identifier;
    }

    public static Activity getOperationResponse(List<String> deviceIDs, Operation operation)
            throws OperationManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        if (deviceIDs == null || deviceIDs.isEmpty()) {
            String errorMessage = "Device identifier list is empty";
            log.error(errorMessage);
            throw new BadRequestException(errorMessage);
        }
        DeviceIdentifier deviceIdentifier;
        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
        for (String deviceId : deviceIDs) {
            deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(AndroidConstants.DEVICE_TYPE_ANDROID);
            deviceIdentifiers.add(deviceIdentifier);
        }
        return AndroidAPIUtils.getDeviceManagementService().addOperation(
                DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID, operation, deviceIdentifiers);
    }

    public static String getAndroidConfig(PlatformConfiguration configuration, String key) {
        String value = null;
        if (configuration != null && configuration.getConfiguration() != null && configuration
                .getConfiguration().size() > 0) {
            List<ConfigurationEntry> configurations = configuration.getConfiguration();
            for (ConfigurationEntry configurationEntry : configurations) {
                if (configurationEntry.getName().equals(key)) {
                    value = (String)configurationEntry.getValue();
                    break;
                }
            }
        }
        return value;
    }

    public static ProfileFeature getEnrollmentFeature(DeviceIdentifier deviceIdentifier) throws
             FeatureManagementException {
        PolicyManagerService policyManagerService = AndroidAPIUtils.getPolicyManagerService();

        List<ProfileFeature> effectiveProfileFeatures= policyManagerService.getEffectiveFeatures(deviceIdentifier);

        if (effectiveProfileFeatures != null) {
            for (ProfileFeature feature : effectiveProfileFeatures) {
                if (AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_FEATURE_CODE
                        .equals(feature.getFeatureCode())) {
                    return feature;
                }
            }
        }
        return null;
    }
}
