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

package io.entgra.device.mgt.plugins.virtualfirealarm.api.service.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.core.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import io.entgra.device.mgt.core.device.mgt.common.authorization.DeviceAccessAuthorizationService;
import io.entgra.device.mgt.core.device.mgt.common.configuration.mgt.PlatformConfigurationManagementService;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.identity.jwt.client.extension.service.JWTClientManagerService;

/**
 * This class provides utility functions used by REST-API.
 */
public class APIUtil {

	private static Log log = LogFactory.getLog(APIUtil.class);

	public static String getAuthenticatedUser() {
		PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		String username = threadLocalCarbonContext.getUsername();
		String tenantDomain = threadLocalCarbonContext.getTenantDomain();
		if (username.endsWith(tenantDomain)) {
			return username.substring(0, username.lastIndexOf("@"));
		}
		return username;
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

	public static APIManagementProviderService getAPIManagementProviderService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		APIManagementProviderService apiManagementProviderService =
				(APIManagementProviderService) ctx.getOSGiService(APIManagementProviderService.class, null);
		if (apiManagementProviderService == null) {
			String msg = "API management provider service has not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return apiManagementProviderService;
	}

	public static JWTClientManagerService getJWTClientManagerService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		JWTClientManagerService jwtClientManagerService =
				(JWTClientManagerService) ctx.getOSGiService(JWTClientManagerService.class, null);
		if (jwtClientManagerService == null) {
			String msg = "JWT Client manager service has not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return jwtClientManagerService;
	}

	public static String getTenantDomainOftheUser() {
		PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		return threadLocalCarbonContext.getTenantDomain();
	}

	public static DeviceAccessAuthorizationService getDeviceAccessAuthorizationService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		DeviceAccessAuthorizationService deviceAccessAuthorizationService =
				(DeviceAccessAuthorizationService) ctx.getOSGiService(DeviceAccessAuthorizationService.class, null);
		if (deviceAccessAuthorizationService == null) {
			String msg = "Device Authorization service has not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return deviceAccessAuthorizationService;
	}

	public static PlatformConfigurationManagementService getTenantConfigurationManagementService() {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		PlatformConfigurationManagementService tenantConfigurationManagementService =
				(PlatformConfigurationManagementService) ctx.getOSGiService(PlatformConfigurationManagementService.class, null);
		if (tenantConfigurationManagementService == null) {
			String msg = "Tenant configuration Management service not initialized.";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return tenantConfigurationManagementService;
	}
	
}
