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
package io.entgra.device.mgt.plugins.input.adapter.http.util;

/**
 * This holds the constants related to HTTP event adapter.
 */
public final class HTTPEventAdapterConstants {

    private HTTPEventAdapterConstants() {
    }

    public static final String ADAPTER_TYPE_HTTP = "oauth-http";
    public static final String ADAPTER_USAGE_TIPS_PREFIX = "http.usage.tips_prefix";
    public static final String ADAPTER_USAGE_TIPS_MID1 = "http.usage.tips_mid1";
    public static final String ADAPTER_USAGE_TIPS_MID2 = "http.usage.tips_mid2";
    public static final String ADAPTER_USAGE_TIPS_MID3 = "http.usage.tips_mid3";
    public static final String ADAPTER_USAGE_TIPS_POSTFIX = "http.usage.tips_postfix";
    public static final int ADAPTER_MIN_THREAD_POOL_SIZE = 8;
    public static final int ADAPTER_MAX_THREAD_POOL_SIZE = 100;
    public static final int ADAPTER_EXECUTOR_JOB_QUEUE_SIZE = 10000;
    public static final long DEFAULT_KEEP_ALIVE_TIME_IN_MILLS = 20000;
    public static final String ENDPOINT_PREFIX = "/endpoints/";
    public static final String ENDPOINT_URL_SEPARATOR = "/";
    public static final String ENDPOINT_TENANT_KEY = "t";
    public static final String ADAPTER_MIN_THREAD_POOL_SIZE_NAME = "minThread";
    public static final String ADAPTER_MAX_THREAD_POOL_SIZE_NAME = "maxThread";
    public static final String ADAPTER_KEEP_ALIVE_TIME_NAME = "keepAliveTimeInMillis";
    public static final String ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME = "jobQueueSize";
    public static final String EXPOSED_TRANSPORTS = "transports";
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    public static final String LOCAL = "local";
    public static final String ALL = "all";
    public static final String CARBON_CONFIG_PORT_OFFSET_NODE = "Ports.Offset";
    public static final int DEFAULT_HTTP_PORT = 9763;
    public static final int DEFAULT_HTTPS_PORT = 9443;
    public static final String MAXIMUM_TOTAL_HTTP_CONNECTION = "maximumTotalHttpConnection";
    public static final String MAXIMUM_HTTP_CONNECTION_PER_HOST = "maximumHttpConnectionPerHost";
    public static final String TOKEN_VALIDATION_ENDPOINT_URL = "tokenValidationUrl";
    public static final String TOKEN_VALIDATION_POST_FIX = "/services/OAuth2TokenValidationService";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TENANT_DOMAIN_TAG = "tenantDomain";
    public static final String USERNAME_TAG = "username";
    public static final String SCOPE_TAG = "scopes";
    public static final String ADAPTER_CONF_CONTENT_VALIDATOR_TYPE = "contentValidator";
    public static final String ADAPTER_CONF_CONTENT_VALIDATOR_TYPE_HINT = "contentValidator.hint";
    public static final String DEFAULT = "default";
    public static final String ADAPTER_CONF_CONTENT_TRANSFORMER_CLASSNAME = "contentTransformer";
    public static final String ADAPTER_CONF_CONTENT_TRANSFORMER_CLASSNAME_HINT = "contentTransformer.hint";
}
