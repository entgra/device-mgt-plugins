/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.android.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.AndroidDAOFactory;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.AndroidFeatureManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.MobileFeatureDAO;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.MobileFeature;
import org.wso2.carbon.device.mgt.mobile.android.impl.util.AndroidPluginConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AndroidFeatureDAOImpl implements MobileFeatureDAO {

    private static final Log log = LogFactory.getLog(AndroidFeatureDAOImpl.class);

    public AndroidFeatureDAOImpl() {

    }

    @Override
    public boolean addFeature(MobileFeature mobileFeature) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        boolean status = false;
        Connection conn;
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "INSERT INTO AD_FEATURE(CODE, NAME, TYPE, HIDDEN, DESCRIPTION) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, mobileFeature.getCode());
            stmt.setString(2, mobileFeature.getName());
            stmt.setString(3, mobileFeature.getType());
            stmt.setBoolean(4, mobileFeature.isHidden());
            stmt.setString(5, mobileFeature.getDescription());
            stmt.executeUpdate();
            status = true;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException(
                    "Error occurred while adding android feature '" +
                            mobileFeature.getName() + "' into the metadata repository", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    @Override
    public boolean addFeatures(List<MobileFeature> mobileFeatures) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        Connection conn;
        try {
            conn = AndroidDAOFactory.getConnection();
            stmt = conn.prepareStatement("INSERT INTO AD_FEATURE(CODE, NAME, TYPE, HIDDEN, DESCRIPTION) " +
                    "VALUES (?, ?, ?, ?, ?)");
            for (MobileFeature mobileFeature : mobileFeatures) {
                stmt.setString(1, mobileFeature.getCode());
                stmt.setString(2, mobileFeature.getName());
                stmt.setString(3, mobileFeature.getType());
                stmt.setBoolean(4, mobileFeature.isHidden());
                stmt.setString(5, mobileFeature.getDescription());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException(
                    "Error occurred while adding android features into the metadata repository", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
    }

    @Override
    public boolean updateFeature(MobileFeature mobileFeature) throws MobileDeviceManagementDAOException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidDAOFactory.getConnection();
            String updateDBQuery =
                    "UPDATE AD_FEATURE SET NAME = ?, TYPE = ?, HIDDEN = ? ,DESCRIPTION = ?" +
                            "WHERE CODE = ?";

            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setString(1, mobileFeature.getName());
            stmt.setString(2, mobileFeature.getType());
            stmt.setBoolean(3, mobileFeature.isHidden());
            stmt.setString(4, mobileFeature.getDescription());
            stmt.setString(5, mobileFeature.getCode());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Android Feature " + mobileFeature.getCode() + " data has been " +
                            "modified.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while updating the Android Feature '" +
                    mobileFeature.getCode() + "' to the Android db.";
            log.error(msg, e);
            throw new AndroidFeatureManagementDAOException(msg, e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    @Override
    public boolean deleteFeatureById(int mblFeatureId) throws MobileDeviceManagementDAOException {

        PreparedStatement stmt = null;
        boolean status = false;
        Connection conn;
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "DELETE FROM AD_FEATURE WHERE ID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mblFeatureId);
            stmt.execute();
            status = true;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException(
                    "Error occurred while deleting android feature '" +
                            mblFeatureId + "' from Android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    @Override
    public boolean deleteFeatureByCode(String mblFeatureCode) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        boolean status;
        Connection conn;
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "DELETE FROM AD_FEATURE WHERE CODE = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, mblFeatureCode);
            stmt.execute();
            status = true;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException(
                    "Error occurred while deleting android feature '" +
                            mblFeatureCode + "' from Android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    @Override
    public MobileFeature getFeatureById(int mblFeatureId) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn;
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "SELECT ID, CODE, NAME, TYPE, HIDDEN, DESCRIPTION FROM AD_FEATURE WHERE ID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mblFeatureId);
            rs = stmt.executeQuery();

            MobileFeature mobileFeature = null;
            if (rs.next()) {
                mobileFeature = new MobileFeature();
                mobileFeature.setId(rs.getInt(AndroidPluginConstants.ANDROID_FEATURE_ID));
                mobileFeature.setCode(rs.getString(AndroidPluginConstants.ANDROID_FEATURE_CODE));
                mobileFeature.setName(rs.getString(AndroidPluginConstants.ANDROID_FEATURE_NAME));
                mobileFeature.setType(rs.getString(AndroidPluginConstants.ANDROID_FEATURE_TYPE));
                mobileFeature.setHidden(rs.getBoolean(AndroidPluginConstants.ANDROID_FEATURE_HIDDEN));
                mobileFeature.setDescription(rs.getString(AndroidPluginConstants.
                        ANDROID_FEATURE_DESCRIPTION));
                mobileFeature.setDeviceType(
                        DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            }
            return mobileFeature;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException(
                    "Error occurred while retrieving android feature '" +
                            mblFeatureId + "' from the Android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public MobileFeature getFeatureByCode(String mblFeatureCode) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn;

        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "SELECT ID, CODE, NAME, TYPE, HIDDEN, DESCRIPTION FROM AD_FEATURE WHERE CODE = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, mblFeatureCode);
            rs = stmt.executeQuery();

            MobileFeature mobileFeature = null;
            if (rs.next()) {
                mobileFeature = populateMobileFeature(rs);
            }
            return mobileFeature;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException(
                    "Error occurred while retrieving android feature '" +
                            mblFeatureCode + "' from the Android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public List<MobileFeature> getFeatureByDeviceType(String deviceType)
            throws MobileDeviceManagementDAOException {
        return this.getAllFeatures();
    }

    @Override
    public List<MobileFeature> getAllFeatures() throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<MobileFeature> features = new ArrayList<>();
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "SELECT ID, CODE, NAME, TYPE, HIDDEN, DESCRIPTION FROM AD_FEATURE";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                features.add(populateMobileFeature(rs));
            }
            return features;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException("Error occurred while retrieving all android features " +
                    "from the android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public List<MobileFeature> getAllFeatures(boolean isHidden) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<MobileFeature> features = new ArrayList<>();
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "SELECT ID, CODE, NAME, TYPE, HIDDEN, DESCRIPTION FROM AD_FEATURE WHERE HIDDEN = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isHidden);
            rs = stmt.executeQuery();

            while (rs.next()) {
                features.add(populateMobileFeature(rs));
            }
            return features;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException("Error occurred while retrieving all android features " +
                    "from the android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public List<MobileFeature> getFeaturesByFeatureType(String featureType) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn;
        List<MobileFeature> features = new ArrayList<>();
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "SELECT ID, CODE, NAME, TYPE, HIDDEN, DESCRIPTION FROM AD_FEATURE WHERE TYPE = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, featureType);
            rs = stmt.executeQuery();

            while (rs.next()) {
                features.add(populateMobileFeature(rs));
            }
            return features;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException("Error occurred while retrieving all android features of " +
                    "type " + featureType + " from the android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public List<MobileFeature> getFeaturesByFeatureType(String featureType, boolean isHidden) throws MobileDeviceManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn;
        List<MobileFeature> features = new ArrayList<>();
        try {
            conn = AndroidDAOFactory.getConnection();
            String sql = "SELECT ID, CODE, NAME, TYPE, HIDDEN, DESCRIPTION " +
                    "FROM AD_FEATURE " +
                    "WHERE TYPE = ? AND HIDDEN = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, featureType);
            stmt.setBoolean(2, isHidden);
            rs = stmt.executeQuery();

            while (rs.next()) {
                features.add(populateMobileFeature(rs));
            }
            return features;
        } catch (SQLException e) {
            throw new AndroidFeatureManagementDAOException("Error occurred while retrieving all android features of " +
                    "[type: " + featureType + " & hidden: " + isHidden + "] from the android database.", e);
        } finally {
            MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
        }
    }

    /**
     * Generate {@link MobileFeature} from the SQL {@link ResultSet}
     *
     * @param rs Result set
     * @return populated {@link MobileFeature}
     * @throws SQLException if unable to extract data from {@link ResultSet}
     */
    private MobileFeature populateMobileFeature(ResultSet rs) throws SQLException {
        MobileFeature mobileFeature = new MobileFeature();
        mobileFeature.setId(rs.getInt(AndroidPluginConstants.ANDROID_FEATURE_ID));
        mobileFeature.setCode(rs.getString(AndroidPluginConstants.ANDROID_FEATURE_CODE));
        mobileFeature.setName(rs.getString(AndroidPluginConstants.ANDROID_FEATURE_NAME));
        mobileFeature.setDescription(rs.getString(AndroidPluginConstants.
                ANDROID_FEATURE_DESCRIPTION));
        mobileFeature.setType(rs.getString(AndroidPluginConstants.ANDROID_FEATURE_TYPE));
        mobileFeature.setHidden(rs.getBoolean(AndroidPluginConstants.ANDROID_FEATURE_HIDDEN));
        mobileFeature.setDeviceType(
                DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);

        return mobileFeature;
    }
}