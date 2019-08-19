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

package org.wso2.carbon.device.mgt.mobile.android.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.mobile.android.AndroidPluginService;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.AndroidDAOFactory;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.EnterpriseDAO;
import org.wso2.carbon.device.mgt.mobile.android.impl.dao.EnterpriseManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;

import java.util.List;

/**
 * This represents the Android implementation of DeviceManagerService.
 */
public class AndroidPluginServiceImpl implements AndroidPluginService {

    private static final Log log = LogFactory.getLog(AndroidPluginServiceImpl.class);
    private EnterpriseDAO enterpriseDAO;

    public AndroidPluginServiceImpl() {
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
}
