
/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.mdm.services.android.bean.wrapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.wso2.carbon.mdm.services.android.bean.ApplicationRestriction;

import java.util.List;

/**
 * This class is used to wrap the Notification bean with devices.
 */
@ApiModel(value = "ApplicationRestrictionBeanWrapper",
        description = "Mapping between application restriction operation and device list to be applied.")
public class ApplicationRestrictionBeanWrapper {

    @ApiModelProperty(name = "deviceIDs", value = "List of device Ids", required = true)
    private List<String> deviceIDs;
    @ApiModelProperty(name = "operation", value = "The information of application restriction operation",
            required = true)
    private ApplicationRestriction operation;

    public List<String> getDeviceIDs() {
        return deviceIDs;
    }

    public void setDeviceIDs(List<String> deviceIDs) {
        this.deviceIDs = deviceIDs;
    }

    public ApplicationRestriction getOperation() {
        return operation;
    }

    public void setOperation(ApplicationRestriction operation) {
        this.operation = operation;
    }
}