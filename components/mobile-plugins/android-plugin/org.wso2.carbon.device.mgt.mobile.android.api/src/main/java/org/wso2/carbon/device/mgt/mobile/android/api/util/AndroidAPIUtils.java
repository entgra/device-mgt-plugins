/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.android.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.application.mgt.common.services.ApplicationManager;
import org.wso2.carbon.device.application.mgt.common.services.SubscriptionManager;
import org.wso2.carbon.device.mgt.common.spi.DeviceTypeCommonService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidGoogleEnterpriseService;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

/**
 * AndroidAPIUtil class provides utility functions used by Android REST-API classes.
 */
public class AndroidAPIUtils {

    private static Log log = LogFactory.getLog(AndroidAPIUtils.class);

    private static ApplicationManager applicationManager = null;
    private static SubscriptionManager subscriptionManager = null;
    private static DeviceManagementProviderService deviceManagementProviderService = null;
    private static AndroidGoogleEnterpriseService androidGoogleEnterpriseService = null;
    private static AndroidService androidService = null;
    private static PolicyManagerService policyManagerService = null;
    private static DeviceTypeCommonService deviceTypeCommonService = null;

    private AndroidAPIUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ApplicationManager getAppManagerService() {
        if (applicationManager == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            applicationManager = (ApplicationManager) ctx.getOSGiService(ApplicationManager.class, null);
            if (applicationManager == null) {
                String msg = "Application Management service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return applicationManager;
    }

    public static SubscriptionManager getAppSubscriptionService() {
        if (subscriptionManager == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            subscriptionManager = (SubscriptionManager) ctx.getOSGiService(SubscriptionManager.class, null);
            if (subscriptionManager == null) {
                String msg = "Application Subscription service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return subscriptionManager;
    }

    public static AndroidGoogleEnterpriseService getAndroidPluginService() {
        if (androidGoogleEnterpriseService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            androidGoogleEnterpriseService =
                    (AndroidGoogleEnterpriseService) ctx.getOSGiService(AndroidGoogleEnterpriseService.class, null);
            if (androidGoogleEnterpriseService == null) {
                String msg = "Android plugin service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return androidGoogleEnterpriseService;
    }

    public static AndroidService getAndroidService() {
        if (androidService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            androidService = (AndroidService) ctx.getOSGiService(AndroidService.class, null);
            if (androidService == null) {
                String msg = "Android service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return androidService;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Device Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }

    public static DeviceTypeCommonService getDeviceTypeCommonService() {
        if (deviceTypeCommonService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            deviceTypeCommonService = (DeviceTypeCommonService) ctx
                    .getOSGiService(DeviceTypeCommonService.class, null);
            if (deviceTypeCommonService == null) {
                String msg = "Device Type Common service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return deviceTypeCommonService;
    }

    public static DeviceInformationManager getDeviceInformationManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager informationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);
        if (informationManager == null) {
            String msg = "Information Manager service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return informationManager;
    }

    public static PolicyManagerService getPolicyManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                PolicyManagerService.class, null);
        if (policyManagerService == null) {
            String msg = "Policy Manager service has not initialized";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return policyManagerService;
    }

}
