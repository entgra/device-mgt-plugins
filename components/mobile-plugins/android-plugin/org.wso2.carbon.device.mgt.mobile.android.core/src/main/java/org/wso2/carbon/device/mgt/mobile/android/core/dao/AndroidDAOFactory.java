/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
*/

package org.wso2.carbon.device.mgt.mobile.android.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.impl.EnterpriseDAOImpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class AndroidDAOFactory extends AbstractMobileDeviceManagementDAOFactory {

    private static final Log log = LogFactory.getLog(AndroidDAOFactory.class);
    protected static DataSource dataSource;
    private static ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public AndroidDAOFactory() {
        this.dataSource = getDataSourceMap().get(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
    }

    public static EnterpriseDAO getEnterpriseDAO() {
        if (dataSource == null) {
            dataSource = getDataSourceMap().get(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        }

        return new EnterpriseDAOImpl();
    }

    public static void beginTransaction() throws EnterpriseManagementDAOException {
        try {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            currentConnection.set(conn);
        } catch (SQLException e) {
            throw new EnterpriseManagementDAOException("Error occurred while retrieving datasource connection", e);
        }
    }

    public static void openConnection() throws EnterpriseManagementDAOException {
        Connection conn = currentConnection.get();
        if (conn != null) {
            throw new EnterpriseManagementDAOException("A transaction is already active within the context of " +
                    "this particular thread. Therefore, calling 'beginTransaction/openConnection' while another " +
                    "transaction is already active is a sign of improper transaction handling");
        }
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new EnterpriseManagementDAOException("Error occurred while opening connection", e);
        }
        currentConnection.set(conn);
    }

    public static Connection getConnection() throws EnterpriseManagementDAOException {
        if (currentConnection.get() == null) {
            try {
                currentConnection.set(dataSource.getConnection());
            } catch (SQLException e) {
                throw new EnterpriseManagementDAOException("Error occurred while retrieving data source connection",
                        e);
            }
        }
        return currentConnection.get();
    }

    public static void commitTransaction() throws EnterpriseManagementDAOException {
        try {
            Connection conn = currentConnection.get();
            if (conn != null) {
                conn.commit();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence commit " +
                            "has not been attempted");
                }
            }
        } catch (SQLException e) {
            throw new EnterpriseManagementDAOException("Error occurred while committing the transaction", e);
        }
    }

    public static void closeConnection() {
        Connection conn = currentConnection.get();
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error("Error occurred while close the connection");
        }
        currentConnection.remove();
    }

    public static void rollbackTransaction() {
        try {
            Connection conn = currentConnection.get();
            if (conn != null) {
                conn.rollback();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence rollback " +
                            "has not been attempted");
                }
            }
        } catch (SQLException e) {
            log.warn("Error occurred while roll-backing the transaction", e);
        }
    }

}