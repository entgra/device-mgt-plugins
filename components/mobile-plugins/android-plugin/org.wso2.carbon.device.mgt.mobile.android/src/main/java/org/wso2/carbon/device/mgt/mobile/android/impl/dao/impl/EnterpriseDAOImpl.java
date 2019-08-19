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

package org.wso2.carbon.device.mgt.mobile.android.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.AndroidDAOFactory;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.EnterpriseDAO;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.EnterpriseManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.MobileDevice;
import org.wso2.carbon.device.mgt.mobile.android.impl.util.AndroidPluginConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				AndroidEnterpriseUser enterpriseUser  = new AndroidEnterpriseUser();
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

	public boolean updateMobileDevice(MobileDevice mobileDevice) throws EnterpriseManagementDAOException {
		boolean status = false;
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = AndroidDAOFactory.getConnection();
			String updateDBQuery =
					"UPDATE AD_DEVICE SET FCM_TOKEN = ?, DEVICE_INFO = ?, SERIAL = ?, VENDOR = ?, " +
					"MAC_ADDRESS = ?, DEVICE_NAME = ?, LATITUDE = ?, LONGITUDE = ?, IMEI = ?, " +
					"IMSI = ?, OS_VERSION = ?, DEVICE_MODEL = ?, OS_BUILD_DATE = ? WHERE DEVICE_ID = ?";
			stmt = conn.prepareStatement(updateDBQuery);

			Map<String, String> properties = mobileDevice.getDeviceProperties();
			stmt.setString(1, properties.get(AndroidPluginConstants.FCM_TOKEN));
			stmt.setString(2, properties.get(AndroidPluginConstants.DEVICE_INFO));
			stmt.setString(3, mobileDevice.getSerial());
			stmt.setString(4, mobileDevice.getVendor());
			stmt.setString(5, properties.get(AndroidPluginConstants.MAC_ADDRESS));
			stmt.setString(6, properties.get(AndroidPluginConstants.DEVICE_NAME));
			stmt.setString(7, mobileDevice.getLatitude());
			stmt.setString(8, mobileDevice.getLongitude());
			stmt.setString(9, mobileDevice.getImei());
			stmt.setString(10, mobileDevice.getImsi());
			stmt.setString(11, mobileDevice.getOsVersion());
			stmt.setString(12, mobileDevice.getModel());
			stmt.setString(13, mobileDevice.getOsBuildDate());
			stmt.setString(14, mobileDevice.getMobileDeviceId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Android device " + mobileDevice.getMobileDeviceId() + " data has been" +
					          " modified.");
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while modifying the Android device '" +
			             mobileDevice.getMobileDeviceId() + "' data.";
			log.error(msg, e);
			throw new EnterpriseManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
		}
		return status;
	}

	public boolean deleteMobileDevice(String mblDeviceId)
			throws EnterpriseManagementDAOException {
		boolean status = false;
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = AndroidDAOFactory.getConnection();
			String deleteDBQuery =
					"DELETE FROM AD_DEVICE WHERE DEVICE_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, mblDeviceId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Android device " + mblDeviceId + " data has deleted" +
					          " from the Android database.");
				}
			}
		} catch (SQLException e) {
			throw new EnterpriseManagementDAOException("Error occurred while deleting android device '" +
                    mblDeviceId + "'", e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(stmt, null);
		}
		return status;
	}

	public List<MobileDevice> getAllMobileDevices() throws EnterpriseManagementDAOException {
		Connection conn;
		PreparedStatement stmt = null;
        ResultSet rs = null;
		MobileDevice mobileDevice;
		List<MobileDevice> mobileDevices = new ArrayList<MobileDevice>();
		try {
			conn = AndroidDAOFactory.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, FCM_TOKEN, DEVICE_INFO, DEVICE_MODEL, SERIAL, " +
					"VENDOR, MAC_ADDRESS, DEVICE_NAME, LATITUDE, LONGITUDE, IMEI, IMSI, OS_VERSION, OS_BUILD_DATE " +
					"FROM AD_DEVICE";
			stmt = conn.prepareStatement(selectDBQuery);
			rs = stmt.executeQuery();

			while (rs.next()) {
				mobileDevice = new MobileDevice();
				mobileDevice.setMobileDeviceId(rs.getString(AndroidPluginConstants.DEVICE_ID));
				mobileDevice.setModel(rs.getString(AndroidPluginConstants.DEVICE_MODEL));
				mobileDevice.setSerial(rs.getString(AndroidPluginConstants.SERIAL));
				mobileDevice.setVendor(rs.getString(AndroidPluginConstants.VENDOR));
				mobileDevice.setLatitude(rs.getString(AndroidPluginConstants.LATITUDE));
				mobileDevice.setLongitude(rs.getString(AndroidPluginConstants.LONGITUDE));
				mobileDevice.setImei(rs.getString(AndroidPluginConstants.IMEI));
				mobileDevice.setImsi(rs.getString(AndroidPluginConstants.IMSI));
				mobileDevice.setOsVersion(rs.getString(AndroidPluginConstants.OS_VERSION));
				mobileDevice.setOsBuildDate(rs.getString(AndroidPluginConstants.OS_BUILD_DATE));

				Map<String, String> propertyMap = new HashMap<>();
				propertyMap.put(AndroidPluginConstants.FCM_TOKEN, rs.getString(AndroidPluginConstants.FCM_TOKEN));
				propertyMap.put(AndroidPluginConstants.DEVICE_INFO, rs.getString(AndroidPluginConstants.DEVICE_INFO));
				propertyMap.put(AndroidPluginConstants.DEVICE_NAME, rs.getString(AndroidPluginConstants.DEVICE_NAME));
				mobileDevice.setDeviceProperties(propertyMap);

				mobileDevices.add(mobileDevice);
			}
			if (log.isDebugEnabled()) {
				log.debug("All Android device details have fetched from Android database.");
			}
			return mobileDevices;
		} catch (SQLException e) {
			throw new EnterpriseManagementDAOException("Error occurred while fetching all Android device data", e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(stmt, rs);
            AndroidDAOFactory.closeConnection();
		}
	}

}
