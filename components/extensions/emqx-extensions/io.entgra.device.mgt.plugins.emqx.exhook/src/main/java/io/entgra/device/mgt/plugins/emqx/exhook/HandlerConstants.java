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

package io.entgra.device.mgt.plugins.emqx.exhook;

public class HandlerConstants {
    public static final String INTROSPECT_ENDPOINT = "/oauth2/introspect";
    public static final String BASIC = "Basic ";
    public static final String EXECUTOR_EXCEPTION_PREFIX = "ExecutorException-";
    public static final String TOKEN_IS_EXPIRED = "ACCESS_TOKEN_IS_EXPIRED";
    public static final String COLON = ":";
    public static final int INTERNAL_ERROR_CODE = 500;
    public static final int MIN_TOKEN_LENGTH = 36;
}
