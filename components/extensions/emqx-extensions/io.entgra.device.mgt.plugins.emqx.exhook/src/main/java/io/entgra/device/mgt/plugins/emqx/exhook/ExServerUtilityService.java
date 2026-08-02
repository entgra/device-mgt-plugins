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
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.core.config.keymanager.KeyManagerConfigurations;
import io.entgra.device.mgt.plugins.emqx.exhook.dto.EmqxConnection;

import java.sql.SQLException;

public interface ExServerUtilityService {

    /**
     * Retrieves the Key Manager configuration used for authentication and
     * authorization of MQTT clients.
     *
     * <p>This configuration is typically required by EMQX ExHook
     * implementations to validate access tokens presented during
     * client connection and authorization events.</p>
     *
     * @return the {@link KeyManagerConfigurations} instance
     */
    KeyManagerConfigurations getKeyManagerConfigurations();

    /**
     * Persists or updates EMQX connection details for a given MQTT client.
     *
     * <p>If a connection record already exists for the specified client ID,
     * the implementation must update it atomically. This method is expected
     * to be safe for concurrent execution in clustered EMQX deployments.</p>
     *
     * @param emqxConnection the EMQX connection metadata to be stored
     * @throws DBConnectionException if a database connection or persistence
     *                               error occurs
     */
    void saveEmqxConnectionDetails(EmqxConnection emqxConnection) throws DBConnectionException, SQLException;

    /**
     * Retrieves EMQX connection details associated with the given MQTT client ID.
     *
     * @param clientId the MQTT client identifier
     * @return the {@link EmqxConnection} details, or {@code null} if no record exists
     * @throws DBConnectionException if a database connection error occurs
     */
    EmqxConnection getEmqxConnectionDetailsByClientId(String clientId) throws DBConnectionException, SQLException;

    /**
     * Deletes EMQX connection details associated with the given MQTT client ID.
     *
     * <p>This method is typically invoked when a client disconnects or when
     * the connection is deemed invalid.</p>
     *
     * @param clientId the MQTT client identifier
     * @throws DBConnectionException if a database connection or deletion error occurs
     */
    void deleteEmqxConnectionDetailsByClientId(String clientId) throws DBConnectionException, SQLException;

    /**
     * Changes the enrolment status of a specific device.
     * @param var1 the unique identifier of the target device
     * @param var2 the new enrolment status to apply
     * @return {@code true} if the device status was successfully updated; {@code false} otherwise
     * @throws DeviceManagementException if an error occurs while updating the device status
     */
    boolean changeDeviceStatus(DeviceIdentifier var1, EnrolmentInfo.Status var2) throws DeviceManagementException;
}
