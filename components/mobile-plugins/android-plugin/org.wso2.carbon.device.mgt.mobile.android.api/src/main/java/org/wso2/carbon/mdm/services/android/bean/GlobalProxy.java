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

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the information of setting up global proxy
 */

@ApiModel(
        value = "GlobalProxy",
        description = "This class represents the information of setting up global proxy"
)
public class GlobalProxy extends AndroidOperation implements Serializable {

    @ApiModelProperty(name = "host", value = "The hostname of the proxy server", required = true)
    private String host;

    @ApiModelProperty(
            name = "port",
            value = "The port which the proxy server is running",
            required = true)
    private int port;

    @ApiModelProperty(
            name = "excludedList",
            value = "Hosts to exclude using the proxy on connections for. These hosts can use wildcards such as " +
                    "*.example.com"
    )
    private List<String> excludedList;

    @ApiModelProperty(
            name = "username",
            value = "Username of the proxy server"
    )
    private String username;

    @ApiModelProperty(
            name = "password",
            value = "Password of the proxy server"
    )
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getExcludedList() {
        return excludedList;
    }

    public void setExcludedList(List<String> excludedList) {
        this.excludedList = excludedList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
