/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
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
package org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.APN;

import java.util.List;

/**
 * This class is used to wrap the APN bean with devices.
 */
@ApiModel(value = "APNBeanWrapper",
        description = "Mapping between APN operation and device list to be applied.")
public class APNBeanWrapper {

    @ApiModelProperty(name = "operation", value = "Information of configuring APN operation", required = true)
    private APN operation;
    @ApiModelProperty(name = "deviceIDs", value = "List of device Ids", required = true)
    private List<String> deviceIDs;

    public Wifi getOperation() {
        return operation;
    }

    public void setOperation(APN operation) {
        this.operation = operation;
    }

    public List<String> getDeviceIDs() {
        return deviceIDs;
    }

    public void setDeviceIDs(List<String> deviceIDs) {
        this.deviceIDs = deviceIDs;
    }
}
