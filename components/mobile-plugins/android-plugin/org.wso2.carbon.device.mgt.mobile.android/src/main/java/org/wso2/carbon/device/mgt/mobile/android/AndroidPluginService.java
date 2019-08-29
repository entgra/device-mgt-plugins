/*
 *   Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.carbon.device.mgt.mobile.android;

import org.wso2.carbon.device.mgt.mobile.android.impl.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseManagedConfig;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;

import java.util.List;

public interface AndroidPluginService {

    void addEnterpriseUser(AndroidEnterpriseUser androidEnterpriseUser) throws EnterpriseServiceException;

    List<AndroidEnterpriseUser> getEnterpriseUser(String username) throws EnterpriseServiceException;

    AndroidEnterpriseManagedConfig getConfigByPackageName(String packageName) throws EnterpriseServiceException;

    void addConfig(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseServiceException;

    boolean updateMobileDevice(AndroidEnterpriseManagedConfig managedConfig) throws EnterpriseServiceException;

    boolean deleteMobileDevice(String id) throws EnterpriseServiceException;
}
