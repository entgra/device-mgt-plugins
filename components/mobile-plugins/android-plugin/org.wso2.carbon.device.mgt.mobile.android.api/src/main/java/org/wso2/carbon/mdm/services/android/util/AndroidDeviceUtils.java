/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
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

package org.wso2.carbon.mdm.services.android.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.analytics.api.AnalyticsDataAPIUtil;
import org.wso2.carbon.analytics.dataservice.commons.AnalyticsDataResponse;
import org.wso2.carbon.analytics.dataservice.commons.SearchResultEntry;
import org.wso2.carbon.analytics.datasource.commons.Record;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.device.details.DeviceInfo;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.policy.mgt.ProfileFeature;
import org.wso2.carbon.device.mgt.common.policy.mgt.monitor.ComplianceFeature;
import org.wso2.carbon.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.search.mgt.impl.Utils;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.mdm.services.android.bean.DeviceState;
import org.wso2.carbon.mdm.services.android.bean.ErrorListItem;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.exception.BadRequestException;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.user.api.UserStoreException;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class for holding Android device related util methods.
 */
public class AndroidDeviceUtils {

    private static Log log = LogFactory.getLog(AndroidDeviceUtils.class);

    private AndroidDeviceUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidDeviceIdentifier(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        Device device = AndroidAPIUtils.getDeviceManagementService().
                getDevice(deviceIdentifier, false);
        return !(device == null || device.getDeviceIdentifier() == null ||
                 device.getDeviceIdentifier().isEmpty() ||
                 device.getEnrolmentInfo() == null ||
                 EnrolmentInfo.Status.REMOVED.equals(device.getEnrolmentInfo().getStatus()));
    }

