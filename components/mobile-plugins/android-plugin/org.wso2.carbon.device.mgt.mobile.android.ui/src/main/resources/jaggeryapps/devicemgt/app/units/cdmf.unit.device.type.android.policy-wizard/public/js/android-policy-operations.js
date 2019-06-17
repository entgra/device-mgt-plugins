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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var configuredOperations = [];

var androidOperationConstants = {
    "PASSCODE_POLICY_OPERATION": "passcode-policy",
    "PASSCODE_POLICY_OPERATION_CODE": "PASSCODE_POLICY",
    "CAMERA_OPERATION": "camera",
    "CAMERA_OPERATION_CODE": "CAMERA",
    "ENCRYPT_STORAGE_OPERATION": "encrypt-storage",
    "ENCRYPT_STORAGE_OPERATION_CODE": "ENCRYPT_STORAGE",
    "WIFI_OPERATION": "wifi",
    "WIFI_OPERATION_CODE": "WIFI",
    "GLOBAL_PROXY_OPERATION": "global-proxy",
    "GLOBAL_PROXY_OPERATION_CODE": "GLOBAL_PROXY",
    "VPN_OPERATION": "vpn",
    "VPN_OPERATION_CODE": "VPN",
    "APPLICATION_OPERATION": "app-restriction",
    "APPLICATION_OPERATION_CODE": "APP-RESTRICTION",
    "KIOSK_APPS_CODE": "KIOSK_APPS",
    "KIOSK_APPS": "cosu-whitelisted-applications",
    "COSU_PROFILE_CONFIGURATION_OPERATION": "cosu-profile-configuration",
    "COSU_PROFILE_CONFIGURATION_OPERATION_CODE": "COSU_PROFILE",
    "ENROLLMENT_APP_INSTALL": "enrollment-app-install",
    "ENROLLMENT_APP_INSTALL_CODE": "ENROLLMENT_APP_INSTALL",
    "CERTIFICATE_INSTALL": "INSTALL_CERT"
};

/**
 * Method to update the visibility of grouped input.
 * @param domElement HTML grouped-input element with class name "grouped-input"
 */
var updateGroupedInputVisibility = function (domElement) {
    if ($(".parent-input:first", domElement).is(":checked")) {
        if ($(".grouped-child-input:first", domElement).hasClass("disabled")) {
            $(".grouped-child-input:first", domElement).removeClass("disabled");
        }
        $(".child-input", domElement).each(function () {
            $(this).prop('disabled', false);
        });
    } else {
        if (!$(".grouped-child-input:first", domElement).hasClass("disabled")) {
            $(".grouped-child-input:first", domElement).addClass("disabled");
        }
        $(".child-input", domElement).each(function () {
            $(this).prop('disabled', true);
        });
    }
};

/**
 * Checks if provided number is valid against a range.
 *
 * @param numberInput Number Input
 * @param min Minimum Limit
 * @param max Maximum Limit
 * @returns {boolean} Returns true if input is within the specified range
 */
var inputIsValidAgainstRange = function (numberInput, min, max) {
    return (numberInput == min || (numberInput > min && numberInput < max) || numberInput == max);
};

var ovpnConfigUploaded = function () {
    var ovpnFileInput = document.getElementById("ovpn-file");
    if ('files' in ovpnFileInput) {
        if (ovpnFileInput.files.length === 1) {
            var reader = new FileReader();
            reader.onload = function(progressEvent){
                var txt = "";
                var lines = this.result.split('\n');
                for(var line = 0; line < lines.length; line++){
                    console.log(lines[line]);
                    if (lines[line].charAt(0) !== '#') {
                        txt += lines[line] + '\n';
                    }
                }
                document.getElementById ("openvpn-config").value = txt;
                console.log(txt);
            };
            reader.readAsText(ovpnFileInput.files[0]);
        }
    }
};


var certConfigUploaded = function (val) {
    var certFileInput = document.getElementById("cert-file-field");
    if ('files' in certFileInput) {
        if (certFileInput.files.length === 1) {
            var reader = new FileReader();
            reader.onload = function(progressEvent){
                var txt = "";
                var lines = this.result.split('\n');
                for(var line = 0; line < lines.length; line++){
                    console.log(lines[line]);
                    if (lines[line].charAt(0) !== '#') {
                        txt += lines[line] + '\n';
                    }
                }
                //document.getElementById ("cert-config").value = txt;
                //console.log(document.getElementById ("cert-config").value);
                $(val).next().val(txt);
            };
            reader.readAsText(certFileInput.files[0]);
        }
    }
};

/**
 * Validates policy profile operations for the windows platform.
 *
 * This function will be invoked from the relevant cdmf unit at the time of policy creation.
 *
 * @returns {boolean} whether validation is successful.
 */
