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

/**
 * Defines constants used in Android-REST API bundle.
 */
public final class AndroidConstants {

    public static final String DEVICE_TYPE_ANDROID = "android";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String SCOPE = "scope";

    public final class DeviceProperties {
        private DeviceProperties() {
            throw new AssertionError();
        }

        public static final String PROPERTY_USER_KEY = "username";
        public static final String PROPERTY_DEVICE_KEY = "device";
    }

    public final class DeviceFeatures {
        private DeviceFeatures() {
            throw new AssertionError();
        }
    }

    public final class DeviceConstants {
        private DeviceConstants() {
            throw new AssertionError();
        }

        public static final String DEVICE_MAC_KEY = "mac";
        public static final String DEVICE_DESCRIPTION_KEY = "description";
        public static final String DEVICE_OWNERSHIP_KEY = "ownership";
        public static final String DEVICE_PROPERTIES_KEY = "properties";
        public static final String DEVICE_FEATURES_KEY = "features";
        public static final String DEVICE_DATA = "data";
        public static final String DEVICE_ID_NOT_FOUND = "Device not found for device id: %s";
        public static final String DEVICE_ID_SERVICE_NOT_FOUND =
                "Issue in retrieving device management service instance for device found at %s";
    }

    public final class Messages {
        private Messages() {
            throw new AssertionError();
        }

        public static final String DEVICE_MANAGER_SERVICE_NOT_AVAILABLE =
                "Device Manager service not available";
    }

    public final class OperationCodes {
        private OperationCodes() {
            throw new AssertionError();
        }

        public static final String DEVICE_LOCK = "DEVICE_LOCK";
        public static final String FILE_DOWNLOAD = "FILE_UPLOAD_TO_THE_DEVICE";
        public static final String FILE_UPLOAD = "FILE_DOWNLOAD_FROM_THE_DEVICE";
        public static final String DEVICE_UNLOCK = "DEVICE_UNLOCK";
        public static final String DEVICE_LOCATION = "DEVICE_LOCATION";
        public static final String WIFI = "WIFI";
        public static final String CAMERA = "CAMERA";
        public static final String DEVICE_MUTE = "DEVICE_MUTE";
        public static final String PASSCODE_POLICY = "PASSCODE_POLICY";
        public static final String DEVICE_INFO = "DEVICE_INFO";
        public static final String ENTERPRISE_WIPE = "ENTERPRISE_WIPE";
        public static final String CLEAR_PASSWORD = "CLEAR_PASSWORD";
        public static final String WIPE_DATA = "WIPE_DATA";
        public static final String APPLICATION_LIST = "APPLICATION_LIST";
        public static final String CHANGE_LOCK_CODE = "CHANGE_LOCK_CODE";
        public static final String INSTALL_APPLICATION = "INSTALL_APPLICATION";
        public static final String UPDATE_APPLICATION = "UPDATE_APPLICATION";
        public static final String UNINSTALL_APPLICATION = "UNINSTALL_APPLICATION";
        public static final String BLACKLIST_APPLICATIONS = "BLACKLIST_APPLICATIONS";
        public static final String ENCRYPT_STORAGE = "ENCRYPT_STORAGE";
        public static final String DEVICE_RING = "DEVICE_RING";
        public static final String DEVICE_REBOOT = "REBOOT";
        public static final String UPGRADE_FIRMWARE = "UPGRADE_FIRMWARE";
        public static final String NOTIFICATION = "NOTIFICATION";
        public static final String WEBCLIP = "WEBCLIP";
        public static final String DISENROLL = "DISENROLL";
        public static final String MONITOR = "MONITOR";
        public static final String VPN = "VPN";
        public static final String LOGCAT = "LOGCAT";
        public static final String APP_RESTRICTION = "APP-RESTRICTION";
        public static final String WORK_PROFILE = "WORK_PROFILE";
        public static final String NOTIFIER_FREQUENCY = "NOTIFIER_FREQUENCY";
    }

    public final class StatusCodes {
        private StatusCodes() {
            throw new AssertionError();
        }

        public static final int MULTI_STATUS_HTTP_CODE = 207;
    }

    public final class TenantConfigProperties {
        private TenantConfigProperties() {
            throw new AssertionError();
        }

        public static final String LICENSE_KEY = "androidEula";
        public static final String LANGUAGE_US = "en_US";
        public static final String CONTENT_TYPE_TEXT = "text";
        public static final String NOTIFIER_FREQUENCY = "notifierFrequency";
    }

    public final class ApplicationProperties {
        private ApplicationProperties() {
            throw new AssertionError();
        }

        public static final String NAME = "name";
        public static final String IDENTIFIER = "package";
        public static final String USS = "USS";
        public static final String VERSION = "version";
        public static final String ICON = "icon";
        public static final String IS_ACTIVE = "isActive";
    }

    public final class ErrorMessages {
        private ErrorMessages () { throw new AssertionError(); }

        public static final String STATUS_BAD_REQUEST_MESSAGE_DEFAULT = "Bad Request";

    }

}
