/*
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

package org.wso2.carbon.device.mgt.mobile.android.core.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseManagedConfig;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.AndroidDAOFactory;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.EnterpriseDAO;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.util.MobileDeviceManagementDAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implements EnterpriseDAO for Android Devices.
 */
public class EnterpriseDAOImpl implements EnterpriseDAO {

    private static final Log log = LogFactory.getLog(EnterpriseDAOImpl.class);

    public List<AndroidEnterpriseUser> getUser(String username, int tenantId) throws EnterpriseManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        List<AndroidEnterpriseUser> enterpriseUsers = new ArrayList<>();
        ResultSet rs = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String selectDBQuery =
                    "SELECT * FROM AD_ENTERPRISE_USER_DEVICE WHERE EMM_USERNAME = ? AND TENANT_ID = ?";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, username);
            stmt.setInt(2, tenantId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                AndroidEnterpriseUser enterpriseUser = new AndroidEnterpriseUser();
                enterpriseUser.setEmmUsername(rs.getString("EMM_USERNAME"));
                enterpriseUser.setTenantId(rs.getInt("TENANT_ID"));
                enterpriseUser.setLastUpdatedTime(rs.getString("LAST_UPDATED_TIMESTAMP"));
                enterpriseUser.setAndroidPlayDeviceId(rs.getString("ANDROID_PLAY_DEVICE_ID"));
                enterpriseUser.setEnterpriseId(rs.getString("ENTERPRISE_ID"));
                enterpriseUser.setGoogleUserId(rs.getString("GOOGLE_USER_ID"));
                enterpriseUser.setEmmDeviceId(rs.getString("EMM_DEVICE_ID"));
                enterpriseUsers.add(enterpriseUser);
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching user : '" + username + "'";
            log.error(msg, e);
            throw new EnterpriseManagementDAOException(msg, e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }

        return enterpriseUsers;
    }

    public AndroidEnterpriseUser getUserByDevice(String deviceId, int tenantId) throws
            EnterpriseManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        AndroidEnterpriseUser enterpriseUser = null;
        ResultSet rs = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String selectDBQuery =
                    "SELECT * FROM AD_ENTERPRISE_USER_DEVICE WHERE EMM_DEVICE_ID = ? AND TENANT_ID = ? " +
                            "ORDER BY LAST_UPDATED_TIMESTAMP DESC";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, deviceId);
            stmt.setInt(2, tenantId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                enterpriseUser = new AndroidEnterpriseUser();
                enterpriseUser.setEmmUsername(rs.getString("EMM_USERNAME"));
                enterpriseUser.setTenantId(rs.getInt("TENANT_ID"));
                enterpriseUser.setLastUpdatedTime(rs.getString("LAST_UPDATED_TIMESTAMP"));
                enterpriseUser.setAndroidPlayDeviceId(rs.getString("ANDROID_PLAY_DEVICE_ID"));
                enterpriseUser.setEnterpriseId(rs.getString("ENTERPRISE_ID"));
                enterpriseUser.setGoogleUserId(rs.getString("GOOGLE_USER_ID"));
                enterpriseUser.setEmmDeviceId(rs.getString("EMM_DEVICE_ID"));
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching user for device : '" + deviceId + "'";
            log.error(msg, e);
            throw new EnterpriseManagementDAOException(msg, e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }

        return enterpriseUser;
    }

    public boolean addUser(AndroidEnterpriseUser androidEnterpriseUser) throws EnterpriseManagementDAOException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String createDBQuery =
                    "INSERT INTO AD_ENTERPRISE_USER_DEVICE(EMM_USERNAME, TENANT_ID, LAST_UPDATED_TIMESTAMP" +
                            ", ANDROID_PLAY_DEVICE_ID, ENTERPRISE_ID, GOOGLE_USER_ID, EMM_DEVICE_ID)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, androidEnterpriseUser.getEmmUsername());
            stmt.setInt(2, androidEnterpriseUser.getTenantId());
            stmt.setTimestamp(3, new Timestamp(new Date().getTime()));
            stmt.setString(4, androidEnterpriseUser.getAndroidPlayDeviceId());
            stmt.setString(5, androidEnterpriseUser.getEnterpriseId());
            stmt.setString(6, androidEnterpriseUser.getGoogleUserId());
            stmt.setString(7, androidEnterpriseUser.getEmmDeviceId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Added user " + androidEnterpriseUser.getEmmUsername());
                }
            }
        } catch (SQLException e) {
            throw new EnterpriseManagementDAOException("Error occurred while adding the user "
                    + androidEnterpriseUser.getEmmUsername(), e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    public AndroidEnterpriseManagedConfig getConfigByPackageName(String packageName, int tenantId)
            throws EnterpriseManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        AndroidEnterpriseManagedConfig managedConfig = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String selectDBQuery =
                    "SELECT * FROM AD_ENTERPRISE_MANAGED_CONFIGS WHERE PACKAGE_NAME = ? AND TENANT_ID = ?";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, packageName);
            stmt.setInt(2, tenantId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                managedConfig = new AndroidEnterpriseManagedConfig();
                managedConfig.setId(rs.getInt("ID"));
                managedConfig.setMcmId(rs.getString("MCM_ID"));
                managedConfig.setProfileName(rs.getString("PROFILE_NAME"));
                managedConfig.setPackageName(rs.getString("PACKAGE_NAME"));
                managedConfig.setTenantID(rs.getInt("TENANT_ID"));
                managedConfig.setLastUpdatedTime(rs.getString("LAST_UPDATED_TIMESTAMP"));
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching config for package name : '" + packageName + "'";
            log.error(msg, e);
            throw new EnterpriseManagementDAOException(msg, e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
        return managedConfig;
    }

    public boolean addConfig(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseManagementDAOException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String createDBQuery =
                    "INSERT INTO AD_ENTERPRISE_MANAGED_CONFIGS(MCM_ID, PROFILE_NAME, PACKAGE_NAME" +
                            ", TENANT_ID, LAST_UPDATED_TIMESTAMP) VALUES (?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, managedConfig.getMcmId());
            stmt.setString(2, managedConfig.getProfileName());
            stmt.setString(3, managedConfig.getPackageName());
            stmt.setInt(4, managedConfig.getTenantID());
            stmt.setTimestamp(5, new Timestamp(new Date().getTime()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Added config for package " + managedConfig.getPackageName());
                }
            }
        } catch (SQLException e) {
            throw new EnterpriseManagementDAOException("Error occurred while adding the config for package "
                    + managedConfig.getPackageName(), e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    public boolean updateConfig(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseManagementDAOException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String updateDBQuery =
                    "UPDATE AD_ENTERPRISE_MANAGED_CONFIGS SET PROFILE_NAME = ?, LAST_UPDATED_TIMESTAMP = ?" +
                            " WHERE MCM_ID = ? AND TENANT_ID = ?";
            stmt = conn.prepareStatement(updateDBQuery);

            stmt.setString(1, managedConfig.getProfileName());
            stmt.setString(2, managedConfig.getLastUpdatedTime());
            stmt.setString(3, managedConfig.getMcmId());
            stmt.setInt(4, managedConfig.getTenantID());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Managed config for mcm id " + managedConfig.getMcmId() + " data has been" +
                            " modified.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while modifying the details for  mcm id '" +
                    managedConfig.getMcmId() + "' data.";
            log.error(msg, e);
            throw new EnterpriseManagementDAOException(msg, e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    public boolean deleteConfig(String id, int tenantId)
            throws EnterpriseManagementDAOException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String deleteDBQuery =
                    "DELETE FROM AD_ENTERPRISE_MANAGED_CONFIGS WHERE MCM_ID = ? AND TENANT_ID = ?";
            stmt = conn.prepareStatement(deleteDBQuery);
            stmt.setString(1, id);
            stmt.setInt(2, tenantId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Error when deleting MCM ID " + id);
                }
            }
        } catch (SQLException e) {
            throw new EnterpriseManagementDAOException("Error occurred while deleting MCM ID '" + id + "'", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

}
