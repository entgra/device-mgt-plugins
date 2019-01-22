/*
 * Copyright (c) 2018, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
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
 */

package org.wso2.carbon.device.mgt.mobile.windows.api.common.exceptions;

public class ProfileConfigurationException extends Exception {

    private static final long serialVersionUID = 8025559931927889261L;

    public ProfileConfigurationException(String errorMsg) {
        super(errorMsg);
    }

    public ProfileConfigurationException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }
}
