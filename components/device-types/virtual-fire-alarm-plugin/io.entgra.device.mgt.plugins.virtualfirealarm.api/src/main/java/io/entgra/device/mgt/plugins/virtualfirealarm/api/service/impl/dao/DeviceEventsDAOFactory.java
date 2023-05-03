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

import org.wso2.carbon.device.mgt.common.exceptions.*;
import org.wso2.carbon.device.mgt.core.dao.util.*;

public class DeviceEventsDAOFactory {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DeviceEventsDAOFactory.class);
    private static javax.sql.DataSource dataSource;
    private static String databaseEngine;
    private static final ThreadLocal<java.sql.Connection> currentConnection = new ThreadLocal<>();

    public static void init(String  jndiName) {
        dataSource = DeviceManagementDAOUtil.lookupDataSource(jndiName, null);
        try {
            databaseEngine = dataSource.getConnection().getMetaData().getDatabaseProductName();
        } catch (java.sql.SQLException e) {
            log.error("Error occurred while retrieving config.datasource connection", e);
        }
    }

    public static DeviceEventsDAO  getDeviceEventDao() {
        return new DeviceEventsDAOImpl();
    }

    public static void openDBConnection() throws DBConnectionException {
        java.sql.Connection conn = currentConnection.get();
        if (conn != null) {
            throw new IllegalTransactionStateException("Database connection has already been obtained.");
        }
        try {
            conn = dataSource.getConnection();
        } catch (java.sql.SQLException e) {
            throw new DBConnectionException("Failed to get a database connection.", e);
        }
        currentConnection.set(conn);
    }

    public static void beginTransaction() throws DBConnectionException {
        try {
            java.sql.Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            currentConnection.set(conn);
        } catch (java.sql.SQLException e) {
            throw new DBConnectionException("Error occurred while retrieving datasource connection", e);
        }
    }

    public static java.sql.Connection getConnection() throws DBConnectionException {
        if (currentConnection.get() == null) {
            try {
                currentConnection.set(dataSource.getConnection());
            } catch (java.sql.SQLException e) {
                throw new DBConnectionException("Error occurred while retrieving data source connection", e);
            }
        }
        return currentConnection.get();
    }

    public static void commitTransaction() throws DBConnectionException {
        try {
            java.sql.Connection conn = currentConnection.get();
            if (conn != null) {
                conn.commit();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence commit " +
                            "has not been attempted");
                }
            }
        } catch (java.sql.SQLException e) {
            throw new DBConnectionException("Error occurred while committing the transaction", e);
        }
    }

    public static void closeConnection() {
        java.sql.Connection conn = currentConnection.get();
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (java.sql.SQLException e) {
            log.error("Error occurred while close the connection");
        }
        currentConnection.remove();
    }

    public static void rollbackTransaction() {
        java.sql.Connection conn = currentConnection.get();
        if (conn == null) {
            throw new org.wso2.carbon.device.mgt.common.exceptions.IllegalTransactionStateException("Database connection is not active. Hence, rollback is "
                    + "not attempted.");
        }
        try {
            conn.rollback();
        } catch (java.sql.SQLException e) {
            log.warn("Error occurred while roll-backing the transaction", e);
        }
    }

}
