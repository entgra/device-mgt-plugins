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
package org.wso2.carbon.device.mgt.mobile.android.common.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This class represents the information of configuring APN operation.
 */
@ApiModel(value = "APN", description = "This class represents the information of configuring APN operation")
public class APN extends AndroidOperation implements Serializable {

    @ApiModelProperty(name = "name", value = "The name of the APN network that you wish to configure",
            required = true)
    private String name;
    @ApiModelProperty(name = "apn", value = "The APN need to connect",
            required = true)
    private String apn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name =name;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }
}
