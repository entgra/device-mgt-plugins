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

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the blacklist applications information.
 */
@ApiModel(value = "BlacklistApplications",
        description = "This class represents the blacklist applications information.")
public class BlacklistApplications extends AndroidOperation implements Serializable {

    @ApiModelProperty(name = "appIdentifiers", value = "A list of application package names to be blacklisted.",
            required = true)
	@Size(min = 1, max = 45)
	private List<String> appIdentifiers;

	public List<String> getAppIdentifier() {
		return appIdentifiers;
	}

	public void setAppIdentifier(List<String> appIdentifiers) {
		this.appIdentifiers = appIdentifiers;
	}

}
