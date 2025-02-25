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

package io.entgra.device.mgt.plugins.mobile.android.api.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This class represents the information of sending Upgrade Firmware operation.
 */
@ApiModel(value = "UpgradeFirmware",
        description = "This class carries all information related to UpgradeFirmware.")
public class UpgradeFirmware extends AndroidOperation implements Serializable {

    @ApiModelProperty(name = "schedule", value = "Schedule of the UpgradeFirmware.")
    private String schedule;

    @ApiModelProperty(name = "server", value = "Firmware package server.")
    private String server;

    @SuppressWarnings("unused")
    public String getSchedule() {
        return schedule;
    }

    @SuppressWarnings("unused")
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @SuppressWarnings("unused")
    public String getServer() {
        return server;
    }

    @SuppressWarnings("unused")
    public void setServer(String server) {
        this.server = server;
    }

}
