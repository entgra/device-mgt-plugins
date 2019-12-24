/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.mobile.android.common.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This class represents the information of sending application config operation.
 */
@ApiModel(value = "ApplicationRestriction",
        description = "Details related to application config passed to device.")
public class ApplicationRestriction extends AndroidOperation implements Serializable {
    private static final long serialVersionUID = 1995401458L;

    @ApiModelProperty(name = "appIdentifier", value = "The application identifier to be sent.", required = true)
    private String appIdentifier;

    @ApiModelProperty(name = "restrictionPayload", value = "The restriction payload to be sent.", required = true)
    private String restrictionPayload;

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }

    public String getRestrictionPayload() {
        return restrictionPayload;
    }

    public void setRestrictionPayload(String restrictionPayload) {
        this.restrictionPayload = restrictionPayload;
    }
}