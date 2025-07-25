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
package io.entgra.device.mgt.plugins.output.adapter.mqtt.util;


public final class MQTTEventAdapterConstants {

    private MQTTEventAdapterConstants() {
    }

    public static final String ADAPTER_TYPE_MQTT = "oauth-mqtt";
    public static final String ADAPTER_CONF_URL = "url";
    public static final String ADAPTER_CONF_USERNAME = "username";
    public static final String ADAPTER_CONF_USERNAME_HINT = "username.hint";
    public static final String ADAPTER_CONF_PASSWORD = "password";
    public static final String ADAPTER_CONF_PASSWORD_HINT = "password.hint";
    public static final String ADAPTER_CONF_DCR_URL = "dcrUrl";
    public static final String ADAPTER_CONF_TOKEN_URL = "tokenUrl";
    public static final String ADAPTER_CONF_SCOPES = "scopes";
    public static final String ADAPTER_CONF_SCOPES_HINT = "scopes.hint";
    public static final String ADAPTER_CONF_URL_HINT = "url.hint";
    public static final String ADAPTER_MESSAGE_TOPIC = "topic";
    public static final String ADAPTER_MESSAGE_QOS = "qos";
    public static final String ADAPTER_CONF_CLEAN_SESSION = "cleanSession";
    public static final String ADAPTER_CONF_CLEAN_SESSION_HINT = "cleanSession.hint";
    public static final String CONNECTION_KEEP_ALIVE_INTERVAL = "connectionKeepAliveInterval";
    public static final int DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL = 60;
    public static final String ADAPTER_TEMP_DIRECTORY_NAME = "java.io.tmpdir";
    public static final String ADAPTER_CONF_CLIENTID = "clientId";
    public static final String ADAPTER_CONF_CLIENTID_HINT = "clientId.hint";
    public static final String EMPTY_STRING = "";
    public static final String ADAPTER_CONF_KEEP_ALIVE = "keepAlive";
    public static final int ADAPTER_CONF_DEFAULT_KEEP_ALIVE = 60000;

    public static final int DEFAULT_MIN_THREAD_POOL_SIZE = 8;
    public static final int DEFAULT_MAX_THREAD_POOL_SIZE = 100;
    public static final int DEFAULT_EXECUTOR_JOB_QUEUE_SIZE = 2000;
    public static final long DEFAULT_KEEP_ALIVE_TIME_IN_MILLIS = 20000;
    public static final String ADAPTER_MIN_THREAD_POOL_SIZE_NAME = "minThread";
    public static final String ADAPTER_MAX_THREAD_POOL_SIZE_NAME = "maxThread";
    public static final String ADAPTER_KEEP_ALIVE_TIME_NAME = "keepAliveTimeInMillis";
    public static final String ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME = "jobQueueSize";

    public static final String DEFAULT_CALLBACK = "";
    public static final String DEFAULT_PASSWORD = "";
    public static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    public static final String TOKEN_SCOPE = "production";
    public static final String APPLICATION_NAME_PREFIX = "OutputAdapter_";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";


    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String AUTHORIZATION_HEADER_VALUE_PREFIX = "Basic ";
    public static final String PASSWORD_GRANT_TYPE = "password";
    public static final String PASSWORD_GRANT_TYPE_USERNAME = "username";
    public static final String PASSWORD_GRANT_TYPE_PASSWORD = "password";
    public static final String PASSWORD_GRANT_TYPE_SCOPES = "scopes";
    public static final String ACCESS_TOKEN_GRANT_TYPE_PARAM_NAME = "access_token";
    public static final String GRANT_TYPE_PARAM_NAME = "grant_type";
    public static final int TOKEN_SPLIT_INDEX = 18;

}
