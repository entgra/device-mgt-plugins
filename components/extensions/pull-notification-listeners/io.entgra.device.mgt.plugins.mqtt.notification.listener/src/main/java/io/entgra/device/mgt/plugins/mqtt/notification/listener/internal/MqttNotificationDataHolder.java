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
package io.entgra.device.mgt.plugins.mqtt.notification.listener.internal;

import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterService;

public class MqttNotificationDataHolder {

    private DeviceManagementProviderService deviceManagementProviderService;
    private InputEventAdapterService inputEventAdapterService;

    private static MqttNotificationDataHolder thisInstance = new MqttNotificationDataHolder();

    public static MqttNotificationDataHolder getInstance() {
        return thisInstance;
    }

    public DeviceManagementProviderService getDeviceManagementProviderService() {
        return deviceManagementProviderService;
    }

    public void setDeviceManagementProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        this.deviceManagementProviderService = deviceManagementProviderService;
    }

    public InputEventAdapterService getInputEventAdapterService() {
        return inputEventAdapterService;
    }

    public void setInputEventAdapterService(
            InputEventAdapterService inputEventAdapterService) {
        this.inputEventAdapterService = inputEventAdapterService;
    }
}
