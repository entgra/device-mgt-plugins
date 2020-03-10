/*
 * Copyright (c) 2016, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.services.android.bean.wrapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

/**
 * This class is used to wrap the events which receive from the agent application.
 */
@ApiModel(value = "EventBeanWrapper",
        description = "Android agent's event related Information.")
public class EventBeanWrapper {

    @ApiModelProperty(name = "deviceIdentifier", value = "DeviceIdentifier to be need to retrieve/publish Event.", required = true)
    @Size(min = 2, max = 45)
    private String deviceIdentifier;
    @ApiModelProperty(name = "payload", value = "Event payload.", required = true)
    private String payload;
    @ApiModelProperty(name = "type", value = "Type of the event.", required = true)
    @Size(min = 2, max = 20)
    private String type;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
