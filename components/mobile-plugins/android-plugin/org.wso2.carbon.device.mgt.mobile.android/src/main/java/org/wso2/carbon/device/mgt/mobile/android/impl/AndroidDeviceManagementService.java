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
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.android.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManager;
import org.wso2.carbon.device.mgt.common.OperationMonitoringTaskConfig;
import org.wso2.carbon.device.mgt.common.ProvisioningConfig;
import org.wso2.carbon.device.mgt.common.InitialOperationConfig;
import org.wso2.carbon.device.mgt.common.DeviceStatusTaskPluginConfig;
import org.wso2.carbon.device.mgt.common.StartupOperationConfig;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManager;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.general.GeneralConfig;
import org.wso2.carbon.device.mgt.common.policy.mgt.PolicyMonitoringManager;
import org.wso2.carbon.device.mgt.common.pull.notification.PullNotificationSubscriber;
import org.wso2.carbon.device.mgt.common.push.notification.PushNotificationConfig;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagementService;
import org.wso2.carbon.device.mgt.common.type.mgt.DeviceTypePlatformDetails;
import org.wso2.carbon.device.mgt.mobile.android.impl.util.AndroidPluginConstants;
import org.wso2.carbon.device.mgt.mobile.android.internal.AndroidDeviceManagementDataHolder;

import java.util.HashMap;
import java.util.List;

/**
 * This represents the Android implementation of DeviceManagerService.
 */
public class AndroidDeviceManagementService implements DeviceManagementService {

    private static final Log log = LogFactory.getLog(AndroidDeviceManagementService.class);
    private DeviceManager deviceManager;
    public static final String DEVICE_TYPE_ANDROID = "android";
    private static final String SUPER_TENANT_DOMAIN = "carbon.super";
    private static final String NOTIFIER_PROPERTY = "notifierType";
    private static final String FCM_API_KEY = "fcmAPIKey";
    private static final String FCM_SENDER_ID = "fcmSenderId";
    private PolicyMonitoringManager policyMonitoringManager;

    @Override
    public String getType() {
        return AndroidDeviceManagementService.DEVICE_TYPE_ANDROID;
    }

    @Override
    public OperationMonitoringTaskConfig getOperationMonitoringConfig() {
        return null;
    }

    @Override
    public void init() throws DeviceManagementException {

    }

    @Override
    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    @Override
    public ApplicationManager getApplicationManager() {
        return null;
    }

    @Override
    public ProvisioningConfig getProvisioningConfig() {
        return new ProvisioningConfig(SUPER_TENANT_DOMAIN, true);
    }

    @Override
    public PushNotificationConfig getPushNotificationConfig() {
        return null;
    }

    @Override
    public PolicyMonitoringManager getPolicyMonitoringManager() {
        return policyMonitoringManager;
    }

    @Override
    public InitialOperationConfig getInitialOperationConfig() {
        return null;
    }

    @Override
    public StartupOperationConfig getStartupOperationConfig() {
        return null;
    }

    @Override
    public PullNotificationSubscriber getPullNotificationSubscriber() {
        return null;
    }

    @Override
    public DeviceStatusTaskPluginConfig getDeviceStatusTaskPluginConfig() {
        return null;
    }

    @Override
    public GeneralConfig getGeneralConfig() {
        return null;
    }

    @Override
    public DeviceTypePlatformDetails getDeviceTypePlatformDetails() { return null; }

    private String getConfigProperty(List<ConfigurationEntry> configs, String propertyName) {
        for (ConfigurationEntry entry : configs) {
            if (propertyName.equals(entry.getName())) {
                return entry.getValue().toString();
            }
        }
        return null;
    }
}
