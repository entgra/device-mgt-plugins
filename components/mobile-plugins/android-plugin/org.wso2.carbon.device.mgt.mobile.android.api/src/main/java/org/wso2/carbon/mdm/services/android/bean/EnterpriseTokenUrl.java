/*
 * Copyright (c) 2016, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.services.android.bean;

import java.io.Serializable;

public class EnterpriseTokenUrl {

    String enterpriseId;
    String parentHost;
    boolean approveApps;
    boolean searchEnabled;
    boolean isPrivateAppsEnabled;
    boolean isWebAppEnabled;
    boolean isOrganizeAppPageVisible;

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getParentHost() {
        return parentHost;
    }

    public void setParentHost(String parentHost) {
        this.parentHost = parentHost;
    }

    public boolean isApproveApps() {
        return approveApps;
    }

    public void setApproveApps(boolean approveApps) {
        this.approveApps = approveApps;
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    public void setSearchEnabled(boolean searchEnabled) {
        this.searchEnabled = searchEnabled;
    }

    public boolean isPrivateAppsEnabled() {
        return isPrivateAppsEnabled;
    }

    public void setPrivateAppsEnabled(boolean privateAppsEnabled) {
        isPrivateAppsEnabled = privateAppsEnabled;
    }

    public boolean isWebAppEnabled() {
        return isWebAppEnabled;
    }

    public void setWebAppEnabled(boolean webAppEnabled) {
        isWebAppEnabled = webAppEnabled;
    }

    public boolean isOrganizeAppPageVisible() {
        return isOrganizeAppPageVisible;
    }

    public void setOrganizeAppPageVisible(boolean organizeAppPageVisible) {
        isOrganizeAppPageVisible = organizeAppPageVisible;
    }
}
