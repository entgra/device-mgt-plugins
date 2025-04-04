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

/*
 * On operation click function.
 * @param selection: Selected operation
 */
function operationSelect(selection) {
    $(modalPopupContent).addClass("operation-data");
    $(modalPopupContent).html($(" .operation[data-operation-code=" + selection + "]").html());
    $(modalPopupContent).data("operation-code", selection);
    if (selection === "FILE_TRANSFER") {
        fileTransferSelection();
    }
    showPopup();
}

var resetLoader = function (formId) {
    $('#' + formId + ' .btnSend').removeClass("hidden");
    $('#' + formId + ' .lbl-execution').addClass("hidden");
};

/**
 * This function changes/hide/show field respective to the selection.
 */
function fileTransferSelection() {
    var userName = document.getElementById('userName');
    var password = document.getElementById('ftpPassword');
    var infoTxt = document.getElementById('defaultFileLocation');
    $(userName).hide();
    $(password).hide();
    $(infoTxt).hide();
    fillUserName();
    checkAuth();
    changeLabels();
}

/**
 * This changes the text box label when the operation is toggled between To device and From device
 * and shows an info label for FILE UPLOAD regarding saving location.
 */
function changeLabels() {
    var upload = document.getElementById('upload');
    var download = document.getElementById('download');
    var infoTxt = document.getElementById('defaultFileLocation');
    console.log("info text " + infoTxt.value);
    jQuery(upload).change(function () {
        document.getElementById('fileURL').placeholder = "File URL";
        document.getElementById('fileLocation').placeholder = "Location to save file in device";
        $(infoTxt).show();
    });
    jQuery(download).change(function () {
        document.getElementById('fileURL').placeholder = "URL to upload file from device";
        document.getElementById('fileLocation').placeholder = "File location in the device";
        $(infoTxt).hide();
    });
}

/**
 * This function show/hide username and password text boxes when authentication is toggled.
 */
function checkAuth() {
    var auth = document.getElementById('authentication');
    var userName = document.getElementById('userName');
    var password = document.getElementById('ftpPassword');
    jQuery(auth).click(function () {
        if (this.checked) {
            $(userName).show();
            $(password).show();
        } else {
            $(userName).hide();
            $(password).hide();
        }
    });
}

/**
 * This function extracts the user name from the file url and fills it in the user name field.
 */
function fillUserName() {
    var inputBox = document.getElementById('fileURL');
    var regexp = ':\/\/[^\/]*@';
    var pattern = new RegExp(regexp);
    jQuery(inputBox).on('input', function () {
            var fileUrl = inputBox.value;
            var res = pattern.test(fileUrl);
            if (res) {
                var name = fileUrl.match(regexp).toString();
                document.getElementById('userName').value = name.substring(3, name.length - 1);
            } else {
                document.getElementById('userName').value = "";
                document.getElementById('userName').placeholder = "User Name"
            }
        }
    );
}

