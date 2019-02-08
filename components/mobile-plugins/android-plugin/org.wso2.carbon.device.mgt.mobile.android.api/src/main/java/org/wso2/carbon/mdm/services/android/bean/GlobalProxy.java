/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.mdm.services.android.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * This class represents the information of setting up global proxy
 */

@ApiModel(
        value = "GlobalProxy",
        description = "This class represents the information of setting up global proxy"
)
public class GlobalProxy extends AndroidOperation implements Serializable {

    @ApiModelProperty(
            name = "proxyConfigType",
            value = "Type of the proxy",
            required = true
    )
    private ProxyType proxyConfigType;

    @ApiModelProperty(
            name = "proxyHost",
            value = "The hostname of the proxy server"
    )
    private String proxyHost;

    @ApiModelProperty(
            name = "proxyPort",
            value = "The port which the proxy server is running"
    )
    private int proxyPort;

    @ApiModelProperty(
            name = "proxyExclList",
            value = "Hosts to exclude using the proxy on connections for. These hosts can use wildcards such as " +
                    "*.example.com"
    )
    private String proxyExclList;

    @ApiModelProperty(
            name = "proxyPacUrl",
            value = "PAC file URL to auto config proxy"
    )
    private String proxyPacUrl;

    public boolean validateRequest() {
        if (ProxyType.MANUAL.equals(this.proxyConfigType)) {
            if (StringUtils.isEmpty(this.proxyHost)) {
                return false;
            }
            if (this.proxyPort < 0 || this.proxyPort > 65535) {
                return false;
            }
        } else if (ProxyType.AUTO.equals(this.proxyConfigType)) {
            if (StringUtils.isEmpty(proxyPacUrl)) {
                return false;
            }
        }
        return false;
    }

    public ProxyType getProxyConfigType() {
        return proxyConfigType;
    }

    public void setProxyConfigType(ProxyType proxyConfigType) {
        this.proxyConfigType = proxyConfigType;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyExclList() {
        return proxyExclList;
    }

    public void setProxyExclList(String proxyExclList) {
        this.proxyExclList = proxyExclList;
    }

    public String getProxyPacUrl() {
        return proxyPacUrl;
    }

    public void setProxyPacUrl(String proxyPacUrl) {
        this.proxyPacUrl = proxyPacUrl;
    }
}
