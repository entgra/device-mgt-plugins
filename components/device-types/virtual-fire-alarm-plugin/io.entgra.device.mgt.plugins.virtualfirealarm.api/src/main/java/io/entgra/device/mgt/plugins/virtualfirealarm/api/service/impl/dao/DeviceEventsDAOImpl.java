/*
 * Copyright (C) 2018 - 2023 Entgra (Pvt) Ltd, Inc - All Rights Reserved.
 *
 * Unauthorised copying/redistribution of this file, via any medium is strictly prohibited.
 *
 * Licensed under the Entgra Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://entgra.io/licenses/entgra-commercial/1.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.plugins.virtualfirealarm.api.service.impl.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.plugins.virtualfirealarm.api.service.impl.dto.SensorRecord;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Implements MobileDeviceDAO for Android Devices.
 */
public class DeviceEventsDAOImpl implements DeviceEventsDAO {

    private static final Log log = LogFactory.getLog(DeviceEventsDAOImpl.class);

    @Override
    public SensorRecord getStats(String deviceId, long fromTime, long toTime) {
        String sql = "SELECT * FROM TABLE_VIRTUALFIREALARM_CARBONSUPER_RDBMS_PUBLISHER WHERE " +
                "META_DEVICEID = ? AND " +
                "META_TIME >= ?  AND " +
                "META_TIME <= ? " +
                "ORDER BY META_TIME ASC";
        Map<Long, Float> stats = new LinkedHashMap<>();
        try {
            DeviceEventsDAOFactory.init("jdbc/EVENT_DB");
            Connection conn = DeviceEventsDAOFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, deviceId);
                stmt.setLong(2, fromTime);
                stmt.setLong(3, toTime);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        stats.put(rs.getLong("META_TIME"), rs.getFloat("TEMPERATURE"));
                    }
                }
            } catch (SQLException e) {
                String msg = "Error occurred while retrieving device details";
                log.error(msg);

            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to retrieve device details";
            log.error(msg);

        }
//        stats.entrySet()
//                .stream()
//                .sorted(Map.Entry.<Long, Float>comparingByKey())
//                .forEach(System.out::println);
        return new SensorRecord(stats);
    }

}
