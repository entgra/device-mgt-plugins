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

package org.wso2.carbon.mdm.services.android.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This class represents the information of configuring display message operation.
 */
@ApiModel(value = "LockScreenMessage", description = "This class represents the information of configuring wifi operation")
public class DisplayMessage extends AndroidOperation implements Serializable {

    @ApiModelProperty(name = "lockScreenMessage", value = "The message of the lock screen that you wish to configure",
            required = true)
    private String lockScreenMessage;
    @ApiModelProperty(name = "settingAppSupportMessage", value = "The message of the administrators applications that you wish to configure",
            required = true)
    private String settingAppSupportMessage;
    @ApiModelProperty(name = "disabledSettingSupportMessage", value = "The password to connect to the specified Wifi network",
            required = true)
    private String disabledSettingSupportMessage;

    public String getLockScreenMessage() {
        return lockScreenMessage;
    }

    public void setLockScreenMessage(String lockScreenMessage) {
        this.lockScreenMessage = lockScreenMessage;
    }

    public String getSettingAppSupportMessage() {
        return settingAppSupportMessage;
    }

    public void setSettingAppSupportMessage(String settingAppSupportMessage) {
        this.settingAppSupportMessage = settingAppSupportMessage;
    }

    public String getDisabledSettingSupportMessage() {
        return disabledSettingSupportMessage;
    }

    public void setDisabledSettingSupportMessage(String disabledSettingSupportMessage) {
        this.disabledSettingSupportMessage = disabledSettingSupportMessage;
    }
}
