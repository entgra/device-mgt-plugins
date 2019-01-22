/*
 *   Copyright (c) 2018, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.windows.api.bean.wrapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.wso2.carbon.device.mgt.mobile.windows.api.bean.EnterpriseApplication;

import java.util.List;

@ApiModel(value = "EnterpriseApplicationBeanWrapper",
        description = "Information related to Enterprise Application.")
public class EnterpriseApplicationBeanWrapper {

    @ApiModelProperty(name = "deviceIDs",
            value = "List of device Ids to be need to execute operation.", required = true)
    private List<String> deviceIDs;
    @ApiModelProperty(name = "operation",
            value = "Enterprise Application.", required = true)
    private EnterpriseApplication operation;

    public List<String> getDeviceIDs() {
        return deviceIDs;
    }

    public void setDeviceIDs(List<String> deviceIDs) {
        this.deviceIDs = deviceIDs;
    }

    public EnterpriseApplication getOperation() {
        return operation;
    }

    public void setOperation(EnterpriseApplication operation) {
        this.operation = operation;
    }
}
