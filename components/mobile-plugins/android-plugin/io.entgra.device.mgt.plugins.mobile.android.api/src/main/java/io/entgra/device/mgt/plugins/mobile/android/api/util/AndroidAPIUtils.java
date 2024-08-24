/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.plugins.mobile.android.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationManagementService;
import io.entgra.device.mgt.core.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import io.entgra.device.mgt.core.device.mgt.core.device.details.mgt.DeviceInformationManager;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.policy.mgt.core.PolicyManagerService;

/**
 * AndroidAPIUtil class provides utility functions used by Android REST-API classes.
 */
public class AndroidAPIUtils {

    private static Log log = LogFactory.getLog(AndroidAPIUtils.class);

    private static DeviceManagementProviderService deviceManagementProviderService = null;
    private static DeviceInformationManager informationManager = null;
    private static PolicyManagerService policyManagerService = null;
    private static ApplicationManagementProviderService applicationManagementProviderService = null;
    private static NotificationManagementService notificationManagementService = null;
//    private static EventsPublisherService eventsPublisherService = null;
//    private static AnalyticsDataAPI analyticsDataAPI = null;

    private AndroidAPIUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getAuthenticatedUser() {
        PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = threadLocalCarbonContext.getUsername();
        String tenantDomain = threadLocalCarbonContext.getTenantDomain();
        if (username != null && username.endsWith(tenantDomain)) {
            return username.substring(0, username.lastIndexOf("@"));
        }
        return username;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        if (deviceManagementProviderService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                String msg = "Device Management service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return deviceManagementProviderService;
    }

    public static DeviceInformationManager getDeviceInformationManagerService() {
        if (informationManager == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            informationManager = (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);
            if (informationManager == null) {
                String msg = "Information Manager service not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return informationManager;
    }

    public static PolicyManagerService getPolicyManagerService() {
        if (policyManagerService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            policyManagerService = (PolicyManagerService) ctx.getOSGiService(PolicyManagerService.class, null);
            if (policyManagerService == null) {
                String msg = "Policy Manager service has not initialized";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return policyManagerService;
    }

    public static ApplicationManagementProviderService getApplicationManagerService() {
        if (applicationManagementProviderService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            applicationManagementProviderService =
                    (ApplicationManagementProviderService) ctx.getOSGiService(ApplicationManagementProviderService.class, null);
            if (applicationManagementProviderService == null) {
                String msg = "Application Management provider service has not initialized";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return applicationManagementProviderService;
    }

    public static NotificationManagementService getNotificationManagementService() {
        if (notificationManagementService == null) {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            notificationManagementService = (NotificationManagementService) ctx.getOSGiService(
                    NotificationManagementService.class, null);
            if (notificationManagementService == null) {
                String msg = "Notification Management service not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return notificationManagementService;
    }

//    public static EventsPublisherService getEventPublisherService() {
//        if (eventsPublisherService == null) {
//            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
//            eventsPublisherService = (EventsPublisherService) ctx.getOSGiService(EventsPublisherService.class, null);
//            if (eventsPublisherService == null) {
//                String msg = "Event Publisher service has not initialized.";
//                log.error(msg);
//                throw new IllegalStateException(msg);
//            }
//        }
//        return eventsPublisherService;
//    }

//    public static AnalyticsDataAPI getAnalyticsDataAPI() {
//        if (analyticsDataAPI == null) {
//            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
//            analyticsDataAPI = (AnalyticsDataAPI) ctx.getOSGiService(AnalyticsDataAPI.class, null);
//            if (analyticsDataAPI == null) {
//                String msg = "Analytics api service has not initialized.";
//                log.error(msg);
//                throw new IllegalStateException(msg);
//            }
//        }
//        return analyticsDataAPI;
//    }

}