function submitForm(formId) {
    $('#' + formId + " .btnSend").addClass("hidden");
    $('#' + formId + " .lbl-execution").removeClass("hidden");
    var form = $("#" + formId);
    var uri = form.attr("action");
    var deviceIdList = form.data("device-id");
    var contentType = form.data("content-type");
    var operationCode = form.data("operation-code");
    var uriEncodedQueryStr = "";
    var uriEncodedFormStr = "";
    var payload = {};
    form.find("input").each(function () {
        var input = $(this);
        var prefix;
        if (input.data("param-type") === "path") {
            uri = uri.replace("{" + input.attr("id") + "}", input.val());
        } else if (input.data("param-type") === "query") {
            prefix = !uriEncodedQueryStr ? "?" : "&";
            uriEncodedQueryStr += prefix + input.attr("id") + "=" + input.val();
        } else if (input.data("param-type") === "form") {
            prefix = !uriEncodedFormStr ? "" : "&";
            uriEncodedFormStr += prefix + input.attr("id") + "=" + input.val();
            if (input.attr("type") === "text" || input.attr("type") === "password") {
                payload[input.attr("id")] = input.val();
            } else if (input.attr("type") === "checkbox") {
                payload[input.attr("id")] = input.is(":checked");
            } else if (input.attr("type") === "radio") {
                payload[input.attr("id")] = input.is(":checked");
            }
        }
    });
    uri += uriEncodedQueryStr;
    var httpMethod = form.attr("method").toUpperCase();
    var validateString = validatePayload(operationCode, payload);

    if (validateString === "OK") {

        if (!contentType) {
            contentType = "application/x-www-form-urlencoded";
            payload = uriEncodedFormStr;
        }

        //setting responses callbacks
        var defaultStatusClasses = "fw fw-stack-1x";
        var content = $("#operation-response-template").find(".content");
        var title = content.find("#title");
        title.attr("class", "center-block text-center");
        var statusIcon = content.find("#status-icon");
        var description = content.find("#description");
        description.html("");

        var successCallBack = function (response) {
            var res = response;
            try {
                res = JSON.parse(response).messageFromServer;
            } catch (err) {
                //do nothing
            }
            title.html("Operation Triggered!");
            statusIcon.attr("class", defaultStatusClasses + " fw-check");
            description.html(res);
            // console.log("success!");
            resetLoader(formId);
            $(modalPopupContent).html(content.html());
        };
        var errorCallBack = function (response) {
            // console.log(response);
            title.html("An Error Occurred!");
            statusIcon.attr("class", defaultStatusClasses + " fw-error");
            var reason = (response.responseText === "null") ? response.statusText : response.responseText;
            try {
                reason = JSON.parse(reason).message;
            } catch (err) {
                //do nothing
            }
            description.html(reason);
            // console.log("Error!");
            resetLoader(formId);
            $(modalPopupContent).html(content.html());
        };
        //executing http request
        if (httpMethod === "GET") {
            invokerUtil.get(uri, successCallBack, errorCallBack, contentType);
        } else if (httpMethod === "POST") {
            var deviceList = deviceIdList.split(",");
            payload = generatePayload(operationCode, payload, deviceList);
            invokerUtil.post(uri, payload, successCallBack, errorCallBack, contentType);
        } else if (httpMethod === "PUT") {
            invokerUtil.put(uri, payload, successCallBack, errorCallBack, contentType);
        } else if (httpMethod === "DELETE") {
            invokerUtil.delete(uri, successCallBack, errorCallBack, contentType);
        } else {
            title.html("An Error Occurred!");
            statusIcon.attr("class", defaultStatusClasses + " fw-error");
            description.html("This operation requires http method: " + httpMethod + " which is not supported yet!");
            resetLoader(formId);
            $(modalPopupContent).html(content.html());
        }
    } else {
        resetLoader(formId);
        $(".modal #operation-error-msg span").text(validateString);
        $(".modal #operation-error-msg").removeClass("hidden");
    }
}

$(document).on('submit', 'form', function (e) {
    e.preventDefault();
    var postOperationRequest = $.ajax({
        url: $(this).attr("action") + '&' + $(this).serialize(),
        method: "post"
    });

    var btnSubmit = $('.btnSend', this);
    btnSubmit.addClass('hidden');

    var lblSending = $('.lblSending', this);
    lblSending.removeClass('hidden');

    var lblSent = $('.lblSent', this);
    postOperationRequest.done(function (data) {
        lblSending.addClass('hidden');
        lblSent.removeClass('hidden');
        setTimeout(function () {
            hidePopup();
        }, 3000);
    });

    postOperationRequest.fail(function (jqXHR, textStatus) {
        lblSending.addClass('hidden');
        lblSent.addClass('hidden');
    });
});

// Constants to define operation types available
var operationTypeConstants = {
    "PROFILE": "profile",
    "CONFIG": "config",
    "COMMAND": "command"
};

function validatePayload(operationCode, payload) {
    console.log(payload);
    var returnVal = "OK";
    switch (operationCode) {
        case "NOTIFICATION":
            if (!payload.messageText) {
                returnVal = "Message Body Can't be empty !";
            }
            break;
        case "FILE_TRANSFER":
            returnVal = validateFileTransferParameters(payload);
            break;
        default:
            break;

    }
    return returnVal;
}

/**
 * This function validates all the parameters that are entered related to the file transfer operation.
 * @param payload
 * @returns {string}
 */
function validateFileTransferParameters(payload) {
    var returnVal = "OK";
    var auth = document.getElementById('authentication');
    var protocol = $(document.getElementById('protocol')).find("option:selected").text();
    if (payload.upload && !payload.fileURL) {
        returnVal = "Please enter File URL";
    } else if (!payload.upload && !payload.fileURL) {
        returnVal = "Please enter the URL to upload file from device";
    } else if (protocol === "HTTP" && !(payload.fileURL.startsWith("http:") || payload.fileURL.startsWith("https:"))) {
        returnVal = "Please enter HTTP URL"
    } else if (protocol === "FTP" && !(payload.fileURL).startsWith("ftp:")) {
        returnVal = "Please enter FTP URL"
    } else if (protocol === "SFTP" && !(payload.fileURL).startsWith("sftp:")) {
        returnVal = "Please enter SFTP URL"
    } else if (!payload.upload && !payload.fileLocation) {
        returnVal = "Please specify the file location in device";
    } else if (auth.checked && !payload.userName) {
        returnVal = "Please enter the user name if authentication required"
    }
    return returnVal;
}

