/*
 * Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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
package io.entgra.device.mgt.plugins.emqx.initializer;

import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.EnrolmentInfo;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.core.config.keymanager.KeyManagerConfigurations;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.plugins.emqx.exhook.ExServerUtilityService;

public class ExServerUtilityServiceImpl implements ExServerUtilityService {

    private final DeviceManagementProviderService deviceManagementProviderService;

    public ExServerUtilityServiceImpl(DeviceManagementProviderService deviceManagementProviderService) {
        this.deviceManagementProviderService = deviceManagementProviderService;
    }
    @Override
    public KeyManagerConfigurations getKeyManagerConfigurations() {
        return deviceManagementProviderService.getDeviceManagementConfig().getKeyManagerConfigurations();
    }

    @Override
    public boolean changeDeviceStatus(DeviceIdentifier var1, EnrolmentInfo.Status var2) throws DeviceManagementException {
        return deviceManagementProviderService.changeDeviceStatus(var1, var2);
    }
}
