/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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

package org.wso2.carbon.device.mgt.mobile.config;

import org.wso2.carbon.device.mgt.mobile.common.PluginTypeEnum;
import org.wso2.carbon.device.mgt.mobile.config.datasource.DataSourceConfigAdapter;
import org.wso2.carbon.device.mgt.mobile.config.datasource.MobileDataSourceConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 * Class for holding management repository data.
 */
@XmlRootElement(name = "ManagementRepository")
public class MobileDeviceManagementRepository {

    private Map<String, MobileDataSourceConfig> mobileDataSourceConfigMap;

	public MobileDataSourceConfig getMobileDataSourceConfig(PluginTypeEnum type) {
		return mobileDataSourceConfigMap.get(type.toString());
	}

    @XmlElement(name="DataSourceConfigurations")
    @XmlJavaTypeAdapter(DataSourceConfigAdapter.class)
    public Map<String, MobileDataSourceConfig> getMobileDataSourceConfigMap() {
        return mobileDataSourceConfigMap;
    }

    public void setMobileDataSourceConfigMap(Map<String, MobileDataSourceConfig> mobileDataSourceConfigMap) {
        this.mobileDataSourceConfigMap = mobileDataSourceConfigMap;
    }
}
