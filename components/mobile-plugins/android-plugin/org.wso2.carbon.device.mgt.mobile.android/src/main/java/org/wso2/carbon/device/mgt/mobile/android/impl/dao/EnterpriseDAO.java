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

package org.wso2.carbon.device.mgt.mobile.android.impl.dao;

import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseManagedConfig;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;

import java.util.List;

/**
 * This class represents the key operations associated with working with Enterprise data
 */
public interface EnterpriseDAO {

	/**
	 * Add a new user to Enterprise.
	 *
	 * @param androidEnterpriseUser Enterprise user and device details.
	 * @return User addition status.
	 * @throws EnterpriseManagementDAOException
	 */
	boolean addUser(AndroidEnterpriseUser androidEnterpriseUser) throws EnterpriseManagementDAOException;

	List<AndroidEnterpriseUser> getUser(String username, int tenantId) throws EnterpriseManagementDAOException;

	AndroidEnterpriseManagedConfig getConfigByPackageName(String packageName, int tenantId)
			throws EnterpriseManagementDAOException;

	boolean addConfig(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseManagementDAOException;

	boolean updateConfig(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseManagementDAOException;

	boolean deleteConfig(String id, int tenantId) throws EnterpriseManagementDAOException;
}
