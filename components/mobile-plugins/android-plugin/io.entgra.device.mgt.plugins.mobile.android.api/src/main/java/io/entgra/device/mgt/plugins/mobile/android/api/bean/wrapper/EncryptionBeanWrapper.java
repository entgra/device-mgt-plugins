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

import io.entgra.device.mgt.plugins.mobile.android.api.bean.DeviceEncryption;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * This class is used to wrap the Encrypt bean with devices.
 */
@ApiModel(value = "EncryptionBeanWrapper",
          description = "Mapping between encryption operation and device list to be applied.")
public class EncryptionBeanWrapper {

    @ApiModelProperty(name = "operation", value = "The information of encrypt operation", required = true)
	private DeviceEncryption operation;
    @ApiModelProperty(name = "deviceIDs", value = "List of device Ids", required = true)
	private List<String> deviceIDs;

	public DeviceEncryption getOperation() {
		return operation;
	}

	public void setOperation(DeviceEncryption operation) {
		this.operation = operation;
	}

	public List<String> getDeviceIDs() {
		return deviceIDs;
	}

	public void setDeviceIDs(List<String> deviceIDs) {
		this.deviceIDs = deviceIDs;
	}
}
