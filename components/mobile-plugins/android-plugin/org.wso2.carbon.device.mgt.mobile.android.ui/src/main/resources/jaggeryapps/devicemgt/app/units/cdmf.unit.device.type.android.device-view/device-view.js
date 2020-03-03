/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */

function onRequest(context) {
// var log = new Log("device-view.js");
    var deviceType = context["uriParams"]["deviceType"];
    var deviceId = request.getParameter("id");
    var owner = request.getParameter("owner");
    var ownership = request.getParameter("ownership");
    var deviceViewData = {};
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];
    var carbonServer = require("carbon").server;
    var constants = require("/app/modules/constants.js");

    if (deviceType && deviceId) {
        var deviceModule = require("/app/modules/business-controllers/device.js")["deviceModule"];
        var response = deviceModule.viewDevice(deviceType, deviceId, owner, ownership);
        if (response["status"] === "success") {
            deviceViewData["deviceFound"] = true;
            deviceViewData["isAuthorized"] = true;

            var filteredDeviceData = response["content"];

            // creating deviceView information model from filtered device data
            var viewModel = {};
            var deviceInfoServiceAPI = devicemgtProps["deviceInfoServiceAPI"];
            var deviceLocationServiceAPI = devicemgtProps["deviceLocationServiceAPI"];

            if (deviceInfoServiceAPI){
              viewModel["deviceInfoServiceAPI"] = deviceInfoServiceAPI.replace("%device-type%", deviceType)
            }
            if (deviceLocationServiceAPI){
              viewModel['deviceLocationServiceAPI'] = deviceLocationServiceAPI.replace("%device-type%", deviceType)
            }
            if (filteredDeviceData["type"]) {
                viewModel["type"] = filteredDeviceData["type"];
                viewModel.isNotWindows = true;
            }
            if (filteredDeviceData["deviceIdentifier"]) {
                viewModel["deviceIdentifier"] = filteredDeviceData["deviceIdentifier"];
            }
            if (filteredDeviceData["name"]) {
                viewModel["name"] = filteredDeviceData["name"];
            }
            if (filteredDeviceData["enrolmentInfo"]) {
                if (filteredDeviceData["enrolmentInfo"]["status"]) {
                    viewModel["status"] = filteredDeviceData["enrolmentInfo"]["status"];
                    viewModel.isActive = false ;
                    viewModel.isNotRemoved = true;
                    if (filteredDeviceData["enrolmentInfo"]["status"]== "ACTIVE") {
                        viewModel.isActive = true ;
                    }
                    if (filteredDeviceData["enrolmentInfo"]["status"]== "REMOVED") {
                        viewModel.isNotRemoved = false ;
                    }
                }
                if (filteredDeviceData["enrolmentInfo"]["owner"]) {
                    viewModel["owner"] = filteredDeviceData["enrolmentInfo"]["owner"];
                }
                if (filteredDeviceData["enrolmentInfo"]["ownership"]) {
                    viewModel["ownership"] = filteredDeviceData["enrolmentInfo"]["ownership"];
                }
            }
            var extensiveDeviceInfo = {};
            if (filteredDeviceData["initialDeviceInfo"]) {
                viewModel["deviceInfoAvailable"] = true;
                if (filteredDeviceData["initialDeviceInfo"]["IMEI"]) {
                    viewModel["imei"] = filteredDeviceData["initialDeviceInfo"]["IMEI"];
                    extensiveDeviceInfo["IMEI"] = filteredDeviceData["initialDeviceInfo"]["IMEI"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["IMSI"]) {
                    extensiveDeviceInfo["IMSI"] = filteredDeviceData["initialDeviceInfo"]["IMSI"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["OS_VERSION"]) {
                    extensiveDeviceInfo["OS Version"] = filteredDeviceData["initialDeviceInfo"]["OS_VERSION"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["DEVICE_MODEL"]) {
                    extensiveDeviceInfo["Model"] = filteredDeviceData["initialDeviceInfo"]["DEVICE_MODEL"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["VENDOR"]) {
                    extensiveDeviceInfo["Vendor"] = filteredDeviceData["initialDeviceInfo"]["VENDOR"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["SERIAL"]) {
                    extensiveDeviceInfo["Serial Number"] = filteredDeviceData["initialDeviceInfo"]["SERIAL"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["MAC_ADDRESS"]) {
                    extensiveDeviceInfo["MAC Address"] = filteredDeviceData["initialDeviceInfo"]["MAC_ADDRESS"];
                }
                if (filteredDeviceData["initialDeviceInfo"]["OS_BUILD_DATE"]) {
                    extensiveDeviceInfo["OS Build Date"] = filteredDeviceData["initialDeviceInfo"]["OS_BUILD_DATE"];
                }
                if (!filteredDeviceData["latestDeviceInfo"]) {
                    if (filteredDeviceData["initialDeviceInfo"]["OS_BUILD_DATE"]) {
                        if (filteredDeviceData["initialDeviceInfo"]["OS_BUILD_DATE"] != "0") {
                            viewModel["osBuildDate"] = new Date(filteredDeviceData["initialDeviceInfo"]["OS_BUILD_DATE"] * 1000);
                        }
                    }
                    if (filteredDeviceData["initialDeviceInfo"]["LATITUDE"] && filteredDeviceData["initialDeviceInfo"]["LONGITUDE"]) {
                        viewModel["location"] = {};
                        viewModel["location"]["latitude"] = filteredDeviceData["initialDeviceInfo"]["LATITUDE"];
                        viewModel["location"]["longitude"] = filteredDeviceData["initialDeviceInfo"]["LONGITUDE"];
                    }
                    if (filteredDeviceData["initialDeviceInfo"]["VENDOR"] && filteredDeviceData["initialDeviceInfo"]["DEVICE_MODEL"]) {
                        viewModel["vendor"] = filteredDeviceData["initialDeviceInfo"]["VENDOR"];
                        viewModel["model"] = filteredDeviceData["initialDeviceInfo"]["DEVICE_MODEL"];
                    }
                    if (filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]) {
                            viewModel["BatteryLevel"] = {};
                            viewModel["BatteryLevel"]["value"] = filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["BATTERY_LEVEL"];

                            viewModel["internalMemory"] = {};
                            viewModel["internalMemory"]["total"] = replaceNaNVal(Math.
                                round(filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["INTERNAL_TOTAL_MEMORY"] * 100) / 100);
                            if (filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["INTERNAL_TOTAL_MEMORY"] != 0) {
                                viewModel["internalMemory"]["usage"] = replaceNaNVal(Math.round((filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["INTERNAL_TOTAL_MEMORY"] -
                                    filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["INTERNAL_AVAILABLE_MEMORY"]) * 100) / 100);
                            } else {
                                viewModel["internalMemory"]["usage"] = 0;
                            }
                        if (filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["OPERATOR"]) {
                            extensiveDeviceInfo["Operator"] = filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["OPERATOR"];
                        }
                        if (filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["PHONE_NUMBER"]) {
                            extensiveDeviceInfo["Phone Number"] = filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["PHONE_NUMBER"];
                        }

                            viewModel["externalMemory"] = {};
                            viewModel["externalMemory"]["total"] = replaceNaNVal(Math.
                                round(filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["EXTERNAL_TOTAL_MEMORY"] * 100) / 100);
                            if (filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["EXTERNAL_TOTAL_MEMORY"] != 0) {
                                viewModel["externalMemory"]["usage"] = replaceNaNVal(Math.
                                    round((filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["EXTERNAL_TOTAL_MEMORY"] -
                                        filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["EXTERNAL_AVAILABLE_MEMORY"])
                                        / filteredDeviceData["initialDeviceInfo"]["DEVICE_INFO"]["EXTERNAL_TOTAL_MEMORY"] * 10000) / 100);
                            } else {
                                viewModel["externalMemory"]["usage"] = 0;
                            }
                    }
                }
            }
            if (filteredDeviceData["latestDeviceInfo"]) {
                viewModel["deviceInfoAvailable"] = true;
                if (filteredDeviceData["latestDeviceInfo"]["osBuildDate"]) {
                    if (filteredDeviceData["latestDeviceInfo"]["osBuildDate"] != "0") {
                        viewModel["osBuildDate"] = new Date(filteredDeviceData["latestDeviceInfo"]["osBuildDate"] * 1000);
                    }
                }
                if (filteredDeviceData["latestDeviceInfo"]["location"] && filteredDeviceData["latestDeviceInfo"]["location"]["latitude"] &&
                    filteredDeviceData["latestDeviceInfo"]["location"]["longitude"]) {
                    viewModel["location"] = {};
                    viewModel["location"]["latitude"] = filteredDeviceData["latestDeviceInfo"]["location"]["latitude"];
                    viewModel["location"]["longitude"] = filteredDeviceData["latestDeviceInfo"]["location"]["longitude"];
                    viewModel["location"]["updatedTime"] = filteredDeviceData["latestDeviceInfo"]["location"]["updatedTime"];
                }
                if (filteredDeviceData["latestDeviceInfo"]["vendor"] && filteredDeviceData["latestDeviceInfo"]["deviceModel"]) {
                    viewModel["vendor"] = filteredDeviceData["latestDeviceInfo"]["vendor"];
                    viewModel["model"] = filteredDeviceData["latestDeviceInfo"]["deviceModel"];
                }
                if (filteredDeviceData["latestDeviceInfo"]["osVersion"]) {
                    extensiveDeviceInfo["OS Version"] = filteredDeviceData["latestDeviceInfo"]["osVersion"];
                }
                if (filteredDeviceData["latestDeviceInfo"]["connectionType"]) {
                    extensiveDeviceInfo["Connection Type"] = filteredDeviceData["latestDeviceInfo"]["connectionType"];
                }
                if (filteredDeviceData["latestDeviceInfo"]["ssid"]) {
                    extensiveDeviceInfo["SSID"] = filteredDeviceData["latestDeviceInfo"]["ssid"];
                }
                if (filteredDeviceData["latestDeviceInfo"]["updatedTime"]) {
                    viewModel["lastUpdatedTime"] = filteredDeviceData["latestDeviceInfo"]["updatedTime"].
                    substr(0, filteredDeviceData["latestDeviceInfo"]["updatedTime"].indexOf("+"));
                }
                viewModel["BatteryLevel"] = {};
                viewModel["BatteryLevel"]["value"] = filteredDeviceData["latestDeviceInfo"]["batteryLevel"];

                viewModel["cpuUsage"] = {};
                viewModel["cpuUsage"]["value"] = filteredDeviceData["latestDeviceInfo"]["cpuUsage"];
                extensiveDeviceInfo["CPU Usage"] = filteredDeviceData["latestDeviceInfo"]["cpuUsage"];

                viewModel["ramUsage"] = {};
                if (filteredDeviceData["latestDeviceInfo"]["totalRAMMemory"] != 0) {
                    viewModel["ramUsage"]["value"] = replaceNaNVal(Math.
                        round((filteredDeviceData["latestDeviceInfo"]["totalRAMMemory"] -
                            filteredDeviceData["latestDeviceInfo"]["availableRAMMemory"])
                            / filteredDeviceData["latestDeviceInfo"]["totalRAMMemory"] * 10000) / 100);
                } else {
                    viewModel["ramUsage"]["value"] = 0;
                }

                viewModel["internalMemory"] = {};
                if (filteredDeviceData["latestDeviceInfo"]["internalTotalMemory"] != 0) {
                    viewModel["internalMemory"]["total"] = replaceNaNVal(Math.round
                    (filteredDeviceData["latestDeviceInfo"]["internalTotalMemory"] * 100) / 100);
                    viewModel["internalMemory"]["usage"] = replaceNaNVal(Math.round
                    (filteredDeviceData["latestDeviceInfo"]["internalAvailableMemory"] * 100) / 100);
                } else {
                    viewModel["internalMemory"]["usage"] = 0;
                }

                viewModel["externalMemory"] = {};
                if (filteredDeviceData["latestDeviceInfo"]["externalTotalMemory"] != 0) {
                    viewModel["externalMemory"]["total"] = replaceNaNVal(Math.round
                    (filteredDeviceData["latestDeviceInfo"]["externalTotalMemory"] * 100) / 100);
                    viewModel["externalMemory"]["usage"] = replaceNaNVal(Math.
                    round(filteredDeviceData["latestDeviceInfo"]["externalAvailableMemory"]* 100)
                    /100);
                } else {
                    viewModel["externalMemory"]["usage"] = 0;
                }
            }
            viewModel["extensiveDeviceInfo"] = extensiveDeviceInfo;
            if (!filteredDeviceData["initialDeviceInfo"] && !filteredDeviceData["latestDeviceInfo"]) {
                viewModel["deviceInfoAvailable"] = false;
            }
            viewModel.locationHistory = stringify(filteredDeviceData["locationHistory"]);
            deviceViewData["device"] = viewModel;
        } else if (response["status"] == "unauthorized") {
            deviceViewData["deviceFound"] = true;
            deviceViewData["isAuthorized"] = false;
        } else if (response["status"] == "notFound") {
            deviceViewData["deviceFound"] = false;
        }

        var remoteSessionEndpoint = devicemgtProps["remoteSessionWSURL"].replace("https", "wss");
        var jwtService = carbonServer.osgiService('org.wso2.carbon.identity.jwt.client.extension.service.JWTClientManagerService');
        var jwtClient = jwtService.getJWTClient();
        var encodedClientKeys = session.get(constants["ENCODED_TENANT_BASED_WEB_SOCKET_CLIENT_CREDENTIALS"]);
        var tokenPair = null;
        var token = "";
        if (encodedClientKeys) {
            var tokenUtil = require("/app/modules/oauth/token-handler-utils.js")["utils"];
            var resp = tokenUtil.decode(encodedClientKeys).split(":");
            if (context.user.domain == "carbon.super") {
                tokenPair = jwtClient.getAccessToken(resp[0], resp[1], context.user.username,"default", {});
            } else {
                tokenPair = jwtClient.getAccessToken(resp[0], resp[1], context.user.username + "@" + context.user.domain,"default", {});
            }
            if (tokenPair) {
                token = tokenPair.accessToken;
            }
        }
        remoteSessionEndpoint = remoteSessionEndpoint + "/remote/session/clients/" + deviceType + "/" + deviceId +
        "?websocketToken=" + token
        deviceViewData["remoteSessionEndpoint"] = remoteSessionEndpoint;
    } else {
        deviceViewData["deviceFound"] = false;
    }

    var autoCompleteParams = [
        {"name" : "deviceId", "value" : deviceId}
    ];

    var userModule = require("/app/modules/business-controllers/user.js")["userModule"];
    var permissions = userModule.getUIPermissions();
    deviceViewData["autoCompleteParams"] = autoCompleteParams;
    deviceViewData["permissions"] = permissions;

    deviceViewData["portalUrl"] = devicemgtProps['portalURL'];
    deviceViewData["isCloud"] = devicemgtProps['isCloud'];
    deviceViewData["anchor"] = encodeURI(JSON.stringify({ "device" : { "id" : deviceId, "type" : deviceType}}));
    return deviceViewData;

    function replaceNaNVal(val){
        if(isNaN(val)){
            return "N/A";
        }
        return val;
    }
}
