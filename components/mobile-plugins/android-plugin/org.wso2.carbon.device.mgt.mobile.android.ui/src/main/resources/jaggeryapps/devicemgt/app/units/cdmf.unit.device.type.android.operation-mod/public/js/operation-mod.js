/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 * Copyright (c) 2018, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

var androidOperationModule = function () {
    var publicMethods = {};
    var privateMethods = {};

    // Constants to define operation types available
    var operationTypeConstants = {
        "PROFILE": "profile",
        "CONFIG": "config",
        "COMMAND": "command"
    };

    // Constants to define Android Operation Constants
    var androidOperationConstants = {
        "PASSCODE_POLICY_OPERATION_CODE": "PASSCODE_POLICY",
        "VPN_OPERATION_CODE": "VPN",
        "APN_OPERATION_CODE": "APN",
        "CAMERA_OPERATION_CODE": "CAMERA",
        "BACKUP_SERVICE_CODE": "BACKUP_SERVICE",
        "ENCRYPT_STORAGE_OPERATION_CODE": "ENCRYPT_STORAGE",
        "WIFI_OPERATION_CODE": "WIFI",
        "GLOBAL_PROXY_OPERATION_CODE": "GLOBAL_PROXY",
        "WIPE_OPERATION_CODE": "WIPE_DATA",
        "NOTIFICATION_OPERATION_CODE": "NOTIFICATION",
        "WORK_PROFILE_CODE": "WORK_PROFILE",
        "CHANGE_LOCK_CODE_OPERATION_CODE": "CHANGE_LOCK_CODE",
        "LOCK_OPERATION_CODE": "DEVICE_LOCK",
        "UPGRADE_FIRMWARE": "UPGRADE_FIRMWARE",
        "DISALLOW_ADJUST_VOLUME": "DISALLOW_ADJUST_VOLUME",
        "DISALLOW_CONFIG_BLUETOOTH": "DISALLOW_CONFIG_BLUETOOTH",
        "DISALLOW_CONFIG_CELL_BROADCASTS": "DISALLOW_CONFIG_CELL_BROADCASTS",
        "DISALLOW_CONFIG_CREDENTIALS": "DISALLOW_CONFIG_CREDENTIALS",
        "DISALLOW_CONFIG_MOBILE_NETWORKS": "DISALLOW_CONFIG_MOBILE_NETWORKS",
        "DISALLOW_CONFIG_TETHERING": "DISALLOW_CONFIG_TETHERING",
        "DISALLOW_CONFIG_VPN": "DISALLOW_CONFIG_VPN",
        "DISALLOW_CONFIG_APN": "DISALLOW_CONFIG_APN",
        "DISALLOW_CONFIG_WIFI": "DISALLOW_CONFIG_WIFI",
        "DISALLOW_APPS_CONTROL": "DISALLOW_APPS_CONTROL",
        "DISALLOW_CREATE_WINDOWS": "DISALLOW_CREATE_WINDOWS",
        "DISALLOW_CROSS_PROFILE_COPY_PASTE": "DISALLOW_CROSS_PROFILE_COPY_PASTE",
        "DISALLOW_DEBUGGING_FEATURES": "DISALLOW_DEBUGGING_FEATURES",
        "DISALLOW_FACTORY_RESET": "DISALLOW_FACTORY_RESET",
        "DISALLOW_ADD_USER": "DISALLOW_ADD_USER",
        "DISALLOW_INSTALL_APPS": "DISALLOW_INSTALL_APPS",
        "DISALLOW_INSTALL_UNKNOWN_SOURCES": "DISALLOW_INSTALL_UNKNOWN_SOURCES",
        "DISALLOW_MODIFY_ACCOUNTS": "DISALLOW_MODIFY_ACCOUNTS",
        "DISALLOW_MOUNT_PHYSICAL_MEDIA": "DISALLOW_MOUNT_PHYSICAL_MEDIA",
        "DISALLOW_NETWORK_RESET": "DISALLOW_NETWORK_RESET",
        "DISALLOW_OUTGOING_BEAM": "DISALLOW_OUTGOING_BEAM",
        "DISALLOW_OUTGOING_CALLS": "DISALLOW_OUTGOING_CALLS",
        "DISALLOW_REMOVE_USER": "DISALLOW_REMOVE_USER",
        "DISALLOW_SAFE_BOOT": "DISALLOW_SAFE_BOOT",
        "DISALLOW_SHARE_LOCATION": "DISALLOW_SHARE_LOCATION",
        "DISALLOW_SMS": "DISALLOW_SMS",
        "DISALLOW_UNINSTALL_APPS": "DISALLOW_UNINSTALL_APPS",
        "DISALLOW_UNMUTE_MICROPHONE": "DISALLOW_UNMUTE_MICROPHONE",
        "DISALLOW_USB_FILE_TRANSFER": "DISALLOW_USB_FILE_TRANSFER",
        "ALLOW_PARENT_PROFILE_APP_LINKING": "ALLOW_PARENT_PROFILE_APP_LINKING",
        "ENSURE_VERIFY_APPS": "ENSURE_VERIFY_APPS",
        "AUTO_TIME": "AUTO_TIME",
        "SET_SCREEN_CAPTURE_DISABLED": "SET_SCREEN_CAPTURE_DISABLED",
        "SET_STATUS_BAR_DISABLED": "SET_STATUS_BAR_DISABLED",
        "APPLICATION_OPERATION_CODE": "APP-RESTRICTION",
        "SYSTEM_UPDATE_POLICY_CODE": "SYSTEM_UPDATE_POLICY",
        "KIOSK_APPS_CODE": "KIOSK_APPS",
        "RUNTIME_PERMISSION_POLICY_OPERATION": "runtime-permission-policy",
        "RUNTIME_PERMISSION_POLICY_OPERATION_CODE": "RUNTIME_PERMISSION_POLICY",
        "COSU_PROFILE_CONFIGURATION_OPERATION": "cosu-profile-configuration",
        "COSU_PROFILE_CONFIGURATION_OPERATION_CODE": "COSU_PROFILE",
        "ENROLLMENT_APP_INSTALL": "enrollment-app-install",
        "ENROLLMENT_APP_INSTALL_CODE": "ENROLLMENT_APP_INSTALL",
        "DISALLOW_SET_WALLPAPER": "DISALLOW_SET_WALLPAPER",
        "DISALLOW_SET_USER_ICON": "DISALLOW_SET_USER_ICON",
        "DISALLOW_REMOVE_MANAGEMENT_PROFILE": "DISALLOW_REMOVE_MANAGEMENT_PROFILE",
        "DISALLOW_AUTOFILL": "DISALLOW_AUTOFILL",
        "DISALLOW_BLUETOOTH": "DISALLOW_BLUETOOTH",
        "DISALLOW_BLUETOOTH_SHARING": "DISALLOW_BLUETOOTH_SHARING",
        "DISALLOW_REMOVE_USER": "DISALLOW_REMOVE_USER",
        "DISALLOW_DATA_ROAMING": "DISALLOW_DATA_ROAMING",
        "CERT_ADD_OPERATION_CODE": "INSTALL_CERT",
        "DISPLAY_MESSAGE_CONFIGURATION_OPERATION_CODE": "DISPLAY_MESSAGE_CONFIGURATION"
    };

    /**
     * Convert the android platform specific code to the generic payload.
     * @param operationCode
     * @param operationPayload
     * @returns {{}}
     */
    privateMethods.generateGenericPayloadFromAndroidPayload = function (operationCode, operationPayload) {
        var payload = {};
        operationPayload = JSON.parse(operationPayload);
        switch (operationCode) {
            case androidOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                payload = {
                    "passcodePolicyAllowSimple": operationPayload["allowSimple"],
                    "passcodePolicyRequireAlphanumeric": operationPayload["requireAlphanumeric"],
                    "passcodePolicyMinLength": operationPayload["minLength"],
                    "passcodePolicyMinComplexChars": operationPayload["minComplexChars"],
                    "passcodePolicyMaxPasscodeAgeInDays": operationPayload["maxPINAgeInDays"],
                    "passcodePolicyPasscodeHistory": operationPayload["pinHistory"],
                    "passcodePolicyMaxFailedAttempts": operationPayload["maxFailedAttempts"]
                };
                if (operationPayload["passcodePolicyWPExist"] === true) {
                    payload["passcodePolicyWPExist"] = operationPayload["passcodePolicyWPExist"];
                    payload["passcodePolicyAllowSimpleWP"] = operationPayload.workProfilePasscode["passcodePolicyAllowSimpleWP"];
                    payload["passcodePolicyRequireAlphanumericWP"] = operationPayload.workProfilePasscode["passcodePolicyRequireAlphanumericWP"];
                    payload["passcodePolicyMinLengthWP"] = operationPayload.workProfilePasscode["passcodePolicyMinLengthWP"];
                    payload["passcodePolicyMinComplexCharsWP"] = operationPayload.workProfilePasscode["passcodePolicyMinComplexCharsWP"];
                    payload["passcodePolicyMaxPasscodeAgeInDaysWP"] = operationPayload.workProfilePasscode["passcodePolicyMaxPasscodeAgeInDaysWP"];
                    payload["passcodePolicyPasscodeHistoryWP"] = operationPayload.workProfilePasscode["passcodePolicyPasscodeHistoryWP"];
                    payload["passcodePolicyMaxFailedAttemptsWP"] = operationPayload.workProfilePasscode["passcodePolicyMaxFailedAttemptsWP"];
                } else {
                    payload["passcodePolicyWPExist"] = operationPayload["passcodePolicyWPExist"];
                }
                break;
            case androidOperationConstants["CAMERA_OPERATION_CODE"]:
                payload = operationPayload;
                break;
            case androidOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"]:
                payload = {
                    "encryptStorageEnabled": operationPayload["encrypted"]
                };
                break;
            case androidOperationConstants["WORK_PROFILE_CODE"]:
                payload = {
                    "workProfilePolicyProfileName": operationPayload["profileName"],
                    "workProfilePolicyEnableSystemApps": operationPayload["enableSystemApps"],
                    "workProfilePolicyHideSystemApps": operationPayload["hideSystemApps"],
                    "workProfilePolicyUnhideSystemApps": operationPayload["unhideSystemApps"],
                    "workProfilePolicyEnablePlaystoreApps": operationPayload["enablePlaystoreApps"]
                };
                break;
            case androidOperationConstants["WIFI_OPERATION_CODE"]:
                payload = {
                    "wifiSSID": operationPayload["ssid"],
                    "wifiPassword": operationPayload["password"],
                    "wifiForceConnectEnabled":operationPayload["forceConnectEnabled"],
                    "wifiType": operationPayload["type"],
                    "wifiEAP": operationPayload["eap"],
                    "wifiPhase2": operationPayload["phase2"],
                    "wifiProvisioning": operationPayload["provisioning"],
                    "wifiIdentity": operationPayload["identity"],
                    "wifiAnoIdentity": operationPayload["anonymousIdentity"],
                    "wifiCaCert": operationPayload["cacert"],
                    "wifiCaCertName": operationPayload["cacertName"]
                };
                break;
            case androidOperationConstants["APN_OPERATION_CODE"]:
                payload = {
                    "apnName": operationPayload["name"],
                    "apnApn": operationPayload["apn"],
                    "apnProxyAddress": operationPayload["proxyAddress"],
                    "apnProxyPort": operationPayload["proxyPort"],
                    "apnUsername": operationPayload["username"],
                    "apnPassword": operationPayload["password"],
                    "apnServer": operationPayload["server"],
                    "apnMMSC": operationPayload["mMSC"],
                    "apnMMSProxyAddress": operationPayload["mmsProxyAddress"],
                    "apnMMSProxyPort": operationPayload["mMSProxyPort"],
                    "apnMCC": operationPayload["mCC"],
                    "apnMNC": operationPayload["mNC"],
                    "apnAuthenticationType": operationPayload["authenticationType"],
                    "apnType": operationPayload["type"],
                    "apnProtocol": operationPayload["protocol"],
                    "apnRoamingProtocol": operationPayload["roamingProtocol"],
                    "apnIsEnable": operationPayload["isEnable"],
                    "apnBearer": operationPayload["bearer"],
                    "apnMVNOType": operationPayload["mVNOType"],
                    "apnMVNOValue": operationPayload["mVNOValue"]
                };
                break;
            case androidOperationConstants["GLOBAL_PROXY_OPERATION_CODE"]:
                payload = {
                    "proxyConfigType": operationPayload["proxyConfigType"],
                    "proxyHost": operationPayload["proxyHost"],
                    "proxyPort": operationPayload["proxyPort"],
                    "proxyExclList": operationPayload["proxyExclList"],
                    "proxyPacUrl": operationPayload["proxyPacUrl"]
                };
                break;
            case androidOperationConstants["VPN_OPERATION_CODE"]:
                payload = {
                    "type": operationPayload["type"],
                    "openvpn_config": operationPayload["openvpn_config"],
                    "packageName": operationPayload["packageName"]
                };
                break;
            case androidOperationConstants["APPLICATION_OPERATION_CODE"]:
                payload = {
                    "restrictionType": operationPayload["restriction-type"],
                    "restrictedApplications": operationPayload["restricted-applications"]
                };
                break;
            case androidOperationConstants["SYSTEM_UPDATE_POLICY_CODE"]:
                if (operationPayload["type"] != "window") {
                    payload = {
                        "cosuSystemUpdatePolicyType": operationPayload["type"]
                    };
                } else {
                    payload = {
                        "cosuSystemUpdatePolicyType": operationPayload["type"],
                        "cosuSystemUpdatePolicyWindowStartTime": operationPayload["startTime"],
                        "cosuSystemUpdatePolicyWindowEndTime": operationPayload["endTime"]
                    };
                }
                break;
            case androidOperationConstants["RUNTIME_PERMISSION_POLICY_OPERATION_CODE"]:
                 payload = {
                        "defaultType": operationPayload["defaultPermissionType"],
                        "permittedApplications": operationPayload["permittedApplications"]
                 };
                 break;
            case androidOperationConstants["COSU_PROFILE_CONFIGURATION_OPERATION_CODE"]:
                payload = {};
                payload["isDeviceRestrictOperationTimeEnabled"] =
                    operationPayload["isDeviceRestrictOperationTimeEnabled"];
                payload["isDeviceGlobalConfigEnabled"] = operationPayload["isDeviceGlobalConfigEnabled"];
                if (payload["isDeviceRestrictOperationTimeEnabled"] === true) {
                    payload["cosuProfileRestrictionStartTime"] = operationPayload["cosuProfileRestrictionStartTime"];
                    payload["cosuProfileRestrictionEndTime"] = operationPayload["cosuProfileRestrictionEndTime"];
                }
                if (payload["isDeviceGlobalConfigEnabled"] === true) {
                    var deviceGlobalConfigurations = operationPayload["deviceGlobalConfigurations"];
                    payload["idleMediaURL"] = deviceGlobalConfigurations["idleMediaURL"];
                    payload["kioskBackgroundImage"] = deviceGlobalConfigurations["kioskBackgroundImage"];
                    payload["kioskLogoImage"] = deviceGlobalConfigurations["kioskLogoImage"];
                    payload["kioskAppName"] = deviceGlobalConfigurations["kioskAppName"];
                    payload["isSingleModeApp"] = deviceGlobalConfigurations["isSingleModeApp"];
                    payload["keepDisplayAwake"] = deviceGlobalConfigurations["keepDisplayAwake"];

                    if (payload["isSingleModeApp"] === true) {
                        payload["isSingleModeAppBuiltForKiosk"] =
                            deviceGlobalConfigurations["isSingleModeAppBuiltForKiosk"];
                    }
                    payload["isIdleGraphicsEnabled"] = deviceGlobalConfigurations["isIdleGraphicsEnabled"];
                    payload["idleTimeout"] = deviceGlobalConfigurations["idleTimeout"];
                    payload["isMultiUserDevice"] = deviceGlobalConfigurations["isMultiUserDevice"];
                    if (payload["isMultiUserDevice"] === true) {
                        payload["isLoginRequired"] = deviceGlobalConfigurations["isLoginRequired"];
                        var userAppConfigurations = operationPayload["userAppConfigurations"];
                        var index;
                        for (index = 0; index < userAppConfigurations.length; index++) {
                            userAppConfigurations[index]["visibleAppList"] =
                                userAppConfigurations[index]["visibleAppList"].map(function (item) {
                                        var packageName = item.trim();
                                        if (packageName && packageName.charAt(0) !== "{") {
                                            var indexValue = packageName.lastIndexOf(":");
                                            if (indexValue > -1) {
                                                packageName = packageName.substring(0, indexValue);
                                            }
                                        }
                                        return packageName;
                                    }).filter(Boolean);
                            if (userAppConfigurations[index]["username"] === "primaryUser") {
                                payload["primaryUserApps"] = userAppConfigurations[index]["visibleAppList"];
                                delete userAppConfigurations[index];
                            }
                        }
                        payload["userAppConfigurations"] = userAppConfigurations.filter(Boolean);
                    }
                    payload["displayOrientation"] = deviceGlobalConfigurations["displayOrientation"];
                    if ("browserProperties" in deviceGlobalConfigurations) {
                        var browserProperties = deviceGlobalConfigurations["browserProperties"];
                        payload["isBrowserPropertyEnabled"] = browserProperties["isBrowserPropertyEnabled"];
                        payload["primaryURL"] = browserProperties["primaryURL"];
                        payload["isTopBarEnabled"] = browserProperties["isTopBarEnabled"];
                        payload["isAddressBarEnabled"] = browserProperties["isAddressBarEnabled"];
                        payload["showBackController"] = browserProperties["showBackController"];
                        payload["isForwardControllerEnabled"] = browserProperties["isForwardControllerEnabled"];
                        payload["isHomeButtonEnabled"] = browserProperties["isHomeButtonEnabled"];
                        payload["isReloadEnabled"] = browserProperties["isReloadEnabled"];
                        payload["lockToPrimaryURL"] = browserProperties["lockToPrimaryURL"];
                        payload["isJavascriptEnabled"] = browserProperties["isJavascriptEnabled"];
                        payload["isTextCopyEnabled"] = browserProperties["isTextCopyEnabled"];
                        payload["isDownloadsEnabled"] = browserProperties["isDownloadsEnabled"];
                        payload["isLockedToBrowser"] = browserProperties["isLockedToBrowser"];
                        payload["isFormAutoFillEnabled"] = browserProperties["isFormAutoFillEnabled"];
                        payload["isContentAccessEnabled"] = browserProperties["isContentAccessEnabled"];
                        payload["isFileAccessAllowed"] = browserProperties["isFileAccessAllowed"];
                        payload["isAllowedUniversalAccessFromFileURLs"] =
                            browserProperties["isAllowedUniversalAccessFromFileURLs"];
                        payload["isAllowedFileAccessFromFileURLs"] =
                            browserProperties["isAllowedFileAccessFromFileURLs"];
                        payload["isAppCacheEnabled"] = browserProperties["isAppCacheEnabled"];
                        payload["appCachePath"] = browserProperties["appCachePath"];
                        payload["cacheMode"] = browserProperties["cacheMode"];
                        payload["isLoadsImagesAutomatically"] = browserProperties["isLoadsImagesAutomatically"];
                        payload["isBlockNetworkImage"] = browserProperties["isBlockNetworkImage"];
                        payload["isBlockNetworkLoads"] = browserProperties["isBlockNetworkLoads"];
                        payload["isSupportZoomEnabled"] = browserProperties["isSupportZoomEnabled"];
                        payload["isDisplayZoomControls"] = browserProperties["isDisplayZoomControls"];
                        payload["textZoom"] = browserProperties["textZoom"];
                        payload["defaultFontSize"] = browserProperties["defaultFontSize"];
                        payload["defaultTextEncodingName"] = browserProperties["defaultTextEncodingName"];
                        payload["isDatabaseEnabled"] = browserProperties["isDatabaseEnabled"];
                        payload["isDomStorageEnabled"] = browserProperties["isDomStorageEnabled"];
                        payload["geolocationEnabled"] = browserProperties["geolocationEnabled"];
                        payload["isJavaScriptCanOpenWindowsAutomatically"] =
                            browserProperties["isJavaScriptCanOpenWindowsAutomatically"];
                        payload["isMediaPlaybackRequiresUserGesture"] =
                            browserProperties["isMediaPlaybackRequiresUserGesture"];
                        payload["isSafeBrowsingEnabled"] = browserProperties["isSafeBrowsingEnabled"];
                        payload["isUseWideViewPort"] = browserProperties["isUseWideViewPort"];
                        payload["userAgentString"] = browserProperties["userAgentString"];
                        payload["mixedContentMode"] = browserProperties["mixedContentMode"];
                    }
                }
                break;
            case androidOperationConstants["KIOSK_APPS_CODE"]:
                payload = {
                    "cosuWhitelistedApplications": operationPayload["whitelistedApplications"]
                };
                break;
            case androidOperationConstants["ENROLLMENT_APP_INSTALL_CODE"]:
                payload = {
                    "enrollmentAppInstall": operationPayload["enrollmentAppInstall"]
                };
                break;
            case androidOperationConstants["CERT_ADD_OPERATION_CODE"]:
                var certList = operationPayload["CERT_LIST"];
                var listNew = [];
                certList.forEach(function (element) {
                    element["CERT_CONTENT_VIEW"] = element["CERT_NAME"] + " File";
                   listNew.push(element);
                });
                payload = {
                    "CERT_LIST": listNew
                };
                break;
            case androidOperationConstants["DISPLAY_MESSAGE_CONFIGURATION_OPERATION_CODE"]:
                payload = {
                    "lockScreenMessage": operationPayload["lockScreenMessage"],
                    "settingAppSupportMessage": operationPayload["settingAppSupportMessage"],
                    "disabledSettingSupportMessage": operationPayload["disabledSettingSupportMessage"]
                };
                break;
        }
        return payload;
    };

    privateMethods.generateAndroidOperationPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        switch (operationCode) {
            case androidOperationConstants["CAMERA_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "CAMERA": operationData["cameraEnabled"],
                        "BACKUP_SERVICE": operationData["enableBackupService"],
                        "DISALLOW_ADJUST_VOLUME": operationData["disallowAdjustVolumeEnabled"],
                        "DISALLOW_CONFIG_BLUETOOTH": operationData["disallowConfigBluetooth"],
                        "DISALLOW_CONFIG_CELL_BROADCASTS": operationData["disallowConfigCellBroadcasts"],
                        "DISALLOW_CONFIG_CREDENTIALS": operationData["disallowConfigCredentials"],
                        "DISALLOW_CONFIG_MOBILE_NETWORKS": operationData["disallowConfigMobileNetworks"],
                        "DISALLOW_CONFIG_TETHERING": operationData["disallowConfigTethering"],
                        "DISALLOW_CONFIG_VPN": operationData["disallowConfigVpn"],
                        "DISALLOW_CONFIG_APN": operationData["disallowConfigApn"],
                        "DISALLOW_CONFIG_WIFI": operationData["disallowConfigWifi"],
                        "DISALLOW_APPS_CONTROL": operationData["disallowAppControl"],
                        "DISALLOW_CREATE_WINDOWS": operationData["disallowCreateWindows"],
                        "DISALLOW_CROSS_PROFILE_COPY_PASTE": operationData["disallowCrossProfileCopyPaste"],
                        "DISALLOW_DEBUGGING_FEATURES": operationData["disallowDebugging"],
                        "DISALLOW_FACTORY_RESET": operationData["disallowFactoryReset"],
                        "DISALLOW_ADD_USER": operationData["disallowAddUser"],
                        "DISALLOW_INSTALL_APPS": operationData["disallowInstallApps"],
                        "DISALLOW_INSTALL_UNKNOWN_SOURCES": operationData["disallowInstallUnknownSources"],
                        "DISALLOW_MODIFY_ACCOUNTS": operationData["disallowModifyAccounts"],
                        "DISALLOW_MOUNT_PHYSICAL_MEDIA": operationData["disallowMountPhysicalMedia"],
                        "DISALLOW_NETWORK_RESET": operationData["disallowNetworkReset"],
                        "DISALLOW_OUTGOING_BEAM": operationData["disallowOutgoingBeam"],
                        "DISALLOW_OUTGOING_CALLS": operationData["disallowOutgoingCalls"],
                        "DISALLOW_REMOVE_USER": operationData["disallowRemoveUser"],
                        "DISALLOW_SAFE_BOOT": operationData["disallowSafeBoot"],
                        "DISALLOW_SHARE_LOCATION": operationData["disallowLocationSharing"],
                        "DISALLOW_SMS": operationData["disallowSMS"],
                        "DISALLOW_UNINSTALL_APPS": operationData["disallowUninstallApps"],
                        "DISALLOW_UNMUTE_MICROPHONE": operationData["disallowUnmuteMicrophone"],
                        "DISALLOW_USB_FILE_TRANSFER": operationData["disallowUSBFileTransfer"],
                        "ALLOW_PARENT_PROFILE_APP_LINKING": operationData["disallowParentProfileAppLinking"],
                        "ENSURE_VERIFY_APPS": operationData["ensureVerifyApps"],
                        "AUTO_TIME": operationData["enableAutoTime"],
                        "SET_SCREEN_CAPTURE_DISABLED": operationData["disableScreenCapture"],
                        "DISALLOW_SET_WALLPAPER": operationData["disallowSetWallpaper"],
                        "DISALLOW_SET_USER_ICON": operationData["disallowSetWallpaper"],
                        "DISALLOW_REMOVE_MANAGEMENT_PROFILE": operationData["disallowRemoveManagedProfile"],
                        "DISALLOW_AUTOFILL": operationData["disallowAutoFill"],
                        "DISALLOW_BLUETOOTH": operationData["disallowBluetooth"],
                        "DISALLOW_BLUETOOTH_SHARING": operationData["disallowBluetoothSharing"],
                        "DISALLOW_REMOVE_USER": operationData["disallowRemoveUser"],
                        "DISALLOW_DATA_ROAMING": operationData["disallowDataRoaming"]
                    }
                };
                break;
            case androidOperationConstants["CHANGE_LOCK_CODE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "lockCode": operationData["lockCode"]
                    }
                };
                break;
            case androidOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "encrypted": operationData["encryptStorageEnabled"]
                    }
                };
                break;
            case androidOperationConstants["NOTIFICATION_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        //"message" : operationData["message"]
                        "messageText": operationData["messageText"],
                        "messageTitle": operationData["messageTitle"]
                    }
                };
                break;
            case androidOperationConstants["UPGRADE_FIRMWARE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "schedule": operationData["schedule"],
                        "server": operationData["server"]
                    }
                };
                break;
            case androidOperationConstants["WIPE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "pin": operationData["pin"]
                    }
                };
                break;
            case androidOperationConstants["WIFI_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "ssid": operationData["wifiSSID"],
                        "type": operationData["wifiType"],
                        "password": operationData["wifiPassword"],
                        "forceConnectEnabled": operationData["wifiForceConnectEnabled"],
                        "eap": operationData["wifiEAP"],
                        "phase2": operationData["wifiPhase2"],
                        "provisioning": operationData["wifiProvisioning"],
                        "identity": operationData["wifiIdentity"],
                        "anonymousIdentity": operationData["wifiAnoIdentity"],
                        "cacert": operationData["wifiCaCert"],
                        "cacertName": operationData["wifiCaCertName"]
                    }
                };
                break;
            case androidOperationConstants["APN_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "name": operationData["apnName"],
                        "apn": operationData["apnApn"],
                        "proxyAddress": operationData["apnProxyAddress"],
                        "proxyPort": operationData["apnProxyPort"],
                        "username": operationData["apnUsername"],
                        "password": operationData["apnPassword"],
                        "server": operationData["apnServer"],
                        "mMSC": operationData["apnMMSC"],
                        "mmsProxyAddress": operationData["apnMMSProxyAddress"],
                        "mMSProxyPort": operationData["apnMMSProxyPort"],
                        "mCC": operationData["apnMCC"],
                        "mNC": operationData["apnMNC"],
                        "authenticationType": operationData["apnAuthenticationType"],
                        "type": operationData["apnType"],
                        "protocol": operationData["apnProtocol"],
                        "roamingProtocol": operationData["apnRoamingProtocol"],
                        "isEnable": operationData["apnIsEnable"],
                        "bearer": operationData["apnBearer"],
                        "mVNOType": operationData["apnMVNOType"],
                        "mVNOValue": operationData["apnMVNOValue"]
                    }
                };
                break;
            case androidOperationConstants["GLOBAL_PROXY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "proxyConfigType": operationData["proxyConfigType"],
                        "proxyHost": operationData["proxyHost"],
                        "proxyPort": operationData["proxyPort"],
                        "proxyExclList": operationData["proxyExclList"],
                        "proxyPacUrl": operationData["proxyPacUrl"]
                    }
                };
                break;
            case androidOperationConstants["VPN_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "type": operationData["type"],
                        "openvpn_config": operationData["openvpn_config"],
                        "packageName": operationData["packageName"]
                    }
                };
                break;
            case androidOperationConstants["LOCK_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "message": operationData["lock-message"],
                        "isHardLockEnabled": operationData["hard-lock"]
                    }
                };
                break;
            case androidOperationConstants["WORK_PROFILE_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "profileName": operationData["workProfilePolicyProfileName"],
                        "enableSystemApps": operationData["workProfilePolicyEnableSystemApps"],
                        "hideSystemApps": operationData["workProfilePolicyHideSystemApps"],
                        "unhideSystemApps": operationData["workProfilePolicyUnhideSystemApps"],
                        "enablePlaystoreApps": operationData["workProfilePolicyEnablePlaystoreApps"]
                    }
                };
                break;
            case androidOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "allowSimple": operationData["passcodePolicyAllowSimple"],
                        "requireAlphanumeric": operationData["passcodePolicyRequireAlphanumeric"],
                        "minLength": operationData["passcodePolicyMinLength"],
                        "minComplexChars": operationData["passcodePolicyMinComplexChars"],
                        "maxPINAgeInDays": operationData["passcodePolicyMaxPasscodeAgeInDays"],
                        "pinHistory": operationData["passcodePolicyPasscodeHistory"],
                        "maxFailedAttempts": operationData["passcodePolicyMaxFailedAttempts"]
                    }
                };
                workProfilePasscode = {};
                if (operationData["passcodePolicyWPExist"] === true) {
                    payload.operation["passcodePolicyWPExist"] = operationData["passcodePolicyWPExist"];
                    workProfilePasscode["passcodePolicyAllowSimpleWP"] = operationData["passcodePolicyAllowSimpleWP"];
                    workProfilePasscode["passcodePolicyRequireAlphanumericWP"] = operationData["passcodePolicyRequireAlphanumericWP"];
                    workProfilePasscode["passcodePolicyMinLengthWP"] = operationData["passcodePolicyMinLengthWP"];
                    workProfilePasscode["passcodePolicyMinComplexCharsWP"] = operationData["passcodePolicyMinComplexCharsWP"];
                    workProfilePasscode["passcodePolicyMaxPasscodeAgeInDaysWP"] = operationData["passcodePolicyMaxPasscodeAgeInDaysWP"];
                    workProfilePasscode["passcodePolicyPasscodeHistoryWP"] = operationData["passcodePolicyPasscodeHistoryWP"];
                    workProfilePasscode["passcodePolicyMaxFailedAttemptsWP"] = operationData["passcodePolicyMaxFailedAttemptsWP"]  ;
                    payload.operation.workProfilePasscode = workProfilePasscode;
                } else {
                    payload["passcodePolicyWPExist"] = operationData["passcodePolicyWPExist"];
                }
                break;
                break;
            case androidOperationConstants["APPLICATION_OPERATION_CODE"]:
                payload = {
                    "operation": {
                        "restriction-type": operationData["restrictionType"],
                        "restricted-applications": operationData["restrictedApplications"]
                    }
                };
                break;
            case androidOperationConstants["RUNTIME_PERMISSION_POLICY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "defaultType": operationData["defaultPermissionType"],
                        "permittedApplications": operationData["permittedApplications"]
                    }
                };
                break;
            case androidOperationConstants["COSU_PROFILE_CONFIGURATION_OPERATION_CODE"]:
                 operationType = operationTypeConstants["PROFILE"];
                 payload = {};
                 var operation = {};
                 operation["isDeviceRestrictOperationTimeEnabled"] =
                     operationData["isDeviceRestrictOperationTimeEnabled"];
                 operation["isDeviceGlobalConfigEnabled"] = operationData["isDeviceGlobalConfigEnabled"];
                 if (operation["isDeviceRestrictOperationTimeEnabled"] === true) {
                     operation["cosuProfileRestrictionStartTime"] = operationData["cosuProfileRestrictionStartTime"];
                     operation["cosuProfileRestrictionEndTime"] = operationData["cosuProfileRestrictionEndTime"];
                 }
                 if (operationData["isDeviceGlobalConfigEnabled"] === true) {
                     var deviceGlobalConfigurations = {};
                     if (operationData["idleMediaURL"]) {
                         deviceGlobalConfigurations["idleMediaURL"] = operationData["idleMediaURL"];
                     }
                     if (operationData["kioskBackgroundImage"]) {
                         deviceGlobalConfigurations["kioskBackgroundImage"] = operationData["kioskBackgroundImage"];
                     }
                     if (operationData["kioskLogoImage"]) {
                         deviceGlobalConfigurations["kioskLogoImage"] = operationData["kioskLogoImage"];
                     }
                     if (operationData["kioskAppName"]) {
                         deviceGlobalConfigurations["kioskAppName"] = operationData["kioskAppName"];
                     }
                     deviceGlobalConfigurations["isSingleModeApp"] = operationData["isSingleModeApp"];
                     if (deviceGlobalConfigurations["isSingleModeApp"] === true) {
                         var selectedSingleModeAppElement = "div#install-app-enrollment .child-input";
                         if ($(selectedSingleModeAppElement + "[data-child-key='type']").val() === "Web Clip") {
                             var webClipApp = {
                                 "identity": $(selectedSingleModeAppElement + "[data-child-key='packageName']").val(),
                                 "title": $(selectedSingleModeAppElement + "[data-child-key='appName']").val()
                             };
                             deviceGlobalConfigurations["singleModeApp"] = JSON.stringify(webClipApp);
                         } else {
                             deviceGlobalConfigurations["singleModeApp"]
                                 = $(selectedSingleModeAppElement + "[data-child-key='packageName']").val();
                         }
                         deviceGlobalConfigurations["isSingleModeAppBuiltForKiosk"] =
                             operationData["isSingleModeAppBuiltForKiosk"];
                     }
                     deviceGlobalConfigurations["isIdleGraphicsEnabled"] = operationData["isIdleGraphicsEnabled"];
                     deviceGlobalConfigurations["keepDisplayAwake"] = operationData["keepDisplayAwake"];
                     if (operationData["idleTimeout"]) {
                         deviceGlobalConfigurations["idleTimeout"] = operationData["idleTimeout"];
                     }
                     deviceGlobalConfigurations["isMultiUserDevice"] = operationData["isMultiUserDevice"];
                     deviceGlobalConfigurations["displayOrientation"] = operationData["displayOrientation"];
                     if (deviceGlobalConfigurations["isMultiUserDevice"] === true) {
                         deviceGlobalConfigurations["isLoginRequired"] = operationData["isLoginRequired"];
                         deviceGlobalConfigurations["tenantDomainName"] = $("#logged-in-user").data("domain");
                         var storeApps = $("#cosu-profile-app-configs-storeapps").data("storeapps");
                         var primaryUserApps = {
                             "username" : "primaryUser",
                             "visibleAppList" : operationData["primaryUserApps"]
                         };
                         var userAppConfigurations = operationData["userAppConfigurations"];
                         userAppConfigurations.push(primaryUserApps);
                         var index;
                         for (index = 0; index < userAppConfigurations.length; index++) {
                             userAppConfigurations[index]["visibleAppList"] =
                                 userAppConfigurations[index]["visibleAppList"].split(/,(?![^{]*})/)
                                     .map(function (item) {
                                         var packageName = item.trim();
                                         if (packageName && storeApps) {
                                             var i;
                                             for (i=0; i<storeApps.length; i++) {
                                                 if (packageName === storeApps[i]["packageName"]) {
                                                     packageName += ":" + storeApps[i]["webUrl"];
                                                 }
                                             }
                                         }
                                         return packageName;
                                 }).filter(Boolean);
                         }
                         operation["userAppConfigurations"] = operationData["userAppConfigurations"];
                     }
                     if (operationData["isBrowserPropertyEnabled"] === true) {
                         var browserProperties = {};
                         browserProperties["isBrowserPropertyEnabled"] = true;
                         browserProperties["primaryURL"] = operationData["primaryURL"];
                         browserProperties["isTopBarEnabled"] = operationData["isTopBarEnabled"];
                         browserProperties["isAddressBarEnabled"] = operationData["isAddressBarEnabled"];
                         browserProperties["showBackController"] = operationData["showBackController"];
                         browserProperties["isForwardControllerEnabled"] = operationData["isForwardControllerEnabled"];
                         browserProperties["isHomeButtonEnabled"] = operationData["isHomeButtonEnabled"];
                         browserProperties["isReloadEnabled"] = operationData["isReloadEnabled"];
                         browserProperties["lockToPrimaryURL"] = operationData["lockToPrimaryURL"];
                         browserProperties["isJavascriptEnabled"] = operationData["isJavascriptEnabled"];
                         browserProperties["isTextCopyEnabled"] = operationData["isTextCopyEnabled"];
                         browserProperties["isDownloadsEnabled"] = operationData["isDownloadsEnabled"];
                         browserProperties["isLockedToBrowser"] = operationData["isLockedToBrowser"];
                         browserProperties["isFormAutoFillEnabled"] = operationData["isFormAutoFillEnabled"];
                         browserProperties["isContentAccessEnabled"] = operationData["isContentAccessEnabled"];
                         browserProperties["isFileAccessAllowed"] = operationData["isFileAccessAllowed"];
                         browserProperties["isAllowedUniversalAccessFromFileURLs"] =
                             operationData["isAllowedUniversalAccessFromFileURLs"];
                         browserProperties["isAllowedFileAccessFromFileURLs"] =
                             operationData["isAllowedFileAccessFromFileURLs"];
                         browserProperties["isAppCacheEnabled"] = operationData["isAppCacheEnabled"];
                         if (operationData["isAppCacheEnabled"] && operationData["appCachePath"]) {
                             browserProperties["appCachePath"] = operationData["appCachePath"];
                         }
                         browserProperties["cacheMode"] = operationData["cacheMode"];
                         browserProperties["isLoadsImagesAutomatically"] = operationData["isLoadsImagesAutomatically"];
                         browserProperties["isBlockNetworkImage"] = operationData["isBlockNetworkImage"];
                         browserProperties["isBlockNetworkLoads"] = operationData["isBlockNetworkLoads"];
                         browserProperties["isSupportZoomEnabled"] = operationData["isSupportZoomEnabled"];
                         browserProperties["isDisplayZoomControls"] = operationData["isDisplayZoomControls"];
                         if (operationData["textZoom"]) {
                             browserProperties["textZoom"] = operationData["textZoom"];
                         }
                         if (operationData["defaultFontSize"]) {
                             browserProperties["defaultFontSize"] = operationData["defaultFontSize"];
                         }
                         if (operationData["defaultTextEncodingName"]) {
                             browserProperties["defaultTextEncodingName"] = operationData["defaultTextEncodingName"];
                         }
                         browserProperties["isDatabaseEnabled"] = operationData["isDatabaseEnabled"];
                         browserProperties["isDomStorageEnabled"] = operationData["isDomStorageEnabled"];
                         browserProperties["geolocationEnabled"] = operationData["geolocationEnabled"];
                         browserProperties["isJavaScriptCanOpenWindowsAutomatically"] =
                             operationData["isJavaScriptCanOpenWindowsAutomatically"];
                         browserProperties["isMediaPlaybackRequiresUserGesture"] =
                             operationData["isMediaPlaybackRequiresUserGesture"];
                         browserProperties["isSafeBrowsingEnabled"] = operationData["isSafeBrowsingEnabled"];
                         browserProperties["isUseWideViewPort"] = operationData["isUseWideViewPort"];
                         if (operationData["userAgentString"]) {
                             browserProperties["userAgentString"] = operationData["userAgentString"];
                         }
                         browserProperties["mixedContentMode"] = operationData["mixedContentMode"];
                         deviceGlobalConfigurations.browserProperties = browserProperties;
                     }
                     operation.deviceGlobalConfigurations = deviceGlobalConfigurations;
                 }
                 payload.operation = operation;
               break;
            case androidOperationConstants["SYSTEM_UPDATE_POLICY_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                if (operationData["cosuSystemUpdatePolicyType"] != "window") {
                    payload = {
                        "operation": {
                            "type": operationData["cosuSystemUpdatePolicyType"]
                        }
                    };
                } else {
                    payload = {
                        "operation": {
                            "type": operationData["cosuSystemUpdatePolicyType"],
                            "startTime": operationData["cosuSystemUpdatePolicyWindowStartTime"],
                            "endTime": operationData["cosuSystemUpdatePolicyWindowEndTime"]
                        }
                    };
                }
                break;
            case androidOperationConstants["KIOSK_APPS_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "whitelistedApplications": operationData["cosuWhitelistedApplications"]
                    }
                };
                break;
            case androidOperationConstants["ENROLLMENT_APP_INSTALL_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "enrollmentAppInstall": operationData["enrollmentAppInstall"]
                    }
                };
                break;
            case androidOperationConstants["CERT_ADD_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "CERT_LIST": operationData["CERT_LIST"]
                    }
                };
                break;
            case androidOperationConstants["DISPLAY_MESSAGE_CONFIGURATION_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "lockScreenMessage": operationData["lockScreenMessage"],
                        "settingAppSupportMessage": operationData["settingAppSupportMessage"],
                        "disabledSettingSupportMessage": operationData["disabledSettingSupportMessage"]
                    }
                };
                break;
            default:
                // If the operation is neither of above, it is a command operation
                operationType = operationTypeConstants["COMMAND"];
                // Operation payload of a command operation is simply an array of device IDs
                payload = deviceList;
        }

        if (operationType == operationTypeConstants["PROFILE"] && deviceList) {
            payload["deviceIDs"] = deviceList;
        }

        return payload;
    };

    publicMethods.getAndroidServiceEndpoint = function (operationCode) {
        var featureMap = {
            "WIFI": "configure-wifi",
            "GLOBAL_PROXY": "configure-global-proxy",
            "CAMERA": "control-camera",
            "BACKUP_SERVICE": "enable-backup",
            "VPN": "configure-vpn",
            "APN": "configure-apn",
            "DEVICE_LOCK": "lock-devices",
            "DEVICE_UNLOCK": "unlock-devices",
            "DEVICE_LOCATION": "location",
            "CLEAR_PASSWORD": "clear-password",
            "APPLICATION_LIST": "get-application-list",
            "DEVICE_RING": "ring",
            "DEVICE_REBOOT": "reboot",
            "CHANGE_LOCK_TASK_MODE": "change-LockTask",
            "UPGRADE_FIRMWARE": "upgrade-firmware",
            "DEVICE_MUTE": "mute",
            "NOTIFICATION": "send-notification",
            "ENCRYPT_STORAGE": "encrypt-storage",
            "CHANGE_LOCK_CODE": "change-lock-code",
            "WEBCLIP": "set-webclip",
            "INSTALL_APPLICATION": "install-application",
            "UNINSTALL_APPLICATION": "uninstall-application",
            "BLACKLIST_APPLICATIONS": "blacklist-applications",
            "PASSCODE_POLICY": "set-password-policy",
            "ENTERPRISE_WIPE": "enterprise-wipe",
            "WIPE_DATA": "wipe",
            "DISPLAY_MESSAGE_CONFIGURATION": "configure-display-message"
        };
        return "/api/device-mgt/android/v1.0/admin/devices/" + featureMap[operationCode];
    };

    /**
     * Get the icon for the featureCode
     * @param operationCode
     * @returns icon class
     */
    publicMethods.getAndroidIconForFeature = function (operationCode) {
        var featureMap = {
            "DEVICE_LOCK": "fw-lock",
            "DEVICE_LOCATION": "fw-map-location",
            "CLEAR_PASSWORD": "fw-clear",
            "ENTERPRISE_WIPE": "fw-block",
            "WIPE_DATA": "fw-delete",
            "DEVICE_RING": "fw-dial-up",
            "DEVICE_REBOOT": "fw-refresh",
            "CHANGE_LOCK_TASK_MODE": "fw-mobile",
            "UPGRADE_FIRMWARE": "fw-hardware",
            "DEVICE_MUTE": "fw-mute",
            "NOTIFICATION": "fw-message",
            "CHANGE_LOCK_CODE": "fw-security",
            "DEVICE_UNLOCK": "fw-key"
        };
        return featureMap[operationCode];
    };

    /**
     * Filter a list by a data attribute.
     * @param prop
     * @param val
     * @returns {Array}
     */
    $.fn.filterByData = function (prop, val) {
        return this.filter(
            function () {
                return $(this).data(prop) == val;
            }
        );
    };

    /**
     * Method to generate Platform specific operation payload.
     *
     * @param operationCode Operation Codes to generate the profile from
     * @param deviceList Optional device list to include in payload body for operations
     * @returns {*}
     */
    publicMethods.generatePayload = function (operationCode, deviceList) {
        var payload;
        var operationData = {};
        // capturing form input data designated by .operationDataKeys
        $(".operation-data").filterByData("operation-code", operationCode).find(".operationDataKeys").each(
            function () {
                var operationDataObj = $(this);
                var key = operationDataObj.data("key");
                var value;
                if (operationDataObj.is(":text") || operationDataObj.is("textarea") ||
                    operationDataObj.is(":password") || operationDataObj.is("input[type=hidden]")) {
                    value = operationDataObj.val();
                    operationData[key] = value;
                } else if (operationDataObj.is(":checkbox")) {
                    value = operationDataObj.is(":checked");
                    operationData[key] = value;
                } else if (operationDataObj.is(":radio") && operationDataObj.is(":checked")) {
                    value = operationDataObj.val();
                    operationData[key] = value;
                } else if (operationDataObj.is("select")) {
                    value = operationDataObj.find("option:selected").attr("value");
                    operationData[key] = value;
                } else if (operationDataObj.hasClass("grouped-array-input")) {
                    value = [];
                    var childInput;
                    var childInputValue;
                    if (operationDataObj.hasClass("one-column-input-array")) {
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            if (childInput.is(":text") || childInput.is("textarea") || childInput.is(":password")
                                || childInput.is("input[type=hidden]")) {
                                childInputValue = childInput.val();
                            } else if (childInput.is(":checkbox")) {
                                childInputValue = childInput.is(":checked");
                            } else if (childInput.is("select")) {
                                childInputValue = childInput.find("option:selected").attr("value");
                            }
                            // push to value
                            value.push(childInputValue);
                        });
                    } else if (operationDataObj.hasClass("valued-check-box-array")) {
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            if (childInput.is(":checked")) {
                                // get associated value with check-box
                                childInputValue = childInput.data("value");
                                // push to value
                                value.push(childInputValue);
                            }
                        });
                    } else if (operationDataObj.hasClass("multi-column-joined-input-array")) {
                        var columnCount = operationDataObj.data("column-count");
                        var inputCount = 0;
                        var joinedInput;
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            if (childInput.is(":text") || childInput.is("textarea") || childInput.is(":password")
                                || childInput.is("input[type=hidden]")) {
                                childInputValue = childInput.val();
                            } else if (childInput.is(":checkbox")) {
                                childInputValue = childInput.is(":checked");
                            } else if (childInput.is("select")) {
                                childInputValue = childInput.find("option:selected").attr("value");
                            }
                            inputCount++;
                            if (inputCount % columnCount == 1) {
                                // initialize joinedInput value
                                joinedInput = "";
                                // append childInputValue to joinedInput
                                joinedInput += childInputValue;
                            } else if ((inputCount % columnCount) >= 2) {
                                // append childInputValue to joinedInput
                                joinedInput += childInputValue;
                            } else {
                                // append childInputValue to joinedInput
                                joinedInput += childInputValue;
                                // push to value
                                value.push(joinedInput);
                            }
                        });
                    } else if (operationDataObj.hasClass("multi-column-key-value-pair-array")) {
                        columnCount = operationDataObj.data("column-count");
                        inputCount = 0;
                        var childInputKey;
                        var keyValuePairJson;
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            childInputKey = childInput.data("child-key");
                            if (childInput.is(":text") || childInput.is("textarea") || childInput.is(":password")
                                || childInput.is("input[type=hidden]")) {
                                childInputValue = childInput.val();
                            } else if (childInput.is(":checkbox")) {
                                childInputValue = childInput.is(":checked");
                            } else if (childInput.is("select")) {
                                childInputValue = childInput.find("option:selected").attr("value");
                            }
                            inputCount++;
                            if ((inputCount % columnCount) == 1) {
                                // initialize keyValuePairJson value
                                keyValuePairJson = {};
                                // set key-value-pair
                                keyValuePairJson[childInputKey] = childInputValue;
                            } else if ((inputCount % columnCount) >= 2) {
                                // set key-value-pair
                                keyValuePairJson[childInputKey] = childInputValue;
                            } else {
                                // set key-value-pair
                                keyValuePairJson[childInputKey] = childInputValue;
                                // push to value
                                value.push(keyValuePairJson);
                            }
                        });
                    }
                    operationData[key] = value;
                }
            }
        );
        payload = privateMethods.generateAndroidOperationPayload(operationCode, operationData, deviceList);
        return payload;
    };

    /**
     * Method to populate the Platform specific operation payload.
     *
     * @param operationCode Operation Codes to generate the profile from
     * @param operationPayload payload
     * @returns {*}
     */
    publicMethods.populateUI = function (operationCode, operationPayload) {
        var uiPayload = privateMethods.generateGenericPayloadFromAndroidPayload(operationCode, operationPayload);
        // capturing form input data designated by .operationDataKeys
        $(".operation-data").filterByData("operation-code", operationCode).find(".operationDataKeys").each(
            function () {
                var operationDataObj = $(this);
                //operationDataObj.prop('disabled', true)
                var key = operationDataObj.data("key");
                // retrieve corresponding input value associated with the key
                var value = uiPayload[key];
                // populating input value according to the type of input
                if (operationDataObj.is(":text") ||
                    operationDataObj.is("textarea") ||
                    operationDataObj.is(":password") ||
                    operationDataObj.is("input[type=hidden]")) {
                    operationDataObj.val(value);
                } else if (operationDataObj.is(":checkbox")) {
                    operationDataObj.prop("checked", value);
                    operationDataObj.trigger("change");
                } else if (operationDataObj.is(":radio")) {
                    if (operationDataObj.val() == uiPayload[key]) {
                        operationDataObj.attr("checked", true);
                        operationDataObj.trigger("click");
                    }
                } else if (operationDataObj.is("select")) {
                    operationDataObj.val(value);
                    /* trigger a change of value, so that if slidable panes exist,
                     make them slide-down or slide-up accordingly */
                    operationDataObj.trigger("change");
                } else if (operationDataObj.hasClass("grouped-array-input")) {
                    // then value is complex
                    var i, childInput;
                    var childInputIndex = 0;
                    // var childInputValue;
                    if (operationDataObj.hasClass("one-column-input-array")) {
                        // generating input fields to populate complex value
                        if (value) {
                            for (i = 0; i < value.length; ++i) {
                                operationDataObj.parent().find("a").filterByData("click-event", "add-form").click();
                            }
                            // traversing through each child input
                            $(".child-input", this).each(function () {
                                childInput = $(this);
                                var childInputValue = value[childInputIndex];
                                // populating extracted value in the UI according to the input type
                                if (childInput.is(":text") ||
                                    childInput.is("textarea") ||
                                    childInput.is(":password") ||
                                    childInput.is("input[type=hidden]") ||
                                    childInput.is("select")) {
                                    childInput.val(childInputValue);
                                } else if (childInput.is(":checkbox")) {
                                    operationDataObj.prop("checked", childInputValue);
                                }
                                // incrementing childInputIndex
                                childInputIndex++;
                            });
                        }
                    } else if (operationDataObj.hasClass("valued-check-box-array")) {
                        // traversing through each child input
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            // check if corresponding value of current checkbox exists in the array of values
                            if (value) {
                                if (value.indexOf(childInput.data("value")) != -1) {
                                    // if YES, set checkbox as checked
                                    childInput.prop("checked", true);
                                }
                            }
                        });
                    } else if (operationDataObj.hasClass("multi-column-joined-input-array")) {
                        // generating input fields to populate complex value
                        if (value) {
                            for (i = 0; i < value.length; ++i) {
                                operationDataObj.parent().find("a").filterByData("click-event", "add-form").click();
                            }
                            var columnCount = operationDataObj.data("column-count");
                            var multiColumnJoinedInputArrayIndex = 0;
                            // handling scenarios specifically
                            if (operationDataObj.attr("id") == "wifi-mcc-and-mncs") {
                                // traversing through each child input
                                $(".child-input", this).each(function () {
                                    childInput = $(this);
                                    var multiColumnJoinedInput = value[multiColumnJoinedInputArrayIndex];
                                    var childInputValue;
                                    if ((childInputIndex % columnCount) == 0) {
                                        childInputValue = multiColumnJoinedInput.substring(3, 0)
                                    } else {
                                        childInputValue = multiColumnJoinedInput.substring(3);
                                        // incrementing childInputIndex
                                        multiColumnJoinedInputArrayIndex++;
                                    }
                                    // populating extracted value in the UI according to the input type
                                    if (childInput.is(":text") ||
                                        childInput.is("textarea") ||
                                        childInput.is(":password") ||
                                        childInput.is("input[type=hidden]") ||
                                        childInput.is("select")) {
                                        childInput.val(childInputValue);
                                    } else if (childInput.is(":checkbox")) {
                                        operationDataObj.prop("checked", childInputValue);
                                    }
                                    // incrementing childInputIndex
                                    childInputIndex++;
                                });
                            }
                        }
                    } else if (operationDataObj.hasClass("multi-column-key-value-pair-array")) {
                        // generating input fields to populate complex value
                        if (value) {
                            if (operationDataObj.hasClass("specific-enrollment-app-install")) {
                                if ($(".enrollment-app-install-input", this).length > 0) {
                                    $("select#enrollment-app-install-table_length").val("100").change();
                                    for (i=0; i<value.length; i++) {
                                        $(".enrollment-app-install-input", this).each(function() {
                                            childInput = $(this);
                                            var childInputKey = childInput.data("child-key");
                                            if (childInputKey == "productSetBehavior" && value[i].productSetBehavior) {
                                                $('select#product-set-behaviour').attr('data-product-set-behavior',
                                                                                                value[i].productSetBehavior);
                                            }
                                            if (childInputKey == "autoUpdatePolicy" && value[i].autoUpdatePolicy) {
                                                $('select#auto-update-policy').attr('data-auto-update-policy',
                                                                                                value[i].autoUpdatePolicy);
                                            }
                                            if (childInputKey === "installGooglePolicy"
                                                && value[i].appId === childInput[0].getAttribute("data-app-id")){
                                                if (value[i].installGooglePolicy) {
                                                    childInput.parent().find("input").filterByData("child-key", "installGooglePolicyPayload").val(value[i].installGooglePolicyPayload);
                                                    childInput.filterByData("child-key", "installGooglePolicy").prop('checked', true);
                                                    childInput.parents("tr").last().find("input").each(function () {
                                                        if(!$(this).hasClass("child-input")) {
                                                            $(this).addClass("child-input");
                                                        }
                                                    });
                                                }
                                            } else if (childInputKey === "enrollmentAppInstall"
                                                && value[i].appId === childInput[0].getAttribute("data-app-id")){
                                                if (value[i].enrollmentAppInstall) {
                                                    childInput.filterByData("child-key", "enrollmentAppInstall").prop('checked', true);
                                                    childInput.parents("tr").last().find("input").each(function () {
                                                        if(!$(this).hasClass("child-input")) {
                                                            $(this).addClass("child-input");
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    $('[data-add-form-container="#enrollment-app-install-grid"]').empty();
                                    for (i=0; i<value.length; i++) {

                                        var enrollmentAppInstallInput = '<input type="checkbox" disabled/>';
                                        if (value[i].enrollmentAppInstall) {
                                            enrollmentAppInstallInput = '<input type="checkbox" checked disabled/>';
                                        }

                                        var installGooglePolicy = '<input type="checkbox" disabled/>';
                                        if (value[i].installGooglePolicy) {
                                            installGooglePolicy = '<input type="checkbox" checked disabled/>';
                                        }


                                        var content = '<tr><td data-title="enrollment-app-install-app-name">'
                                            + value[i].appName + '</td><td data-title="enrollment-app-install-app-type">'
                                            + value[i].type + '</td><td data-title="enrollment-app-install-version">'
                                            + value[i].version + '</td>'
                                            + '<td>' + enrollmentAppInstallInput + '</td>'
                                            + '<td>' + installGooglePolicy + '</td>'
                                            +'</tr>';
                                        $('[data-add-form-container="#enrollment-app-install-grid"]').append(content);
                                    }
                                }
                            } else {
                                for (i = 0; i < value.length; ++i) {
                                    operationDataObj.parent().find("a").filterByData("click-event", "add-form").click();
                                }
                                columnCount = operationDataObj.data("column-count");
                                var multiColumnKeyValuePairArrayIndex = 0;
                                // traversing through each child input
                                $(".child-input", this).each(function () {
                                    childInput = $(this);
                                    var multiColumnKeyValuePair = value[multiColumnKeyValuePairArrayIndex];
                                    var childInputKey = childInput.data("child-key");
                                    var childInputValue = multiColumnKeyValuePair[childInputKey];

                                    // populating extracted value in the UI according to the input type
                                    if (childInput.is(":text") ||
                                        childInput.is("textarea") ||
                                        childInput.is(":password") ||
                                        childInput.is("input[type=hidden]") ||
                                        childInput.is("select")) {
                                        childInput.val(childInputValue);
                                    } else if (childInput.is(":checkbox")) {
                                        operationDataObj.prop("checked", childInputValue);
                                    }
                                    // incrementing multiColumnKeyValuePairArrayIndex for the next row of inputs
                                    if ((childInputIndex % columnCount) == (columnCount - 1)) {
                                        multiColumnKeyValuePairArrayIndex++;
                                    }
                                    // incrementing childInputIndex
                                    childInputIndex++;
                                });
                            }
                        }
                    }
                }
            }
        );
    };

    /**
     * generateProfile method is only used for policy-creation UIs.
     *
     * @param operationCodes Operation codes to generate the profile from
     * @returns {object} generated profile
     */
    publicMethods.generateProfile = function (operationCodes) {
        var generatedProfile = {};
        for (var i = 0; i < operationCodes.length; ++i) {
            var operationCode = operationCodes[i];
            var payload = publicMethods.generatePayload(operationCode, null);

            if (operationCodes[i] == androidOperationConstants["CAMERA_OPERATION_CODE"]) {
                var operations = payload["operation"];
                for (var key in operations) {
                    operationCode = key;
                    var restriction = false;
                    if (operations[key]) {
                        restriction = true;
                    }
                    var payloadResult = {
                        "operation": {
                            "enabled": restriction
                        }
                    };
                    generatedProfile[operationCode] = payloadResult["operation"];
                }

            } else {
                generatedProfile[operationCode] = payload["operation"];
            }
        }
        return generatedProfile;
    };

    /**
     * populateProfile method is used to populate the html ui with saved payload.
     *
     * @param payload List of profileFeatures
     * @returns [] configuredOperations array
     */
    publicMethods.populateProfile = function (payload) {
        var i, configuredOperations = [];
        var restrictions = {};
        for (i = 0; i < payload.length; ++i) {
            var configuredFeature = payload[i];
            var featureCode = configuredFeature["featureCode"];
            var operationPayload = configuredFeature["content"];
            var restriction = JSON.parse(operationPayload);
            if (featureCode == androidOperationConstants["CAMERA_OPERATION_CODE"]) {
                restrictions["cameraEnabled"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_ADJUST_VOLUME"]) {
                restrictions["disallowAdjustVolumeEnabled"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["BACKUP_SERVICE_CODE"]) {
                restrictions["enableBackupService"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_BLUETOOTH"]) {
                restrictions["disallowConfigBluetooth"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_CELL_BROADCASTS"]) {
                restrictions["disallowConfigCellBroadcasts"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_CREDENTIALS"]) {
                restrictions["disallowConfigCredentials"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_MOBILE_NETWORKS"]) {
                restrictions["disallowConfigMobileNetworks"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_TETHERING"]) {
                restrictions["disallowConfigTethering"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_VPN"]) {
                restrictions["disallowConfigVpn"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_APN"]) {
                restrictions["disallowConfigApn"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_WIFI"]) {
                restrictions["disallowConfigWifi"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_APPS_CONTROL"]) {
                restrictions["disallowAppControl"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CREATE_WINDOWS"]) {
                restrictions["disallowCreateWindows"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_CROSS_PROFILE_COPY_PASTE"]) {
                restrictions["disallowCrossProfileCopyPaste"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_DEBUGGING_FEATURES"]) {
                restrictions["disallowDebugging"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_FACTORY_RESET"]) {
                restrictions["disallowFactoryReset"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_ADD_USER"]) {
                restrictions["disallowAddUser"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_INSTALL_APPS"]) {
                restrictions["disallowInstallApps"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_INSTALL_UNKNOWN_SOURCES"]) {
                restrictions["disallowInstallUnknownSources"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_MODIFY_ACCOUNTS"]) {
                restrictions["disallowModifyAccounts"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_MOUNT_PHYSICAL_MEDIA"]) {
                restrictions["disallowMountPhysicalMedia"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_NETWORK_RESET"]) {
                restrictions["disallowNetworkReset"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_OUTGOING_BEAM"]) {
                restrictions["disallowOutgoingBeam"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_OUTGOING_CALLS"]) {
                restrictions["disallowOutgoingCalls"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_REMOVE_USER"]) {
                restrictions["disallowRemoveUser"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_SAFE_BOOT"]) {
                restrictions["disallowSafeBoot"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_SHARE_LOCATION"]) {
                restrictions["disallowLocationSharing"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_SMS"]) {
                restrictions["disallowSMS"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_UNINSTALL_APPS"]) {
                restrictions["disallowUninstallApps"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_UNMUTE_MICROPHONE"]) {
                restrictions["disallowUnmuteMicrophone"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["DISALLOW_USB_FILE_TRANSFER"]) {
                restrictions["disallowUSBFileTransfer"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["ALLOW_PARENT_PROFILE_APP_LINKING"]) {
                restrictions["disallowParentProfileAppLinking"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["ENSURE_VERIFY_APPS"]) {
                restrictions["ensureVerifyApps"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["AUTO_TIME"]) {
                restrictions["enableAutoTime"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["SET_SCREEN_CAPTURE_DISABLED"]) {
                restrictions["disableScreenCapture"] = restriction["enabled"];
                continue;
            } else if (featureCode == androidOperationConstants["SET_STATUS_BAR_DISABLED"]) {
                restrictions["disableStatusBar"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_SET_WALLPAPER"]) {
                restrictions["disallowSetWallpaper"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_SET_USER_ICON"]) {
                restrictions["disallowSetUserIcon"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_REMOVE_MANAGEMENT_PROFILE"]) {
                restrictions["disallowRemoveManagedProfile"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_AUTOFILL"]) {
                restrictions["disallowAutoFill"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_BLUETOOTH"]) {
                restrictions["disallowBluetooth"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_BLUETOOTH_SHARING"]) {
                restrictions["disallowBluetoothSharing"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_REMOVE_USER"]) {
                restrictions["disallowRemoveUser"] = restriction["enabled"];
                continue;
            }else if (featureCode == androidOperationConstants["DISALLOW_DATA_ROAMING"]) {
                restrictions["disallowDataRoaming"] = restriction["enabled"];
                continue;
            }
            //push the feature-code to the configuration array
            configuredOperations.push(featureCode);
            publicMethods.populateUI(featureCode, operationPayload);
        }
        if (typeof restrictions.cameraEnabled !== 'undefined') {
            configuredOperations.push(androidOperationConstants["CAMERA_OPERATION_CODE"]);
            publicMethods.populateUI(androidOperationConstants["CAMERA_OPERATION_CODE"], JSON.stringify(restrictions));
        }
        return configuredOperations;
    };

    return publicMethods;
}();