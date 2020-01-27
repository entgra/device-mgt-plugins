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

package org.wso2.carbon.device.mgt.mobile.android.core.internal;

import org.wso2.carbon.device.mgt.common.spi.DeviceTypeCommonService;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidGoogleEnterpriseService;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.registry.core.service.RegistryService;

/**
 * DataHolder class of Mobile plugins component.
 */
public class AndroidDeviceManagementDataHolder {

	private RegistryService registryService;
    private AndroidGoogleEnterpriseService androidDeviceManagementService;
    private AndroidService androidService;

	private DeviceTypeCommonService deviceTypeCommonService;

	private static AndroidDeviceManagementDataHolder thisInstance = new AndroidDeviceManagementDataHolder();

	private AndroidDeviceManagementDataHolder() {
	}

	public static AndroidDeviceManagementDataHolder getInstance() {
		return thisInstance;
	}

	public RegistryService getRegistryService() {
		return registryService;
	}

	public void setRegistryService(RegistryService registryService) {
		this.registryService = registryService;
	}

    public AndroidGoogleEnterpriseService getAndroidDeviceManagementService() {
        return androidDeviceManagementService;
    }

    public void setAndroidDeviceManagementService(
			AndroidGoogleEnterpriseService androidDeviceManagementService) {
        this.androidDeviceManagementService = androidDeviceManagementService;
    }

	public AndroidService getAndroidService() {
		return androidService;
	}

	public void setAndroidService(AndroidService androidService) {
		this.androidService = androidService;
	}

	public DeviceTypeCommonService getDeviceTypeCommonService() {
		return deviceTypeCommonService;
	}

	public void setDeviceTypeCommonService(DeviceTypeCommonService deviceTypeCommonService) {
		this.deviceTypeCommonService = deviceTypeCommonService;
	}
}
