/*
 *   Copyright (c) 2020, Entgra (pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 *   Entgra (pvt) Ltd. licenses this file to you under the Apache License,
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
package io.entgra.healthcheck.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;

public class APIUtils {

    private static Log log = LogFactory.getLog(APIUtils.class);
    private static DeviceManagementProviderService deviceManagementProviderService = null;

    public static DeviceManagementProviderService getDeviceManagementService() {
        if (deviceManagementProviderService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                String msg = "DeviceImpl Management provider service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return deviceManagementProviderService;
    }
}