var validatePolicyProfile = function () {
    var validationStatusArray = [];
    var validationStatus;
    var operation;

    // starting validation process and updating validationStatus
    if (configuredOperations.length == 0) {
        // updating validationStatus
        validationStatus = {
            "error": true,
            "mainErrorMsg": "You cannot continue. Zero configured features."
        };
        // updating validationStatusArray with validationStatus
        validationStatusArray.push(validationStatus);
    } else {
        // validating each and every configured Operation
        // Validating PASSCODE_POLICY
        if ($.inArray(androidOperationConstants["PASSCODE_POLICY_OPERATION_CODE"], configuredOperations) != -1) {
            // if PASSCODE_POLICY is configured
            operation = androidOperationConstants["PASSCODE_POLICY_OPERATION"];
            // initializing continueToCheckNextInputs to true
            var continueToCheckNextInputs = true;

            // validating first input: passcodePolicyMaxPasscodeAgeInDays
            var passcodePolicyMaxPasscodeAgeInDays = $("input#passcode-policy-max-passcode-age-in-days").val();
            if (passcodePolicyMaxPasscodeAgeInDays) {
                if (!$.isNumeric(passcodePolicyMaxPasscodeAgeInDays)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Provided passcode age is not a number.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                } else if (!inputIsValidAgainstRange(passcodePolicyMaxPasscodeAgeInDays, 1, 730)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Provided passcode age is not with in the range of 1-to-730.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
            }

            // validating second and last input: passcodePolicyPasscodeHistory
            if (continueToCheckNextInputs) {
                var passcodePolicyPasscodeHistory = $("input#passcode-policy-passcode-history").val();
                if (passcodePolicyPasscodeHistory) {
                    if (!$.isNumeric(passcodePolicyPasscodeHistory)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Provided passcode history is not a number.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    } else if (!inputIsValidAgainstRange(passcodePolicyPasscodeHistory, 1, 50)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Provided passcode history is not with in the range of 1-to-50.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    }
                }
            }

            // at-last, if the value of continueToCheckNextInputs is still true
            // this means that no error is found
            if (continueToCheckNextInputs) {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }

            // updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }
        // Validating CAMERA
        if ($.inArray(androidOperationConstants["CAMERA_OPERATION_CODE"], configuredOperations) != -1) {
            // if CAMERA is configured
            operation = androidOperationConstants["CAMERA_OPERATION"];
            // updating validationStatus
            validationStatus = {
                "error": false,
                "okFeature": operation
            };
            // updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }

        // Validating COSU PROFILE CONFIGURATION
        if ($.inArray(androidOperationConstants["COSU_PROFILE_CONFIGURATION_OPERATION_CODE"], configuredOperations) != -1) {

            operation = androidOperationConstants["COSU_PROFILE_CONFIGURATION_OPERATION"];
            var continueToCheckNextInputs = true;

            var isDeviceGlobalConfigChecked = $("input#cosu-profile-device-global-config").is(":checked");
            var isDeviceRestrictOperationChecked = $("input#cosu-profile-device-restrict-operation-time").is(":checked");

            if (isDeviceGlobalConfigChecked === false && isDeviceRestrictOperationChecked === false) {
                validationStatus = {
                    "error": true,
                    "subErrorMsg": "COSU Profile Configuration is empty.",
                    "erroneousFeature": operation
                };
                continueToCheckNextInputs = false;
            }

            if (continueToCheckNextInputs) {
                var backgroundImage = $("input#cosu-global-config-kiosk-background-image").val();
                if (backgroundImage && !(backgroundImage.endsWith("jpg") || backgroundImage.endsWith("jpeg")
                                || backgroundImage.endsWith("png"))) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Launcher background image file types are jpg, jpeg and png",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                }
            }

            if (continueToCheckNextInputs) {
                var kioskLogo = $("input#cosu-global-config-kiosk-logo-image").val();
                if (kioskLogo && !(kioskLogo.endsWith("jpg") || kioskLogo.endsWith("jpeg")
                                || kioskLogo.endsWith("png"))) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Company log to display file types are jpg, jpeg and png",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                }
            }

            if (continueToCheckNextInputs) {
                var idleMediaUrl = $("input#cosu-global-config-idle-media-url").val();
                if (idleMediaUrl && !(idleMediaUrl.endsWith("jpg") || idleMediaUrl.endsWith("jpeg")
                                || idleMediaUrl.endsWith("png") || idleMediaUrl.endsWith("mp4")
                                || idleMediaUrl.endsWith("3gp") || idleMediaUrl.endsWith("wmv")
                                || idleMediaUrl.endsWith("mkv"))) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Idle media url file types are .jpg, .png, .jpeg, .mp4, .3gp, .wmv, .mkv",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                }
            }

            if (continueToCheckNextInputs) {
                var idleTimeout = $("input#cosu-browser-property-idle-timeout").val();
                if (idleTimeout && (!$.isNumeric(idleTimeout) || idleTimeout < 0)) {
                            validationStatus = {
                                "error": true,
                                "subErrorMsg": "Idle timeout must be a positive whole number",
                                "erroneousFeature": operation
                            };
                            continueToCheckNextInputs = false;
                }
            }

            if (continueToCheckNextInputs) {
                var textZoom = $("input#cosu-browser-property-text-zoom").val();
                if (textZoom && (!$.isNumeric(textZoom) || textZoom < 0)) {
                            validationStatus = {
                                "error": true,
                                "subErrorMsg": "Text zoom must be a positive whole number",
                                "erroneousFeature": operation
                            };
                            continueToCheckNextInputs = false;
                }
            }

            if (continueToCheckNextInputs) {
                var defaultFontSize = $("input#cosu-browser-property-default-font-size").val();
                if (defaultFontSize) {
                    if (!$.isNumeric(defaultFontSize) || !inputIsValidAgainstRange(defaultFontSize, 0, 72)) {
                                validationStatus = {
                                    "error": true,
                                    "subErrorMsg": "Default font size is a number between 0 and 72",
                                    "erroneousFeature": operation
                                };
                                continueToCheckNextInputs = false;
                    }
                 }
            }

            if (continueToCheckNextInputs) {
                var isSingleAppMode = $("input#cosu-global-config-is-single-application-mode").is(":checked");
                if (isSingleAppMode === true) {
                    var enrollmentAppInstallAppList = "div#install-app-enrollment .child-input";
                    if ($(enrollmentAppInstallAppList).length === 0) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "No application has been selected in Enrollment Application Install " +
                                "config to run on Single Application Mode.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    }
                }
            }

            if (continueToCheckNextInputs) {
                var isMultiUser = $("input#cosu-global-config-is-multi-user-device").is(":checked");
                if (isMultiUser === true) {
                    var primaryUserApps = $("input#cosu-user-app-config-primary-user").val();
                    if (!primaryUserApps) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Primary user apps are not configured.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    }
                    if (continueToCheckNextInputs) {
                        var isEmptyInputValue = false;
                        var cosuUserAppConfig = $("#cosu-user-app-config .child-input");
                        if ($(cosuUserAppConfig).length === 0) {
                            validationStatus = {
                                "error": true,
                                "subErrorMsg": "Users and apps are not configured for multi users.",
                                "erroneousFeature": operation
                            };
                            continueToCheckNextInputs = false;
                        } else {
                            var childInputCount = 0;
                            var userInputArray = [];
                            var appInputArray = [];
                            $(cosuUserAppConfig).each(function () {
                                childInputCount++;
                                var childInputValue = $(this).val();
                                if (!childInputValue) {
                                    isEmptyInputValue = true;
                                    return false;
                                }
                                if (childInputCount % 2 === 0) {
                                    appInputArray.push(childInputValue);
                                } else {
                                    userInputArray.push(childInputValue);
                                }
                            });
                            var uniqueUserInputArray = userInputArray.filter(function (value, index, self) {
                                return self.indexOf(value) === index;
                            });
                            if (userInputArray.length !== uniqueUserInputArray.length) {
                                validationStatus = {
                                    "error": true,
                                    "subErrorMsg": "Duplicate values exist for username in multi user app configuration.",
                                    "erroneousFeature": operation
                                };
                                continueToCheckNextInputs = false;
                            }
                        }
                        if (isEmptyInputValue === true) {
                            validationStatus = {
                                "error": true,
                                "subErrorMsg": "One or more multi user app configurations are empty.",
                                "erroneousFeature": operation
                            };
                            continueToCheckNextInputs = false;
                        }
                    }
                }
            }

           if (continueToCheckNextInputs) {
               validationStatus = {
                    "error": false,
                    "okFeature": operation
               };
           }
           validationStatusArray.push(validationStatus);
        }

        // Validating ENCRYPT_STORAGE
        if ($.inArray(androidOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"], configuredOperations) != -1) {
            // if ENCRYPT_STORAGE is configured
            operation = androidOperationConstants["ENCRYPT_STORAGE_OPERATION"];
            // updating validationStatus
            validationStatus = {
                "error": false,
                "okFeature": operation
            };
            // updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }
        // Validating WIFI
        if ($.inArray(androidOperationConstants["WIFI_OPERATION_CODE"], configuredOperations) != -1) {
            // if WIFI is configured
            operation = androidOperationConstants["WIFI_OPERATION"];
            // initializing continueToCheckNextInputs to true
            continueToCheckNextInputs = true;

            var wifiSSID = $("input#wifi-ssid").val();
            if (!wifiSSID) {
                validationStatus = {
                    "error": true,
                    "subErrorMsg": "WIFI SSID is not given. You cannot proceed.",
                    "erroneousFeature": operation
                };
                continueToCheckNextInputs = false;
            }
            // For the secure wifi types, it is required to have a password
            var wifiTypeUIElement = $("#wifi-type");
            var wifiType = wifiTypeUIElement.find("option:selected").val();
            if (wifiTypeUIElement.is("input:checkbox")) {
                wifiType = wifiTypeUIElement.is(":checked").toString();
            }
            if (wifiType != "none") {
                if (!$("#wifi-password").val()) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Password is required for the wifi security type " + wifiType + ". " +
                        "Please provide a password to proceed.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
            }
            // at-last, if the value of continueToCheckNextInputs is still true
            // this means that no error is found
            if (continueToCheckNextInputs) {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }

            // updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }

        // Validating PROXY
        if ($.inArray(androidOperationConstants["GLOBAL_PROXY_OPERATION_CODE"], configuredOperations) !== -1) {
            // if PROXY is configured
            operation = androidOperationConstants["GLOBAL_PROXY_OPERATION"];
            // initializing continueToCheckNextInputs to true
            continueToCheckNextInputs = true;

            if ($("input#manual-proxy-configuration-radio-button").is(":checked")) {
                var proxyHost = $("input#proxy-host").val();
                var proxyPort = $("input#proxy-port").val();
                if (!proxyHost) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Proxy server host name is required.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }

                if (!proxyPort) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Proxy server port is required.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                } else if (!$.isNumeric(proxyPort)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Proxy server port requires a number input.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                } else if (!inputIsValidAgainstRange(proxyPort, 0, 65535)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Proxy server port is not within the range of valid port numbers.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
            } else if ($("input#auto-proxy-configuration-radio-button").is(":checked")) {
                var pacFileUrl = $("input#proxy-pac-url").val();
                if (!pacFileUrl) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Proxy pac file URL is required for proxy auto config.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
            }

            // at-last, if the value of continueToCheckNextInputs is still true
            // this means that no error is found
            if (continueToCheckNextInputs) {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }

            // updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }

        if ($.inArray(androidOperationConstants["VPN_OPERATION_CODE"], configuredOperations) != -1) {
            operation = androidOperationConstants["VPN_OPERATION"];
            // initializing continueToCheckNextInputs to true
            continueToCheckNextInputs = true;
            let openVpnConfigEnabled = document.getElementById('vpn-body').classList.contains('in');
            if (openVpnConfigEnabled) {
                var openvpnConfig = $("input#openvpn-config").val();
                if (!openvpnConfig || openvpnConfig === '') {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "ovpn config file is required. You cannot proceed.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
            }

            if (validationStatus) {
                validationStatusArray.push(validationStatus);
            }

            let alwaysOnVpnEnabled = document.getElementById('always-on-vpn-body').classList.contains('in');
            if (alwaysOnVpnEnabled) {
                var alwaysOnConfig = $("input#vpn-client-app").val();
                if (!alwaysOnConfig || alwaysOnConfig === '') {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Add a valid package name for the VPN client",
                        "erroneousFeature": "always-on-vpn"
                    };
                    continueToCheckNextInputs = false;
                }
            }
            // at-last, if the value of continueToCheckNextInputs is still true
            // this means that no error is found
            if (continueToCheckNextInputs) {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }

            // updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }
        if ($.inArray(androidOperationConstants["APPLICATION_OPERATION_CODE"], configuredOperations) != -1) {
            //If application restriction configured
            operation = androidOperationConstants["APPLICATION_OPERATION"];
            // Initializing continueToCheckNextInputs to true
            continueToCheckNextInputs = true;

            var appRestrictionType = $("#app-restriction-type").val();

            var restrictedApplicationsGridChildInputs = "div#restricted-applications .child-input";

            if (!appRestrictionType) {
                validationStatus = {
                    "error": true,
                    "subErrorMsg": "Applications restriction type is not provided.",
                    "erroneousFeature": operation
                };
                continueToCheckNextInputs = false;
            }

            if (continueToCheckNextInputs) {
                if ($(restrictedApplicationsGridChildInputs).length == 0) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Applications are not provided in application restriction list.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
                else {
                    childInputCount = 0;
                    childInputArray = [];
                    emptyChildInputCount = 0;
                    duplicatesExist = false;
                    // Looping through each child input
                    $(restrictedApplicationsGridChildInputs).each(function () {
                        childInputCount++;
                        if (childInputCount % 2 == 0) {
                            // If child input is of second column
                            childInput = $(this).val();
                            childInputArray.push(childInput);
                            // Updating emptyChildInputCount
                            if (!childInput) {
                                // If child input field is empty
                                emptyChildInputCount++;
                            }
                        }
                    });
                    // Checking for duplicates
                    initialChildInputArrayLength = childInputArray.length;
                    if (emptyChildInputCount == 0 && initialChildInputArrayLength > 1) {
                        for (m = 0; m < (initialChildInputArrayLength - 1); m++) {
                            poppedChildInput = childInputArray.pop();
                            for (n = 0; n < childInputArray.length; n++) {
                                if (poppedChildInput == childInputArray[n]) {
                                    duplicatesExist = true;
                                    break;
                                }
                            }
                            if (duplicatesExist) {
                                break;
                            }
                        }
                    }
                    // Updating validationStatus
                    if (emptyChildInputCount > 0) {
                        // If empty child inputs are present
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "One or more package names of " +
                            "applications are empty.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    } else if (duplicatesExist) {
                        // If duplicate input is present
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Duplicate values exist with " +
                            "for package names.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    }

                }
            }

            if (continueToCheckNextInputs) {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }

            // Updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }
        if ($.inArray(androidOperationConstants["KIOSK_APPS_CODE"], configuredOperations) != -1) {
            //If COSU whitelisting applications configured
            operation = androidOperationConstants["KIOSK_APPS"];
            // Initializing continueToCheckNextInputs to true
            continueToCheckNextInputs = true;
            var whitelistedApplicationsGridChildInputs = "div#cosu-whitelisted-applications .child-input";
            if (continueToCheckNextInputs) {
                if ($(whitelistedApplicationsGridChildInputs).length == 0) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Applications are not provided in application whitelist list.",
                        "erroneousFeature": operation
                    };
                    continueToCheckNextInputs = false;
                }
                else {
                    childInputCount = 0;
                    childInputArray = [];
                    emptyChildInputCount = 0;
                    duplicatesExist = false;
                    // Looping through each child input
                    $(whitelistedApplicationsGridChildInputs).each(function () {
                        childInputCount++;
                        if (childInputCount % 2 == 0) {
                            // If child input is of second column
                            childInput = $(this).val();
                            childInputArray.push(childInput);
                            // Updating emptyChildInputCount
                            if (!childInput) {
                                // If child input field is empty
                                emptyChildInputCount++;
                            }
                        }
                    });
                    // Checking for duplicates
                    initialChildInputArrayLength = childInputArray.length;
                    if (emptyChildInputCount == 0 && initialChildInputArrayLength > 1) {
                        for (m = 0; m < (initialChildInputArrayLength - 1); m++) {
                            poppedChildInput = childInputArray.pop();
                            for (n = 0; n < childInputArray.length; n++) {
                                if (poppedChildInput == childInputArray[n]) {
                                    duplicatesExist = true;
                                    break;
                                }
                            }
                            if (duplicatesExist) {
                                break;
                            }
                        }
                    }
                    // Updating validationStatus
                    if (emptyChildInputCount > 0) {
                        // If empty child inputs are present
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "One or more package names of " +
                            "applications are empty.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    } else if (duplicatesExist) {
                        // If duplicate input is present
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Duplicate values exist with " +
                            "for package names.",
                            "erroneousFeature": operation
                        };
                        continueToCheckNextInputs = false;
                    }
                }
            }
            if (continueToCheckNextInputs) {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }
            // Updating validationStatusArray with validationStatus
            validationStatusArray.push(validationStatus);
        }
        if ($.inArray(androidOperationConstants["ENROLLMENT_APP_INSTALL_CODE"], configuredOperations) != -1) {
            //If enrollment app install configured
            operation = androidOperationConstants["ENROLLMENT_APP_INSTALL"];
            var enrollmentAppInstallAppList = "div#install-app-enrollment .child-input";
            if($(enrollmentAppInstallAppList).length === 0) {
                validationStatus = {
                    "error": true,
                    "subErrorMsg": "Applications are not selected to be installed during device enrollment.",
                    "erroneousFeature": operation
                };
            } else {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
            }
            validationStatusArray.push(validationStatus);
        }
        if ($.inArray(androidOperationConstants["CERTIFICATE_INSTALL"], configuredOperations) != -1) {
            //If enrollment app install configured
            let isErrorsFound = false;
            operation = androidOperationConstants["CERTIFICATE_INSTALL"];
            var certList = $("#cert-list").find(".child-input");
            for (let j = 0; j < certList.length; j++) {
                if ($(certList[j]).val().trim() === "") {
                    isErrorsFound = true;
                    break;
                }
            }
            if (isErrorsFound) {
                validationStatus = {
                    "error": true,
                    "subErrorMsg": "Certificates are not selected to be installed.",
                    "erroneousFeature": operation
                };
                validationStatusArray.push(validationStatus);
            } else {
                validationStatus = {
                    "error": false,
                    "okFeature": operation
                };
                validationStatusArray.push(validationStatus);
            }
        }
    }
    // ending validation process

    // start taking specific notifying actions upon validation
    var wizardIsToBeContinued;
    var errorCount = 0;
    var mainErrorMsgWrapper, mainErrorMsg,
        subErrorMsgWrapper, subErrorMsg, subErrorIcon, subOkIcon, featureConfiguredIcon;
    var i;
    for (i = 0; i < validationStatusArray.length; i++) {
        validationStatus = validationStatusArray[i];
        if (validationStatus["error"]) {
            errorCount++;
            if (validationStatus["mainErrorMsg"]) {
                mainErrorMsgWrapper = "#policy-profile-main-error-msg";
                mainErrorMsg = mainErrorMsgWrapper + " span";
                $(mainErrorMsg).text(validationStatus["mainErrorMsg"]);
                $(mainErrorMsgWrapper).removeClass("hidden");
            } else if (validationStatus["subErrorMsg"]) {
                subErrorMsgWrapper = "#" + validationStatus["erroneousFeature"] + "-feature-error-msg";
                subErrorMsg = subErrorMsgWrapper + " span";
                subErrorIcon = "#" + validationStatus["erroneousFeature"] + "-error";
                subOkIcon = "#" + validationStatus["erroneousFeature"] + "-ok";
                featureConfiguredIcon = "#" + validationStatus["erroneousFeature"] + "-configured";
                // hiding featureConfiguredState as the first step
                if (!$(featureConfiguredIcon).hasClass("hidden")) {
                    $(featureConfiguredIcon).addClass("hidden");
                }
                // updating error state and corresponding messages
                $(subErrorMsg).text(validationStatus["subErrorMsg"]);
                if ($(subErrorMsgWrapper).hasClass("hidden")) {
                    $(subErrorMsgWrapper).removeClass("hidden");
                }
                if (!$(subOkIcon).hasClass("hidden")) {
                    $(subOkIcon).addClass("hidden");
                }
                if ($(subErrorIcon).hasClass("hidden")) {
                    $(subErrorIcon).removeClass("hidden");
                }
            }
        } else {
            if (validationStatus["okFeature"]) {
                subErrorMsgWrapper = "#" + validationStatus["okFeature"] + "-feature-error-msg";
                subErrorIcon = "#" + validationStatus["okFeature"] + "-error";
                subOkIcon = "#" + validationStatus["okFeature"] + "-ok";
                featureConfiguredIcon = "#" + validationStatus["okFeature"] + "-configured";
                // hiding featureConfiguredState as the first step
                if (!$(featureConfiguredIcon).hasClass("hidden")) {
                    $(featureConfiguredIcon).addClass("hidden");
                }
                // updating success state and corresponding messages
                if (!$(subErrorMsgWrapper).hasClass("hidden")) {
                    $(subErrorMsgWrapper).addClass("hidden");
                }
                if (!$(subErrorIcon).hasClass("hidden")) {
                    $(subErrorIcon).addClass("hidden");
                }
                if ($(subOkIcon).hasClass("hidden")) {
                    $(subOkIcon).removeClass("hidden");
                }
            }
        }
    }

    wizardIsToBeContinued = (errorCount == 0);
    return wizardIsToBeContinued;
};

/**
 * Generates policy profile feature list which will be saved with the profile.
 *
 * This function will be invoked from the relevant cdmf unit at the time of policy creation.
 *
 * @returns {Array} profile payloads
 */
var generateProfileFeaturesList = function () {
    var profilePayloads = [];
    // traverses key by key in policy["profile"]
    var key;
    for (key in policy["profile"]) {
        if (policy["profile"].hasOwnProperty(key)) {
            profilePayloads.push({
                "featureCode": key,
                "deviceType": policy["platform"],
                "content": policy["profile"][key]
            });
        }
    }

    return profilePayloads;
};

/**
 * Generates policy profile object which will be saved with the profile.
 * 
 * This function will be invoked from the relevant cdmf unit at the time of policy creation.
 * 
 * @returns {object} generated profile.
 */
var generatePolicyProfile = function () {
    return androidOperationModule.generateProfile(configuredOperations);
};

/**
 * Resets policy profile configurations.
 */
var resetPolicyProfile = function () {
  configuredOperations = [];
};

// Start of HTML embedded invoke methods
var showAdvanceOperation = function (operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    var hiddenOperation = ".wr-hidden-operations-content > div";
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
};


/**
 * This method will display appropriate fields based on wifi type
 * @param select
 */
var changeAndroidWifiPolicy = function (select) {
    slideDownPaneAgainstValueSet(select, 'control-wifi-password', ['wep', 'wpa', '802eap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-eap', ['802eap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-phase2', ['802eap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-identity', ['802eap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-anoidentity', ['802eap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-cacert', ['802eap']);
};

/**
 * This method will display appropriate fields based on wifi EAP type
 * @param select
 * @param superSelect
 */
var changeAndroidWifiPolicyEAP = function (select, superSelect) {
    slideDownPaneAgainstValueSet(select, 'control-wifi-password', ['peap', 'ttls', 'pwd', 'fast', 'leap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-phase2', ['peap', 'ttls', 'fast']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-provisioning', ['fast']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-identity', ['peap', 'tls', 'ttls', 'pwd', 'fast', 'leap']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-anoidentity', ['peap', 'ttls']);
    slideDownPaneAgainstValueSet(select, 'control-wifi-cacert', ['peap', 'tls', 'ttls']);
    if (superSelect.value != '802eap') {
        changeAndroidWifiPolicy(superSelect);
    }
};

/**
 * Pass a div Id and a check box to view or hide div content based on checkbox value
 */
var changeDivVisibility = function (divId, checkbox) {
    if (checkbox.checked) {
        document.getElementById(divId).style.display= "block";
    } else {
        document.getElementById(divId).style.display= "none";
        $("#" + divId + " input").each(
            function () {
                if ($(this).is("input:text")) {
                    $(this).val(this.defaultValue);
                } else if ($(this).is("input:checkbox")) {
                    $(this).prop("checked", this.defaultChecked);
                }
            }
        );
        $("#" + divId + " select").each(
            function () {
                $(this).val($(this).data("default"));
            }
        );
        $("#" + divId + " .grouped-array-input").each(
            function () {
                var gridInputs = $(this).find("[data-add-form-clone]");
                if (gridInputs.length > 0) {
                    gridInputs.remove();
                }
                var helpTexts = $(this).find("[data-help-text=add-form]");
                if (helpTexts.length > 0) {
                    helpTexts.show();
                }
            }
        );
        $("#" + divId + " .collapse-config").each(
            function() {
                this.style.display = "none";
            }
        );
    }
};

/**
 * Method to slide down a provided pane upon provided value set.
 *
 * @param selectElement Select HTML Element to consider
 * @param paneID HTML ID of div element to slide down
 * @param valueSet Applicable Value Set
 */
var slideDownPaneAgainstValueSet = function (selectElement, paneID, valueSet) {
    var selectedValueOnChange = $(selectElement).find("option:selected").val();
    if ($(selectElement).is("input:checkbox")) {
        selectedValueOnChange = $(selectElement).is(":checked").toString();
    }

    var i, slideDownVotes = 0;
    for (i = 0; i < valueSet.length; i++) {
        if (selectedValueOnChange == valueSet[i]) {
            slideDownVotes++;
        }
    }
    var paneSelector = "#" + paneID;
    if (slideDownVotes > 0) {
        if (!$(paneSelector).hasClass("expanded")) {
            $(paneSelector).addClass("expanded");
        }
        $(paneSelector).slideDown();
    } else {
        if ($(paneSelector).hasClass("expanded")) {
            $(paneSelector).removeClass("expanded");
        }
        $(paneSelector).slideUp();
        /** now follows the code to reinitialize all inputs of the slidable pane */
        // reinitializing input fields into the defaults
        $(paneSelector + " input").each(
            function () {
                if ($(this).is("input:text")) {
                    $(this).val($(this).data("default"));
                } else if ($(this).is("input:password")) {
                    $(this).val("");
                } else if ($(this).is("input:checkbox")) {
                    $(this).prop("checked", $(this).data("default"));
                    // if this checkbox is the parent input of a grouped-input
                    if ($(this).hasClass("parent-input")) {
                        var groupedInput = $(this).parent().parent().parent();
                        updateGroupedInputVisibility(groupedInput);
                    }
                }
            }
        );
        // reinitializing select fields into the defaults
        $(paneSelector + " select").each(
            function () {
                var defaultOption = $(this).data("default");
                $("option:eq(" + defaultOption + ")", this).prop("selected", "selected");
            }
        );
        // collapsing expanded-panes (upon the selection of html-select-options) if any
        $(paneSelector + " .expanded").each(
            function () {
                if ($(this).hasClass("expanded")) {
                    $(this).removeClass("expanded");
                }
                $(this).slideUp();
            }
        );
        // removing all entries of grid-input elements if exist
        $(paneSelector + " .grouped-array-input").each(
            function () {
                var gridInputs = $(this).find("[data-add-form-clone]");
                if (gridInputs.length > 0) {
                    gridInputs.remove();
                }
                var helpTexts = $(this).find("[data-help-text=add-form]");
                if (helpTexts.length > 0) {
                    helpTexts.show();
                }
            }
        );
    }
};

var slideDownPaneAgainstValueSetForRadioButtons = function (selectElement, paneID, valueSet) {
    var selectedValueOnChange = selectElement.value;
    var slideDownVotes = 0;
    for (var i = 0; i < valueSet.length; i++) {
        if (selectedValueOnChange == valueSet[i]) {
            slideDownVotes++;
        }
    }
    var paneSelector = "#" + paneID;
    if (slideDownVotes > 0) {
        $(paneSelector).removeClass("hidden");
    } else {
        $(paneSelector).addClass("hidden");
    }
};

/**
 * Method to switch panes based on the selected radio button.
 *
 * The method will un hide the element with the id (paneIdPrefix + selectElement.value)
 *
 * @param selectElement selected HTML element
 * @param paneIdPrefix  prefix of the id of the pane to un hide.
 * @param valueSet applicable value set
 */
var switchPaneAgainstValueSetForRadioButtons = function (selectElement, paneIdPrefix, valueSet) {
    var selectedValueOnChange = selectElement.value;
    var paneSelector = "#" + paneIdPrefix;
    var pane;
    for (var i = 0; i < valueSet.length; ++i) {
        if (selectedValueOnChange !== valueSet[i]) {
            pane = paneSelector + valueSet[i].toLowerCase();
            if ($(pane).hasClass("expanded")) {
                $(pane).removeClass("expanded");
            }
            $(pane).slideUp();
        } else {
            pane = paneSelector + selectedValueOnChange.toLowerCase();
            if (!$(pane).hasClass("expanded")) {
                $(pane).addClass("expanded");
            }
            $(pane).slideDown();
        }
    }
};

// End of HTML embedded invoke methods


// Start of functions related to grid-input-view

/**
 * Method to set count id to cloned elements.
 * @param {object} addFormContainer
 */
var setId = function (addFormContainer) {
    $(addFormContainer).find("[data-add-form-clone]").each(function (i) {
        $(this).attr("id", $(this).attr("data-add-form-clone").slice(1) + "-" + (i + 1));
        if ($(this).find(".index").length > 0) {
            $(this).find(".index").html(i + 1);
        }
    });
};

/**
 * Method to set count id to cloned elements.
 * @param {object} addFormContainer
 */
var showHideHelpText = function (addFormContainer) {
    var helpText = "[data-help-text=add-form]";
    if ($(addFormContainer).find("[data-add-form-clone]").length > 0) {
        $(addFormContainer).find(helpText).hide();
    } else {
        $(addFormContainer).find(helpText).show();
    }
};

var applyDataTable = function() {
    $("#enrollment-app-install-table").datatables_extended({
        ordering: false,
        lengthMenu: [5, 10, 25, 50, 100]
    });
};

$(document).ready(function () {
    // Maintains an array of configured features of the profile
    var advanceOperations = ".wr-advance-operations";
    $(advanceOperations).on("click", ".wr-input-control.switch", function (event) {
        var operationCode = $(this).parents(".operation-data").data("operation-code");
        var operation = $(this).parents(".operation-data").data("operation");
        var operationDataWrapper = $(this).data("target");
        // prevents event bubbling by figuring out what element it's being called from.
        if (event.target.tagName == "INPUT") {
            var isNonAdvanceOperation = $("input[type='checkbox']", this).hasClass("non-advance-operation");
            if (!isNonAdvanceOperation) {
                var featureConfiguredIcon;
                if ($("input[type='checkbox']", this).is(":checked")) {
                    configuredOperations.push(operationCode);
                    // when a feature is enabled, if "zero-configured-features" msg is available, hide that.
                    var zeroConfiguredOperationsErrorMsg = "#policy-profile-main-error-msg";
                    if (!$(zeroConfiguredOperationsErrorMsg).hasClass("hidden")) {
                        $(zeroConfiguredOperationsErrorMsg).addClass("hidden");
                    }
                    // add configured-state-icon to the feature
                    featureConfiguredIcon = "#" + operation + "-configured";
                    if ($(featureConfiguredIcon).hasClass("hidden")) {
                        $(featureConfiguredIcon).removeClass("hidden");
                    }
                } else {
                    //splicing the array if operation is present.
                    var index = $.inArray(operationCode, configuredOperations);
                    if (index != -1) {
                        configuredOperations.splice(index, 1);
                    }
                    // when a feature is disabled, clearing all its current configured, error or success states
                    var subErrorMsgWrapper = "#" + operation + "-feature-error-msg";
                    var subErrorIcon = "#" + operation + "-error";
                    var subOkIcon = "#" + operation + "-ok";
                    featureConfiguredIcon = "#" + operation + "-configured";

                    if (!$(subErrorMsgWrapper).hasClass("hidden")) {
                        $(subErrorMsgWrapper).addClass("hidden");
                    }
                    if (!$(subErrorIcon).hasClass("hidden")) {
                        $(subErrorIcon).addClass("hidden");
                    }
                    if (!$(subOkIcon).hasClass("hidden")) {
                        $(subOkIcon).addClass("hidden");
                    }
                    if (!$(featureConfiguredIcon).hasClass("hidden")) {
                        $(featureConfiguredIcon).addClass("hidden");
                    }
                    // reinitializing input fields into the defaults
                    $(operationDataWrapper + " input").each(
                        function () {
                            if ($(this).is("input:text")) {
                                $(this).val($(this).data("default"));
                            } else if ($(this).is("input:password")) {
                                $(this).val("");
                            } else if ($(this).is("input:checkbox")) {
                                $(this).prop("checked", $(this).data("default"));
                                // if this checkbox is the parent input of a grouped-input
                                if ($(this).hasClass("parent-input")) {
                                    var groupedInput = $(this).parent().parent().parent();
                                    updateGroupedInputVisibility(groupedInput);
                                }
                            }
                        }
                    );
                    // reinitializing select fields into the defaults
                    $(operationDataWrapper + " select").each(
                        function () {
                            var defaultOption = $(this).data("default");
                            $("option:eq(" + defaultOption + ")", this).prop("selected", "selected");
                        }
                    );
                    // collapsing expanded-panes (upon the selection of html-select-options) if any
                    $(operationDataWrapper + " .expanded").each(
                        function () {
                            if ($(this).hasClass("expanded")) {
                                $(this).removeClass("expanded");
                            }
                            $(this).slideUp();
                        }
                    );
                    // removing all entries of grid-input elements if exist
                    $(operationDataWrapper + " .grouped-array-input").each(
                        function () {
                            var gridInputs = $(this).find("[data-add-form-clone]");
                            if (gridInputs.length > 0) {
                                gridInputs.remove();
                            }
                            var helpTexts = $(this).find("[data-help-text=add-form]");
                            if (helpTexts.length > 0) {
                                helpTexts.show();
                            }
                        }
                    );
                }
            }
        }
    });

    // <start - fixing feature-configuring switch double-click issue>
    $(advanceOperations).on('hidden.bs.collapse', function (event) {
        var collapsedFeatureBody = event.target.id;
        var operation = collapsedFeatureBody.substr(0, collapsedFeatureBody.lastIndexOf("-"));
        var featureConfiguringSwitch = "#" + operation + "-heading input[type=checkbox]";
        var featureConfiguredIcon = "#" + operation + "-configured";
        if ($(featureConfiguringSwitch).prop("checked") == true) {
            $(featureConfiguringSwitch).prop("checked", false);
        }
        if (!$(featureConfiguredIcon).hasClass("hidden")) {
            $(featureConfiguredIcon).addClass("hidden");
        }
    });

    $(advanceOperations).on('shown.bs.collapse', function (event) {
        var expandedFeatureBody = event.target.id;
        var operation = expandedFeatureBody.substr(0, expandedFeatureBody.lastIndexOf("-"));
        var featureConfiguringSwitch = "#" + operation + "-heading input[type=checkbox]";
        var featureConfiguredIcon = "#" + operation + "-configured";
        if ($(featureConfiguringSwitch).prop("checked") == false) {
            $(featureConfiguringSwitch).prop("checked", true);
        }
        if ($(featureConfiguredIcon).hasClass("hidden")) {
            $(featureConfiguredIcon).removeClass("hidden");
        }
    });
    // <end - fixing feature-configuring switch double-click issue>

    // adding support for cloning multiple profiles per feature with cloneable class definitions
    $(advanceOperations).on("click", ".multi-view.add.enabled", function () {
        // get a copy of .cloneable and create new .cloned div element
        var cloned = "<div class='cloned'><hr>" + $(".cloneable", $(this).parent().parent()).html() + "</div>";
        // append newly created .cloned div element to panel-body
        $(this).parent().parent().append(cloned);
        // enable remove action of newly cloned div element
        $(".cloned", $(this).parent().parent()).each(
            function () {
                if ($(".multi-view.remove", this).hasClass("disabled")) {
                    $(".multi-view.remove", this).removeClass("disabled");
                }
                if (!$(".multi-view.remove", this).hasClass("enabled")) {
                    $(".multi-view.remove", this).addClass("enabled");
                }
            }
        );
    });

    $(advanceOperations).on("click", ".multi-view.remove.enabled", function () {
        $(this).parent().remove();
    });

    // enabling or disabling grouped-input based on the status of a parent check-box
    $(advanceOperations).on("click", ".grouped-input", function () {
        updateGroupedInputVisibility(this);
    });

    // add form entry click function for grid inputs
    $(advanceOperations).on("click", "[data-click-event=add-form]", function () {
        var addFormContainer = $("[data-add-form-container=" + $(this).attr("href") + "]");
        var clonedForm = $("[data-add-form=" + $(this).attr("href") + "]").clone().find("[data-add-form-element=clone]")
            .attr("data-add-form-clone", $(this).attr("href"));

        // adding class .child-input to capture text-input-array-values
        $("input, select", clonedForm).addClass("child-input");

        $(addFormContainer).append(clonedForm);
        setId(addFormContainer);
        showHideHelpText(addFormContainer);
    });

    // remove form entry click function for grid inputs
    $(advanceOperations).on("click", "[data-click-event=remove-form]", function () {
        var addFormContainer = $("[data-add-form-container=" + $(this).attr("href") + "]");

        $(this).closest("[data-add-form-element=clone]").remove();
        setId(addFormContainer);
        showHideHelpText(addFormContainer);
    });

    // add app entry for enrollment-app-install
    $(advanceOperations).on("click", "[data-click-event=add-enrollment-app]", function () {
        $(this).attr("hidden", true);
        $(this).siblings("a").removeAttr("hidden");
        $(this).parent().parent().find("input").each(function () {
            $(this).addClass("child-input");
        });
    });

    // remove app entry for enrollment-app-install
    $(advanceOperations).on("click", "[data-click-event=remove-enrollment-app]", function () {
        $(this).attr("hidden", true);
        $(this).siblings("a").removeAttr("hidden");
        $(this).parent().parent().find("input").each(function () {
            $(this).removeClass("child-input");
        });
    });
});
