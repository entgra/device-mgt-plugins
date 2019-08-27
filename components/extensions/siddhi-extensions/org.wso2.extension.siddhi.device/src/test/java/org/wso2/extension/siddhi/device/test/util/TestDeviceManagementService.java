/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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
package org.wso2.extension.siddhi.device.test.util;

import org.wso2.carbon.device.mgt.common.DeviceManager;
import org.wso2.carbon.device.mgt.common.DeviceStatusTaskPluginConfig;
import org.wso2.carbon.device.mgt.common.InitialOperationConfig;
import org.wso2.carbon.device.mgt.common.MonitoringOperation;
import org.wso2.carbon.device.mgt.common.OperationMonitoringTaskConfig;
import org.wso2.carbon.device.mgt.common.ProvisioningConfig;
import org.wso2.carbon.device.mgt.common.StartupOperationConfig;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManager;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.general.GeneralConfig;
import org.wso2.carbon.device.mgt.common.policy.mgt.PolicyMonitoringManager;
import org.wso2.carbon.device.mgt.common.pull.notification.PullNotificationSubscriber;
import org.wso2.carbon.device.mgt.common.push.notification.PushNotificationConfig;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagementService;
import org.wso2.carbon.device.mgt.common.type.mgt.DeviceTypePlatformDetails;

import java.util.ArrayList;
import java.util.List;

public class TestDeviceManagementService implements DeviceManagementService {

    private String providerType;
    private String tenantDomain;

    public TestDeviceManagementService(String deviceType, String tenantDomain) {
        providerType = deviceType;
        this.tenantDomain = tenantDomain;
    }

    @Override
    public String getType() {
        return providerType;
    }

    @Override
    public OperationMonitoringTaskConfig getOperationMonitoringConfig() {
        OperationMonitoringTaskConfig taskConfig = new OperationMonitoringTaskConfig();
        taskConfig.setEnabled(true);
        taskConfig.setFrequency(3000);
        List<MonitoringOperation> monitoringOperations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MonitoringOperation monitoringOperation = new MonitoringOperation();
            monitoringOperation.setTaskName("OPERATION-" + i);
            monitoringOperation.setRecurrentTimes(i);
            monitoringOperations.add(monitoringOperation);
        }
        taskConfig.setMonitoringOperation(monitoringOperations);
        return taskConfig;
    }

    @Override
    public void init() throws DeviceManagementException {

    }

    @Override
    public DeviceManager getDeviceManager() {
        return new TestDeviceManager();
    }

    @Override
    public ApplicationManager getApplicationManager() {
        return null;
    }

    @Override
    public ProvisioningConfig getProvisioningConfig() {
        return new ProvisioningConfig(tenantDomain, false);
    }

    @Override
    public PushNotificationConfig getPushNotificationConfig() {
        return null;
    }

    @Override
    public PolicyMonitoringManager getPolicyMonitoringManager() {
        return null;
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
}
