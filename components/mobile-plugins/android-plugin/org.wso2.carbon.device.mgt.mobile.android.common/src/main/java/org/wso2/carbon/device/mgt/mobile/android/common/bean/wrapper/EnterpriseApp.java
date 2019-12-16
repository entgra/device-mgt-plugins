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
package org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper;

import com.google.api.services.androidenterprise.model.VariableSet;

import java.io.Serializable;
import java.util.List;

public class EnterpriseApp implements Serializable {
    private static final long serialVersionUID = 45598101734L;

    String productId;
    String autoInstallMode;
    int autoInstallPriority;
    String chargingStateConstraint;
    String deviceIdleStateConstraint;
    String networkTypeConstraint;

    String mcmId;
    List<VariableSet> variableSet;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAutoInstallMode() {
        return autoInstallMode;
    }

    public void setAutoInstallMode(String autoInstallMode) {
        this.autoInstallMode = autoInstallMode;
    }

    public int getAutoInstallPriority() {
        return autoInstallPriority;
    }

    public void setAutoInstallPriority(int autoInstallPriority) {
        this.autoInstallPriority = autoInstallPriority;
    }

    public String getChargingStateConstraint() {
        return chargingStateConstraint;
    }

    public void setChargingStateConstraint(String chargingStateConstraint) {
        this.chargingStateConstraint = chargingStateConstraint;
    }

    public String getDeviceIdleStateConstraint() {
        return deviceIdleStateConstraint;
    }

    public void setDeviceIdleStateConstraint(String deviceIdleStateConstraint) {
        this.deviceIdleStateConstraint = deviceIdleStateConstraint;
    }

    public String getNetworkTypeConstraint() {
        return networkTypeConstraint;
    }

    public void setNetworkTypeConstraint(String networkTypeConstraint) {
        this.networkTypeConstraint = networkTypeConstraint;
    }

    public String getMcmId() {
        return mcmId;
    }

    public void setMcmId(String mcmId) {
        this.mcmId = mcmId;
    }

    public List<VariableSet> getVariableSet() {
        return variableSet;
    }

    public void setVariableSet(List<VariableSet> variableSet) {
        this.variableSet = variableSet;
    }
}