    public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
        DeviceIdentifier identifier = new DeviceIdentifier();
        identifier.setId(deviceId);
        identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        return identifier;
    }

    public static Activity getOperationResponse(List<String> deviceIDs, Operation operation)
            throws OperationManagementException, InvalidDeviceException {
        if (deviceIDs == null || deviceIDs.isEmpty()) {
            String errorMessage = "Device identifier list is empty";
            log.error(errorMessage);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(errorMessage).build());
        }
        DeviceIdentifier deviceIdentifier;
        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
        for (String deviceId : deviceIDs) {
            deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(AndroidConstants.DEVICE_TYPE_ANDROID);
            deviceIdentifiers.add(deviceIdentifier);
        }
        return AndroidAPIUtils.getDeviceManagementService().addOperation(
                DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID, operation, deviceIdentifiers);
    }

    public static List<DeviceState> getAllEventsForDevice(String tableName, String query) throws AnalyticsException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        AnalyticsDataAPI analyticsDataAPI = AndroidAPIUtils.getAnalyticsDataAPI();
        int eventCount = analyticsDataAPI.searchCount(tenantId, tableName, query);
        if (eventCount == 0) {
            return null;
        }
        List<SearchResultEntry> resultEntries = analyticsDataAPI.search(tenantId, tableName, query, 0, eventCount);
        List<String> recordIds = getRecordIds(resultEntries);
        AnalyticsDataResponse response = analyticsDataAPI.get(tenantId, tableName, 1, null, recordIds);
        Map<String, DeviceState> deviceStateses = createDeviceStatusData(AnalyticsDataAPIUtil.listRecords(
                analyticsDataAPI, response));
        return getSortedDeviceStateData(deviceStateses, resultEntries);
    }

    private static List<String> getRecordIds(List<SearchResultEntry> searchResults) {
        List<String> ids = new ArrayList<>();
        for (SearchResultEntry searchResult : searchResults) {
            ids.add(searchResult.getId());
        }
        return ids;
    }

    public static Map<String, DeviceState> createDeviceStatusData(List<Record> records) {
        Map<String, DeviceState> deviceStatuses = new HashMap<>();
        for (Record record : records) {
            DeviceState deviceState = createDeviceStatusData(record);
            deviceStatuses.put(deviceState.getId(), deviceState);
        }
        return deviceStatuses;
    }

    private static DeviceState createDeviceStatusData(Record record) {
        DeviceState deviceState = new DeviceState();
        deviceState.setId(record.getId());
        deviceState.setValues(record.getValues());
        return deviceState;
    }

    public static List<DeviceState> getSortedDeviceStateData(Map<String, DeviceState> sensorDatas,
                                                             List<SearchResultEntry> searchResults) {
        List<DeviceState> sortedRecords = new ArrayList<>();
        for (SearchResultEntry searchResultEntry : searchResults) {
            sortedRecords.add(sensorDatas.get(searchResultEntry.getId()));
        }
        return sortedRecords;
    }

    public static void updateOperation(String deviceId, Operation operation)
            throws OperationManagementException, PolicyComplianceException, ApplicationManagementException {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);

        if (!Operation.Status.ERROR.equals(operation.getStatus()) &&
            AndroidConstants.OperationCodes.MONITOR.equals(operation.getCode())) {
            if (log.isDebugEnabled()) {
                log.debug("Received compliance status from MONITOR operation ID: " + operation.getId());
            }
            AndroidAPIUtils.getPolicyManagerService().checkPolicyCompliance(deviceIdentifier,
                                                                            getComplianceFeatures(operation.getPayLoad()));
        } else if (!Operation.Status.ERROR.equals(operation.getStatus()) && AndroidConstants.
                OperationCodes.APPLICATION_LIST.equals(operation.getCode())) {
            if (log.isDebugEnabled()) {
                log.debug("Received applications list from device '" + deviceId + "'");
            }
            updateApplicationList(operation, deviceIdentifier);

        } else if (!Operation.Status.ERROR.equals(operation.getStatus()) && AndroidConstants.
                OperationCodes.DEVICE_INFO.equals(operation.getCode())) {

            try {
                if (log.isDebugEnabled()) {
                    log.debug("Operation response: " + operation.getOperationResponse());
                }
                Device device = new Gson().fromJson(operation.getOperationResponse(), Device.class);
                org.wso2.carbon.device.mgt.common.device.details.DeviceInfo deviceInfo = convertDeviceToInfo(device);
                updateDeviceInfo(deviceIdentifier, deviceInfo);
            } catch (DeviceDetailsMgtException e) {
                throw new OperationManagementException("Error occurred while updating the device information.", e);
            }

        } else if (!Operation.Status.ERROR.equals(operation.getStatus()) &&
                   AndroidConstants.OperationCodes.DEVICE_LOCATION.equals(operation.getCode())) {
            try {
                DeviceLocation location = new Gson().fromJson(operation.getOperationResponse(), DeviceLocation.class);
                // reason for checking "location.getLatitude() != null" because when device fails to provide
                // device location and send status instead, above Gson converter create new location object
                // with null attributes
                if (location != null && location.getLatitude() != null) {
                    location.setDeviceIdentifier(deviceIdentifier);
                    updateDeviceLocation(location);
                }
            } catch (DeviceDetailsMgtException e) {
                throw new OperationManagementException("Error occurred while updating the device location.", e);
            }
        }
        AndroidAPIUtils.getDeviceManagementService().updateOperation(deviceIdentifier, operation);
    }

    public static List<? extends Operation> getPendingOperations
            (DeviceIdentifier deviceIdentifier) throws OperationManagementException {

        List<? extends Operation> operations;
        operations = AndroidAPIUtils.getDeviceManagementService().getPendingOperations(deviceIdentifier);
        return operations;
    }

    private static void updateApplicationList(Operation operation, DeviceIdentifier deviceIdentifier)
            throws ApplicationManagementException {
        // Parsing json string to get applications list.
        if (operation.getOperationResponse() != null) {
            if (operation.getOperationResponse().equals("SAME_APPLICATION_LIST")) {
                return;
            }
            JsonElement jsonElement = new JsonParser().parse(operation.getOperationResponse());
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Application app;
            List<Application> applications = new ArrayList<>(jsonArray.size());
            for (JsonElement element : jsonArray) {
                app = new Application();
                app.setName(element.getAsJsonObject().
                        get(AndroidConstants.ApplicationProperties.NAME).getAsString());
                app.setApplicationIdentifier(element.getAsJsonObject().
                        get(AndroidConstants.ApplicationProperties.IDENTIFIER).getAsString());
                app.setPlatform(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
                if (element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.USS) != null) {
                    app.setMemoryUsage(element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.USS).getAsInt());
                }
                if (element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.VERSION) != null) {
                    app.setVersion(element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.VERSION).getAsString());
                }
                if (element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.IS_ACTIVE) != null) {
                    app.setActive(element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.IS_ACTIVE).getAsBoolean());
                }
                applications.add(app);
            }
            AndroidAPIUtils.getApplicationManagerService().updateApplicationListInstalledInDevice(deviceIdentifier, applications);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Operation Response is null.");
            }
        }

    }

    private static void updateDeviceLocation(DeviceLocation deviceLocation) throws DeviceDetailsMgtException {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager informationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);

        informationManager.addDeviceLocation(deviceLocation);
    }

    private static void updateDeviceInfo(DeviceIdentifier deviceId, DeviceInfo deviceInfo)
            throws DeviceDetailsMgtException {
        DeviceInformationManager informationManager = AndroidAPIUtils.getDeviceInformationManagerService();
        informationManager.addDeviceInfo(deviceId, deviceInfo);
    }

    private static org.wso2.carbon.device.mgt.common.device.details.DeviceInfo convertDeviceToInfo(Device device) {

        org.wso2.carbon.device.mgt.common.device.details.DeviceInfo deviceInfo =
                new org.wso2.carbon.device.mgt.common.device.details.DeviceInfo();
        if (deviceInfo.getDeviceDetailsMap() == null) {
            deviceInfo.setDeviceDetailsMap(new HashMap<>());
        }
        List<Device.Property> props = device.getProperties();

        for (Device.Property prop : props) {
            if (Utils.getDeviceDetailsColumnNames().containsValue(prop.getName())) {
                extractDefinedProperties(deviceInfo, prop);
            } else {
                extractMapProperties(deviceInfo, prop);
            }
        }
        return deviceInfo;
    }

    private static void extractMapProperties(DeviceInfo deviceInfo, Device.Property prop) {
        if (prop.getName().equalsIgnoreCase("CPU_INFO")) {
            deviceInfo.getDeviceDetailsMap().put("cpuUser", getProperty(prop.getValue(), "User"));
            deviceInfo.getDeviceDetailsMap().put("cpuSystem", getProperty(prop.getValue(), "System"));
            deviceInfo.getDeviceDetailsMap().put("IOW", getProperty(prop.getValue(), "IOW"));
            deviceInfo.getDeviceDetailsMap().put("IRQ", getProperty(prop.getValue(), "IRQ"));
        } else if (prop.getName().equalsIgnoreCase("RAM_INFO")) {
            if (!getProperty(prop.getValue(), "TOTAL_MEMORY").isEmpty()) {
                deviceInfo.setTotalRAMMemory(Double.parseDouble(getProperty(prop.getValue(), "TOTAL_MEMORY")));
            } else {
                deviceInfo.setTotalRAMMemory(-1D);
            }
            if (!getProperty(prop.getValue(), "AVAILABLE_MEMORY").isEmpty()) {
                deviceInfo.setAvailableRAMMemory(Double.parseDouble(
                        getProperty(prop.getValue(), "AVAILABLE_MEMORY")));
            } else {
                deviceInfo.setAvailableRAMMemory(-1D);
            }
            deviceInfo.getDeviceDetailsMap().put("ramThreshold", getProperty(prop.getValue(), "THRESHOLD"));
            deviceInfo.getDeviceDetailsMap().put("ramLowMemory", getProperty(prop.getValue(), "LOW_MEMORY"));
        } else if (prop.getName().equalsIgnoreCase("BATTERY_INFO")) {
            deviceInfo.setPluggedIn(Boolean.parseBoolean(getProperty(prop.getValue(), "PLUGGED")));

            deviceInfo.getDeviceDetailsMap().put("batteryLevel", getProperty(prop.getValue(), "BATTERY_LEVEL"));
            deviceInfo.getDeviceDetailsMap().put("batteryScale", getProperty(prop.getValue(), "SCALE"));
            deviceInfo.getDeviceDetailsMap().put("batteryVoltage",
                                                 getProperty(prop.getValue(), "BATTERY_VOLTAGE"));
            deviceInfo.getDeviceDetailsMap().put("batteryTemperature",
                                                 getProperty(prop.getValue(), "TEMPERATURE"));
            deviceInfo.getDeviceDetailsMap().put("batteryCurrentTemperature",
                                                 getProperty(prop.getValue(), "CURRENT_AVERAGE"));
            deviceInfo.getDeviceDetailsMap().put("batteryTechnology",
                                                 getProperty(prop.getValue(), "TECHNOLOGY"));
            deviceInfo.getDeviceDetailsMap().put("batteryHealth", getProperty(prop.getValue(), "HEALTH"));
            deviceInfo.getDeviceDetailsMap().put("batteryStatus", getProperty(prop.getValue(), "STATUS"));
        } else if (prop.getName().equalsIgnoreCase("NETWORK_INFO")) {
            deviceInfo.setSsid(getProperty(prop.getValue(), "WIFI_SSID"));
            deviceInfo.setConnectionType(getProperty(prop.getValue(), "CONNECTION_TYPE"));

            deviceInfo.getDeviceDetailsMap().put("mobileSignalStrength",
                                                 getProperty(prop.getValue(), "MOBILE_SIGNAL_STRENGTH"));
            deviceInfo.getDeviceDetailsMap().put("wifiSignalStrength",
                                                 getProperty(prop.getValue(), "WIFI_SIGNAL_STRENGTH"));
        } else if (prop.getName().equalsIgnoreCase("DEVICE_INFO")) {
            if (!getProperty(prop.getValue(), "BATTERY_LEVEL").isEmpty()) {
                deviceInfo.setBatteryLevel(Double.parseDouble(
                        getProperty(prop.getValue(), "BATTERY_LEVEL")));
            } else {
                deviceInfo.setBatteryLevel(-1D);
            }
            if (!getProperty(prop.getValue(), "INTERNAL_TOTAL_MEMORY").isEmpty()) {
                deviceInfo.setInternalTotalMemory(Double.parseDouble(
                        getProperty(prop.getValue(), "INTERNAL_TOTAL_MEMORY")));
            } else {
                deviceInfo.setInternalTotalMemory(-1D);
            }
            if (!getProperty(prop.getValue(), "INTERNAL_AVAILABLE_MEMORY").isEmpty()) {
                deviceInfo.setInternalAvailableMemory(Double.parseDouble(
                        getProperty(prop.getValue(), "INTERNAL_AVAILABLE_MEMORY")));
            } else {
                deviceInfo.setInternalAvailableMemory(-1D);
            }
            if (!getProperty(prop.getValue(), "EXTERNAL_TOTAL_MEMORY").isEmpty()) {
                deviceInfo.setExternalTotalMemory(Double.parseDouble(
                        getProperty(prop.getValue(), "EXTERNAL_TOTAL_MEMORY")));
            } else {
                deviceInfo.setExternalTotalMemory(-1D);
            }
            if (!getProperty(prop.getValue(), "EXTERNAL_AVAILABLE_MEMORY").isEmpty()) {
                deviceInfo.setExternalAvailableMemory(Double.parseDouble(
                        getProperty(prop.getValue(), "EXTERNAL_AVAILABLE_MEMORY")));
            } else {
                deviceInfo.setExternalAvailableMemory(-1D);
            }

            deviceInfo.getDeviceDetailsMap().put("encryptionEnabled",
                                                 getProperty(prop.getValue(), "ENCRYPTION_ENABLED"));
            deviceInfo.getDeviceDetailsMap().put("passcodeEnabled",
                                                 getProperty(prop.getValue(), "PASSCODE_ENABLED"));
            deviceInfo.getDeviceDetailsMap().put("operator",
                                                 getProperty(prop.getValue(), "OPERATOR"));
            deviceInfo.getDeviceDetailsMap().put("PhoneNumber",
                                                 getProperty(prop.getValue(), "PHONE_NUMBER"));
        } else if (prop.getName().equalsIgnoreCase("IMEI")) {
            deviceInfo.getDeviceDetailsMap().put("IMEI", prop.getValue());
        } else if (prop.getName().equalsIgnoreCase("IMSI")) {
            deviceInfo.getDeviceDetailsMap().put("IMSI", prop.getValue());
        } else if (prop.getName().equalsIgnoreCase("MAC")) {
            deviceInfo.getDeviceDetailsMap().put("mac", prop.getValue());
        } else if (prop.getName().equalsIgnoreCase("SERIAL")) {
            deviceInfo.getDeviceDetailsMap().put("serial", prop.getValue());
        } else {
            deviceInfo.getDeviceDetailsMap().put(prop.getName(), prop.getValue());
        }
    }

    private static void extractDefinedProperties(DeviceInfo deviceInfo, Device.Property prop) {
        if (prop.getName().equalsIgnoreCase("DEVICE_MODEL")) {
            deviceInfo.setDeviceModel(prop.getValue());
        } else if (prop.getName().equalsIgnoreCase("VENDOR")) {
            deviceInfo.setVendor(prop.getValue());
        } else if (prop.getName().equalsIgnoreCase("OS_VERSION")) {
            deviceInfo.setOsVersion(prop.getValue());
        } else if (prop.getName().equalsIgnoreCase("OS_BUILD_DATE")) {
            deviceInfo.setOsBuildDate(prop.getValue());
        }
    }

    private static String getProperty(String properties, String needed) {
        // This is not a key value pair. value is the immediate element to its filed name.
        // Ex:
        // [{"name":"ENCRYPTION_ENABLED","value":"false"},{"name":"PASSCODE_ENABLED","value":"true"},
        // {"name":"BATTERY_LEVEL","value":"100"},{"name":"INTERNAL_TOTAL_MEMORY","value":"0.76"}]
        JsonElement jsonElement = new JsonParser().parse(properties);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("name")
                    && jsonObject.get("name").getAsString().equalsIgnoreCase(needed)) {
                    if (jsonObject.has("value") && jsonObject.get("value") != JsonNull.INSTANCE) {
                        return jsonObject.get("value").getAsString().replace("%", "");
                    } else {
                        return "";
                    }
                }
            }
        }
        return "";
    }

    private static List<ComplianceFeature> getComplianceFeatures(Object compliancePayload)
            throws PolicyComplianceException {
        String compliancePayloadString = new Gson().toJson(compliancePayload);
        if (compliancePayload == null) {
            return null;
        }
        // Parsing json string to get compliance features.
        JsonElement jsonElement;
        if (compliancePayloadString instanceof String) {
            jsonElement = new JsonParser().parse(compliancePayloadString);
        } else {
            throw new PolicyComplianceException("Invalid policy compliance payload");
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Gson gson = new Gson();
        ComplianceFeature complianceFeature;
        List<ComplianceFeature> complianceFeatures = new ArrayList<ComplianceFeature>(jsonArray.size());

        for (JsonElement element : jsonArray) {
            complianceFeature = gson.fromJson(element, ComplianceFeature.class);
            complianceFeatures.add(complianceFeature);
        }
        return complianceFeatures;
    }

    /**
     * Returns a new BadRequestException
     *
     * @param description description of the exception
     * @return a new BadRequestException with the specified details as a response DTO
     */
    public static BadRequestException buildBadRequestException(String description) {
        ErrorResponse errorResponse = getErrorResponse(AndroidConstants.
                                                               ErrorMessages.STATUS_BAD_REQUEST_MESSAGE_DEFAULT, 400l, description);
        return new BadRequestException(errorResponse);
    }

    /**
     * Returns generic ErrorResponse.
     *
     * @param message     specific error message
     * @param code        error code
     * @param description error description
     * @return generic Response with error specific details.
     */
    public static ErrorResponse getErrorResponse(String message, Long code, String description) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(code);
        errorResponse.setMoreInfo("");
        errorResponse.setMessage(message);
        errorResponse.setDescription(description);
        return errorResponse;
    }

    public static <T> ErrorResponse getConstraintViolationErrorDTO(Set<ConstraintViolation<T>> violations) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDescription("Validation Error");
        errorResponse.setMessage("Bad Request");
        errorResponse.setCode(400l);
        errorResponse.setMoreInfo("");
        List<ErrorListItem> errorListItems = new ArrayList<>();
        for (ConstraintViolation violation : violations) {
            ErrorListItem errorListItemDTO = new ErrorListItem();
            errorListItemDTO.setCode(400 + "_" + violation.getPropertyPath());
            errorListItemDTO.setMessage(violation.getPropertyPath() + ": " + violation.getMessage());
            errorListItems.add(errorListItemDTO);
        }
        errorResponse.setErrorItems(errorListItems);
        return errorResponse;
    }

    public static void installEnrollmentApplications(ProfileFeature feature, String deviceId)
            throws PolicyManagementException {
        String appId = "";
        String payload;
        StringRequestEntity requestEntity;
        HttpClient httpClient;
        PostMethod request;
        try {
            String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
            ApiApplicationKey apiApplicationKey = OAuthUtils.getClientCredentials(tenantDomain);
            String username = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm()
                    .getRealmConfiguration().getAdminUserName() + AndroidConstants.ApplicationInstall.AT + tenantDomain;
            AccessTokenInfo tokenInfo = OAuthUtils.getOAuthCredentials(apiApplicationKey, username);
            String requestUrl = AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_PROTOCOL +
                    System.getProperty(AndroidConstants.ApplicationInstall.IOT_CORE_HOST) +
                    AndroidConstants.ApplicationInstall.COLON +
                    System.getProperty(AndroidConstants.ApplicationInstall.IOT_CORE_PORT) +
                    AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_CONTEXT;
            JsonElement appListElement = new JsonParser().parse(feature.getContent().toString()).getAsJsonObject()
                    .get(AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_CODE);
            JsonArray appListArray = appListElement.getAsJsonArray();
            for (JsonElement appElement : appListArray) {
                appId = appElement.getAsJsonObject().
                        get(AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_ID).getAsString();
                payload = "{\"appId\": \"" + appId + "\", \"scheduleTime\":\"2013-12-25T15:25:30-05:00\"," +
                        "\"deviceIds\": [\"{\\\"id\\\":\\\"" + deviceId + "\\\", \\\"type\\\":\\\"android\\\"}\"]}";
                requestEntity = new StringRequestEntity(payload, MediaType.APPLICATION_JSON,
                        AndroidConstants.ApplicationInstall.ENCODING);
                httpClient = new HttpClient();
                request = new PostMethod(requestUrl);
                request.addRequestHeader(AndroidConstants.ApplicationInstall.AUTHORIZATION,
                        AndroidConstants.ApplicationInstall.AUTHORIZATION_HEADER_VALUE + tokenInfo.getAccessToken());
                request.setRequestEntity(requestEntity);
                httpClient.executeMethod(request);
            }
        } catch (UserStoreException e) {
            throw new PolicyManagementException("Error while accessing user store for user with iOS device id: " +
                    deviceId, e);
        } catch (APIManagerException e) {
            throw new PolicyManagementException("Error while retrieving access token for Android device id: " +
                    deviceId, e);
        } catch (HttpException e) {
            throw new PolicyManagementException("Error while calling the app store to install enrollment app with " +
                    "id: " + appId + " on device with id: " + deviceId, e);
        } catch (IOException e) {
            throw new PolicyManagementException("Error while installing the enrollment app with id: " + appId +
                    " on device with id: " + deviceId, e);
        }
    }

}
