/*
 *   Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.carbon.device.mgt.mobile.windows.api.bean;

import com.google.gson.Gson;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.mobile.windows.api.operations.WindowsOperationException;

import java.util.List;

/***
 * This abstract class is used for extending generic functions with regard to operation.
 */
public abstract class WindowsOperation {

    public abstract List<Object> createOperationContent(Operation operation) throws WindowsOperationException;

    /***
     * Converts operation object to a json format.
     *
     * @return JSON formatted String
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Checks if the given string is null or empty
     *
     * @param value string value to check
     * @return boolean value of null or empty
     */
    public boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /***
     * Checks if the given list is null or empty
     *
     * @param value string list value to check
     * @return boolean value of null or empty
     */
    public boolean isNullOrEmpty(List<String> value) {
        return value == null || value.isEmpty();
    }
}
