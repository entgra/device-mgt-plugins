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

package io.entgra.device.mgt.plugins.output.adapter.websocket.util;

/**
 * This class contains the constants related to websocket Output Event Adaptor.
 */
public class WebsocketEventAdapterConstants {

    private WebsocketEventAdapterConstants() {
    }

    public static final String ADAPTER_TYPE_UI = "secured-websocket";
    public static final String ADAPTER_USAGE_TIPS_PREFIX = "ui.usage.tips_prefix";
    public static final String ADAPTER_USAGE_TIPS_POSTFIX = "ui.usage.tips_postfix";
    public static final String ADAPTER_UI_DEFAULT_OUTPUT_STREAM_VERSION = "1.0.0";
    public static final String ADAPTER_UI_COLON = ":";
    public static final int INDEX_ZERO = 0;
    public static final int INDEX_ONE = 1;
    public static final int INDEX_TWO = 2;

    public static final int ADAPTER_MIN_THREAD_POOL_SIZE = 8;
    public static final int ADAPTER_MAX_THREAD_POOL_SIZE = 100;
    public static final int ADAPTER_EXECUTOR_JOB_QUEUE_SIZE = 2000;
    public static final long DEFAULT_KEEP_ALIVE_TIME_IN_MILLIS = 20000;
    public static final String ADAPTER_MIN_THREAD_POOL_SIZE_NAME = "minThread";
    public static final String ADAPTER_MAX_THREAD_POOL_SIZE_NAME = "maxThread";
    public static final String ADAPTER_KEEP_ALIVE_TIME_NAME = "keepAliveTimeInMillis";
    public static final String ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME = "jobQueueSize";

    public static final String ADAPTER_EVENT_QUEUE_SIZE_NAME = "eventQueueSize";
    public static final int EVENTS_QUEUE_SIZE = 30;
    public static final String AUTHENTICATOR_CLASS = "authenticator";
    public static final String AUTHORIZER_CLASS = "authorizer";

}
