/* Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.android.core.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.AppRegistrationCredentials;
import org.wso2.carbon.device.mgt.common.ApplicationRegistrationException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidConfigurationException;
import org.wso2.carbon.device.mgt.common.spi.DeviceTypeCommonService;
import org.wso2.carbon.device.mgt.core.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.core.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.core.config.keymanager.KeyManagerConfigurations;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeviceTypeCommonServiceImpl implements DeviceTypeCommonService {

    private static final Log log = LogFactory.getLog(DeviceTypeCommonServiceImpl.class);

    @Override
    public Map<String, Object> getEnrollmentQRCode(String ownershipType) throws DeviceManagementException {
        AndroidService androidService = AndroidAPIUtils.getAndroidService();
        PlatformConfiguration platformConfiguration = androidService.getPlatformConfig();

        Map<String, Object> qrEnrollmentPayload = new HashMap<>();
        Map<String, Object> defaultQREnrollmentPayload = new HashMap<>();

        if (Arrays.stream(EnrolmentInfo.OwnerShip.values())
                .noneMatch(ownerShip -> ownerShip.toString().equalsIgnoreCase(ownershipType))) {
            String msg = "Request to get QR enrollment code for invalid device ownership type " + ownershipType;
            log.error(msg);
            throw new BadRequestException(msg);
        }

        String accessToken = getAccessTokenToEnroll();
        if (StringUtils.isBlank(accessToken)) {
            String msg = "Couldn't get a access token for user " + PrivilegedCarbonContext.getThreadLocalCarbonContext()
                    .getUsername();
            log.error(msg);
            throw new DeviceManagementException(msg);
        }

        for (ConfigurationEntry configEntry : platformConfiguration.getConfiguration()) {
            if (AndroidConstants.PlatformConfigs.DefaultConfigs.SERVER_IP.equals(configEntry.getName())) {
                defaultQREnrollmentPayload
                        .put(AndroidConstants.PlatformConfigs.DefaultConfigs.SERVER_IP, configEntry.getValue());
            }

            if (EnrolmentInfo.OwnerShip.COSU.toString().equalsIgnoreCase(ownershipType) || EnrolmentInfo.OwnerShip.COPE
                    .toString().equalsIgnoreCase(ownershipType)) {
                if (AndroidConstants.PlatformConfigs.KioskConfigs.ADMIN_COMPONENT_NAME.equals(configEntry.getName())) {
                    qrEnrollmentPayload.put(AndroidConstants.PlatformConfigs.KioskConfigs.ADMIN_COMPONENT_NAME,
                            configEntry.getValue());
                } else if (AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SSID.equals(configEntry.getName())) {
                    qrEnrollmentPayload
                            .put(AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SSID, configEntry.getValue());
                } else if (AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_PASSWORD.equals(configEntry.getName())) {
                    qrEnrollmentPayload
                            .put(AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_PASSWORD, configEntry.getValue());
                } else if (AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SECURITY.equals(configEntry.getName())) {
                    qrEnrollmentPayload
                            .put(AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SECURITY, configEntry.getValue());
                } else if (AndroidConstants.PlatformConfigs.KioskConfigs.CHECKSUM.equals(configEntry.getName())) {
                    qrEnrollmentPayload
                            .put(AndroidConstants.PlatformConfigs.KioskConfigs.CHECKSUM, configEntry.getValue());
                } else if (AndroidConstants.PlatformConfigs.KioskConfigs.DOWNLOAD_URL.equals(configEntry.getName())) {
                    qrEnrollmentPayload
                            .put(AndroidConstants.PlatformConfigs.KioskConfigs.DOWNLOAD_URL, configEntry.getValue());
                } else if (AndroidConstants.PlatformConfigs.KioskConfigs.SKIP_ENCRYPTION
                        .equals(configEntry.getName())) {
                    qrEnrollmentPayload
                            .put(AndroidConstants.PlatformConfigs.KioskConfigs.SKIP_ENCRYPTION, configEntry.getValue());
                }
            }

        }

        defaultQREnrollmentPayload
                .put(AndroidConstants.PlatformConfigs.DefaultConfigs.DEFAULT_OWNERSHIP, ownershipType.toUpperCase());
        defaultQREnrollmentPayload.put(AndroidConstants.PlatformConfigs.DefaultConfigs.ACCESS_TOKEN, accessToken);
        qrEnrollmentPayload
                .put(AndroidConstants.PlatformConfigs.KioskConfigs.ANDROID_EXTRA, defaultQREnrollmentPayload);
        validateQREnrollmentPayload(qrEnrollmentPayload, ownershipType);
        return qrEnrollmentPayload;
    }

    /**
     * To get Access token for device enroll scope.
     *
     * @return Access token
     * @throws DeviceManagementException if error occurred when trying to get access token for device enroll scope.
     */
    private String getAccessTokenToEnroll() throws DeviceManagementException {
        DeviceManagementConfig deviceManagementConfig = DeviceConfigurationManager.getInstance()
                .getDeviceManagementConfig();
        KeyManagerConfigurations kmConfig = deviceManagementConfig.getKeyManagerConfigurations();
        try {
            AppRegistrationCredentials credentials = DeviceManagerUtil.getApplicationRegistrationCredentials(
                    System.getProperty(
                            org.wso2.carbon.device.mgt.core.DeviceManagementConstants.ConfigurationManagement.IOT_GATEWAY_HOST),
                    System.getProperty(
                            org.wso2.carbon.device.mgt.core.DeviceManagementConstants.ConfigurationManagement.IOT_GATEWAY_HTTPS_PORT),
                    kmConfig.getAdminUsername(), kmConfig.getAdminPassword());

            AccessTokenInfo accessTokenForAdmin = DeviceManagerUtil
                    .getAccessTokenForDeviceOwner("perm:device:enroll", credentials.getClient_id(),
                            credentials.getClient_secret(),
                            PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            return accessTokenForAdmin.getAccessToken();
        } catch (ApplicationRegistrationException e) {
            String msg = "Error occurred while registering Application to get access token to create enrollment QR code "
                    + "payload.";
            log.error(msg);
            throw new DeviceManagementException(msg, e);
        } catch (JWTClientException e) {
            String msg = "JWT Error occurred while registering Application to get access token to create enrollment "
                    + "QR code payload.";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
    }

    /**
     * Validate the QR enrollment payload
     *
     * @param payload Configs use to generate Enrollment QR code
     * @param ownershipType Device enrollment type
     */
    private void validateQREnrollmentPayload(Map<String, Object> payload,
            String ownershipType) {
        boolean isInvalidConfig = false;
        String invalidPlatformConfigs = "";

        if (payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.ANDROID_EXTRA) == null) {
            invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.ANDROID_EXTRA + ", ";
            isInvalidConfig = true;
        } else {
            Map<String, Object> defaultPayload = (Map<String, Object>) payload
                    .get(AndroidConstants.PlatformConfigs.KioskConfigs.ANDROID_EXTRA);
            if (StringUtils.isBlank(
                    (String) defaultPayload.get(AndroidConstants.PlatformConfigs.DefaultConfigs.DEFAULT_OWNERSHIP))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.DefaultConfigs.DEFAULT_OWNERSHIP + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils
                    .isBlank((String) defaultPayload.get(AndroidConstants.PlatformConfigs.DefaultConfigs.SERVER_IP))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.DefaultConfigs.SERVER_IP + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils.isBlank(
                    (String) defaultPayload.get(AndroidConstants.PlatformConfigs.DefaultConfigs.ACCESS_TOKEN))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.DefaultConfigs.ACCESS_TOKEN + ", ";
                isInvalidConfig = true;
            }
        }

        if (EnrolmentInfo.OwnerShip.COSU.toString().equalsIgnoreCase(ownershipType) || EnrolmentInfo.OwnerShip.COPE
                .toString().equalsIgnoreCase(ownershipType)) {
            if (StringUtils.isBlank(
                    (String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.ADMIN_COMPONENT_NAME))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.ADMIN_COMPONENT_NAME + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils.isBlank((String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SSID))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SSID + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils
                    .isBlank((String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_PASSWORD))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_PASSWORD + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils
                    .isBlank((String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SECURITY))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.WIFI_SECURITY + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils.isBlank((String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.CHECKSUM))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.CHECKSUM + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils.isBlank((String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.DOWNLOAD_URL))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.DOWNLOAD_URL + ", ";
                isInvalidConfig = true;
            }
            if (StringUtils
                    .isBlank((String) payload.get(AndroidConstants.PlatformConfigs.KioskConfigs.SKIP_ENCRYPTION))) {
                invalidPlatformConfigs += AndroidConstants.PlatformConfigs.KioskConfigs.SKIP_ENCRYPTION + ", ";
                isInvalidConfig = true;
            }
        }

        if (isInvalidConfig) {
            String msg = "Android Platform Configuration is not configured properly. Platform configs [ "
                    + invalidPlatformConfigs + " ]";
            log.error(msg);
            throw new InvalidConfigurationException(msg);
        }
    }
}
