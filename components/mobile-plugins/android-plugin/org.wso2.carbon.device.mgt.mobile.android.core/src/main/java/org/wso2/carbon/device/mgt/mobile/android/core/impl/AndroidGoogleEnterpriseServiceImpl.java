/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
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

package org.wso2.carbon.device.mgt.mobile.android.core.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseManagedConfig;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidGoogleEnterpriseService;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.AndroidDAOFactory;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.EnterpriseDAO;

import java.util.List;

/**
 * This represents the Android implementation of DeviceManagerService.
 */
public class AndroidGoogleEnterpriseServiceImpl implements AndroidGoogleEnterpriseService {

    private static final Log log = LogFactory.getLog(AndroidGoogleEnterpriseServiceImpl.class);
    private EnterpriseDAO enterpriseDAO;

    public AndroidGoogleEnterpriseServiceImpl() {
        enterpriseDAO = AndroidDAOFactory.getEnterpriseDAO();
    }

    @Override
    public void addEnterpriseUser(AndroidEnterpriseUser androidEnterpriseUser) throws EnterpriseServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Calling add user service by device identifier: " + androidEnterpriseUser.getEmmDeviceId());
        }
        try {
            AndroidDAOFactory.beginTransaction();
            this.enterpriseDAO.addUser(androidEnterpriseUser);
            AndroidDAOFactory.commitTransaction();
        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while adding the user "
                    + CarbonContext.getThreadLocalCarbonContext().getUsername();
            log.error(msg, e);
            AndroidDAOFactory.rollbackTransaction();
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public List<AndroidEnterpriseUser> getEnterpriseUser(String username) throws EnterpriseServiceException {

        List<AndroidEnterpriseUser> androidEnterpriseUsers;
        if (log.isDebugEnabled()) {
            log.debug("Calling get user service by device identifier: " + CarbonContext
                    .getThreadLocalCarbonContext().getUsername());
        }
        try {
            AndroidDAOFactory.openConnection();
            androidEnterpriseUsers = this.enterpriseDAO.getUser(username, CarbonContext.getThreadLocalCarbonContext()
                    .getTenantId());

        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while adding the user "
                    + CarbonContext.getThreadLocalCarbonContext().getUsername();
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
        return androidEnterpriseUsers;
    }

    @Override
    public AndroidEnterpriseUser getEnterpriseUserByDevice(String deviceId) throws EnterpriseServiceException {

        AndroidEnterpriseUser androidEnterpriseUsers;
        if (log.isDebugEnabled()) {
            log.debug("Calling get user service by device identifier: " + CarbonContext
                    .getThreadLocalCarbonContext().getUsername());
        }
        try {
            AndroidDAOFactory.openConnection();
            androidEnterpriseUsers = this.enterpriseDAO.getUserByDevice(deviceId, CarbonContext.getThreadLocalCarbonContext()
                    .getTenantId());

        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while adding the user "
                    + CarbonContext.getThreadLocalCarbonContext().getUsername();
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
        return androidEnterpriseUsers;
    }

    @Override
    public AndroidEnterpriseManagedConfig getConfigByPackageName(String packageName) throws EnterpriseServiceException {
        AndroidEnterpriseManagedConfig enterpriseManagedConfig;
        if (log.isDebugEnabled()) {
            log.debug("Calling get user service by device identifier: " + CarbonContext
                    .getThreadLocalCarbonContext().getUsername());
        }
        try {
            AndroidDAOFactory.openConnection();
            enterpriseManagedConfig = this.enterpriseDAO.getConfigByPackageName(packageName, CarbonContext
                    .getThreadLocalCarbonContext().getTenantId());

        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while getting configs for the package  " + packageName;
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
        return enterpriseManagedConfig;
    }

    @Override
    public void addConfig(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseServiceException {
        if (log.isDebugEnabled()) {
            log.debug("Calling add managed config for package : " + managedConfig.getPackageName());
        }

        // Block from fetching other tenants data.
        managedConfig.setTenantID(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        try {
            AndroidDAOFactory.beginTransaction();
            this.enterpriseDAO.addConfig(managedConfig);
            AndroidDAOFactory.commitTransaction();
        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while adding managed configs for package " + managedConfig.getPackageName();
            log.error(msg, e);
            AndroidDAOFactory.rollbackTransaction();
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
    }

    @Override
    public boolean updateMobileDevice(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseServiceException {
        boolean status;
        if (log.isDebugEnabled()) {
            log.debug("Calling update managed config for mcm id : " + managedConfig.getMcmId());
        }

        // Block from fetching other tenants data.
        managedConfig.setTenantID(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        try {
            AndroidDAOFactory.beginTransaction();
            status = this.enterpriseDAO.updateConfig(managedConfig);
            AndroidDAOFactory.commitTransaction();
        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while updating managed configs for mcm id " + managedConfig.getMcmId();
            log.error(msg, e);
            AndroidDAOFactory.rollbackTransaction();
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
        return status;
    }

    @Override
    public boolean deleteMobileDevice(String id) throws EnterpriseServiceException {
        boolean status;
        if (log.isDebugEnabled()) {
            log.debug("Calling update managed config for mcm id : " + id);
        }

        try {
            AndroidDAOFactory.beginTransaction();
            status = this.enterpriseDAO.deleteConfig(id,CarbonContext.getThreadLocalCarbonContext().getTenantId());
            AndroidDAOFactory.commitTransaction();
        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while updating managed configs for mcm id " + id;
            log.error(msg, e);
            AndroidDAOFactory.rollbackTransaction();
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
        return status;
    }

    @Override
    public List<AndroidEnterpriseUser> getAllEnterpriseDevices(String enterpriseId)
            throws EnterpriseServiceException {

        List<AndroidEnterpriseUser> androidEnterpriseUsers;
        if (log.isDebugEnabled()) {
            log.debug("Calling get enterprise device service by enterprise identifier: " + enterpriseId);
        }
        try {
            AndroidDAOFactory.openConnection();
            androidEnterpriseUsers = this.enterpriseDAO.getAllEnterpriseDevices(CarbonContext
                    .getThreadLocalCarbonContext()
                    .getTenantId(), enterpriseId);

        } catch (EnterpriseManagementDAOException e) {
            String msg = "Error occurred while adding the user "
                    + CarbonContext.getThreadLocalCarbonContext().getUsername();
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        } finally {
            AndroidDAOFactory.closeConnection();
        }
        return androidEnterpriseUsers;
    }
}
