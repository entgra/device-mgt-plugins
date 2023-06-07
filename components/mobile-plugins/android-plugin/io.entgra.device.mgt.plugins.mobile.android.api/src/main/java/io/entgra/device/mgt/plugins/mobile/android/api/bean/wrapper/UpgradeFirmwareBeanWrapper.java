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
package io.entgra.device.mgt.plugins.mobile.android.api.bean.wrapper;

import io.entgra.device.mgt.plugins.mobile.android.api.bean.UpgradeFirmware;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * This class is used to wrap the UpgradeFirmware bean with devices.
 */
@ApiModel(value = "UpgradeFirmwareBeanWrapper",
        description = "Information related to UpgradeFirmware.")
public class UpgradeFirmwareBeanWrapper {
    @ApiModelProperty(name = "deviceIDs",
            value = "List of device Ids to be need to execute UpgradeFirmware operation.", required = true)
    private List<String> deviceIDs;
    @ApiModelProperty(name = "operation", value = "Information related to UpgradeFirmware operation.", required = true)
    private UpgradeFirmware operation;

    public List<String> getDeviceIDs() {
        return deviceIDs;
    }

    public void setDeviceIDs(List<String> deviceIDs) {
        this.deviceIDs = deviceIDs;
    }

    public UpgradeFirmware getOperation() {
        return operation;
    }

    public void setOperation(UpgradeFirmware operation) {
        this.operation = operation;
    }
}
