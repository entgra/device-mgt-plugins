/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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
 *
 */
package org.wso2.carbon.mdm.services.android.bean.wrapper;

import java.io.Serializable;
import java.util.List;

public class EnterpriseInstallPolicy implements Serializable {
    private static final long serialVersionUID = 15598101712L;

    String username;
    String managementType;
    String kind;
    String androidId;
    String autoUpdatePolicy;
    String productAvailabilityPolicy;
    String productSetBehavior;
    List<EnterpriseApp> apps;

    public String getProductSetBehavior() {
        return productSetBehavior;
    }

    public void setProductSetBehavior(String productSetBehavior) {
        this.productSetBehavior = productSetBehavior;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getManagementType() {
        return managementType;
    }

    public void setManagementType(String managementType) {
        this.managementType = managementType;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getAutoUpdatePolicy() {
        return autoUpdatePolicy;
    }

    public void setAutoUpdatePolicy(String autoUpdatePolicy) {
        this.autoUpdatePolicy = autoUpdatePolicy;
    }

    public String getProductAvailabilityPolicy() {
        return productAvailabilityPolicy;
    }

    public void setProductAvailabilityPolicy(String productAvailabilityPolicy) {
        this.productAvailabilityPolicy = productAvailabilityPolicy;
    }

    public List<EnterpriseApp> getApps() {
        return apps;
    }

    public void setApps(List<EnterpriseApp> apps) {
        this.apps = apps;
    }

}
