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
import java.util.List;

/**
 * This class represents information of configuring App Restriction ex: White list and Black list
 */
@ApiModel(value = "AppRestriction",
		description = "This class carries all information related to application restriction.")
public class AppRestriction extends AndroidOperation implements Serializable {

	@ApiModelProperty(name = "restrictionType", value = "Restriction type of the application.", required = true)
	private String restrictionType;
	@ApiModelProperty(name = "restrictedList", value = "Device id list of the operation to be executed.", required = true)
	private List<String> restrictedList;

	public String getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(String restrictionType) {
		this.restrictionType = restrictionType;
	}

	public List<String> getRestrictedList() {
		return restrictedList;
	}

	public void setRestrictedList(List<String> restrictedList) {
		this.restrictedList = restrictedList;
	}

}
