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
package io.entgra.device.mgt.plugins.emqx.exhook;

import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.EnrolmentInfo;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.core.config.keymanager.KeyManagerConfigurations;

/**
 * Provides operations related to the ExServer functionality.
 */
public interface ExServerUtilityService {

    /**
     * Retrieves the current key manager configurations used by the ExServer.
     * @return {@link KeyManagerConfigurations} containing key and token management details
     */
    KeyManagerConfigurations getKeyManagerConfigurations();

    /**
     * Changes the enrolment status of a specific device.
     * @param var1 the unique identifier of the target device
     * @param var2 the new enrolment status to apply
     * @return {@code true} if the device status was successfully updated; {@code false} otherwise
     * @throws DeviceManagementException if an error occurs while updating the device status
     */
    boolean changeDeviceStatus(DeviceIdentifier var1, EnrolmentInfo.Status var2) throws DeviceManagementException;
}