var generatePayload = function (operationCode, operationData, deviceList) {
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
                    "SET_STATUS_BAR_DISABLED": operationData["disableStatusBar"],
                    "DISALLOW_SET_WALLPAPER": operationData["disallowSetWallpaper"],
                    "DISALLOW_SET_USER_ICON": operationData["disallowSetUserIcon"],
                    "DISALLOW_REMOVE_MANAGEMENT_PROFILE": operationData["disallowRemoveManagedProfile"],
                    "DISALLOW_AUTOFILL": operationData["disallowAutoFill"],
                    "DISALLOW_BLUETOOTH": operationData["disallowBluetooth"],
                    "DISALLOW_BLUETOOTH_SHARING": operationData["disallowBluetoothSharing"],
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
        case androidOperationConstants["FILE_TRANSFER"]:
            operationType = operationTypeConstants["PROFILE"];
            payload = {
                "operation": {
                    "fileURL": operationData["fileURL"],
                    "userName": operationData["userName"],
                    "ftpPassword": operationData["ftpPassword"],
                    "fileLocation": operationData["fileLocation"]
                },
                "upload": operationData["upload"]
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
                    "messageTitle": operationData["messageTitle"],
                    "messageText": operationData["messageText"]
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
                    "maxFailedAttemptsDeviceLock": operationData["maximumNumberOfFailedAttemptsBeforeDeviceLock"],
                    "maxFailedAttemptsDeviceWipe": operationData["maximumNumberOfFailedAttemptsBeforeDeviceWipe"]
                }
            };
            break;
        case androidOperationConstants["APPLICATION_OPERATION_CODE"]:
            payload = {
                "operation": {
                    "restriction-type": operationData["restrictionType"],
                    "restricted-applications": operationData["restrictedApplications"]
                }
            };
            break;
        case androidOperationConstants["SYSTEM_UPDATE_POLICY_CODE"]:
            operationType = operationTypeConstants["PROFILE"];
            if (operationData["cosuSystemUpdatePolicyType"] !== "window") {
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
        case androidOperationConstants["APP_RESTRICTION_OPERATION_CODE"]:
            operationType = operationTypeConstants["PROFILE"];
            payload = {
                 "operation": {
                 "appIdentifier": operationData["app-id"],
                 "restrictionPayload": operationData["payload"]
                 }
            };
            break;
        default:
            // If the operation is neither of above, it is a command operation
            operationType = operationTypeConstants["COMMAND"];
            // Operation payload of a command operation is simply an array of device IDs
            payload = deviceList;
    }

    if (operationType === operationTypeConstants["PROFILE"] && deviceList) {
        payload["deviceIDs"] = deviceList;
    }
    return payload;
};


// Constants to define Android Operation Constants
var androidOperationConstants = {
    "PASSCODE_POLICY_OPERATION_CODE": "PASSCODE_POLICY",
    "VPN_OPERATION_CODE": "VPN",
    "CAMERA_OPERATION_CODE": "CAMERA",
    "BACKUP_SERVICE_CODE": "BACKUP_SERVICE",
    "ENCRYPT_STORAGE_OPERATION_CODE": "ENCRYPT_STORAGE",
    "WIFI_OPERATION_CODE": "WIFI",
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
    "FILE_TRANSFER": "FILE_TRANSFER",
    "APP_RESTRICTION_OPERATION_CODE": "REMOTE_APP_CONFIG",
    "DISALLOW_SET_WALLPAPER": "DISALLOW_SET_WALLPAPER",
    "DISALLOW_SET_USER_ICON": "DISALLOW_SET_USER_ICON",
    "DISALLOW_REMOVE_MANAGEMENT_PROFILE": "DISALLOW_REMOVE_MANAGEMENT_PROFILE",
    "DISALLOW_AUTOFILL": "DISALLOW_AUTOFILL",
    "DISALLOW_BLUETOOTH": "DISALLOW_BLUETOOTH",
    "DISALLOW_BLUETOOTH_SHARING": "DISALLOW_BLUETOOTH_SHARING",
    "DISALLOW_DATA_ROAMING": "DISALLOW_DATA_ROAMING"
};
