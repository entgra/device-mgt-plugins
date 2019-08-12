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
 */

package org.wso2.carbon.mdm.services.android.util;

import com.google.api.services.androidenterprise.model.AutoInstallConstraint;
import com.google.api.services.androidenterprise.model.AutoInstallPolicy;
import com.google.api.services.androidenterprise.model.Device;
import com.google.api.services.androidenterprise.model.Policy;
import com.google.api.services.androidenterprise.model.ProductPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseApp;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseInstallPolicy;

import java.util.ArrayList;
import java.util.List;

public class AndroidEnterpriseUtils {

    private static Log log = LogFactory.getLog(AndroidEnterpriseUtils.class);

    public static Device convertToDeviceInstance(EnterpriseInstallPolicy enterpriseInstallPolicy) {
        Device device = new Device();
        device.setManagementType(enterpriseInstallPolicy.getManagementType());
        device.setKind(enterpriseInstallPolicy.getKind());
        device.setAndroidId(enterpriseInstallPolicy.getAndroidId());
        Policy policy = new Policy();
        List<ProductPolicy> policyList = new ArrayList<>();

        for (EnterpriseApp app : enterpriseInstallPolicy.getApps()) {
            ProductPolicy productPolicy = new ProductPolicy();
            AutoInstallPolicy autoInstallPolicy = new AutoInstallPolicy();
            autoInstallPolicy.setAutoInstallMode(app.getAutoInstallMode());
            autoInstallPolicy.setAutoInstallPriority(app.getAutoInstallPriority());
            List<AutoInstallConstraint> autoInstallConstraintList = new ArrayList<>();
            AutoInstallConstraint autoInstallConstraint = new AutoInstallConstraint();
            autoInstallConstraint.setChargingStateConstraint(app.getChargingStateConstraint());
            autoInstallConstraint.setDeviceIdleStateConstraint(app.getDeviceIdleStateConstraint());
            autoInstallConstraint.setNetworkTypeConstraint(app.getNetworkTypeConstraint());
            autoInstallConstraintList.add(autoInstallConstraint);
            autoInstallPolicy.setAutoInstallConstraint(autoInstallConstraintList);

            productPolicy.setAutoInstallPolicy(autoInstallPolicy);
            productPolicy.setProductId(app.getProductId());
            policyList.add(productPolicy);
        }

        policy.setProductPolicy(policyList);
        policy.setAutoUpdatePolicy(enterpriseInstallPolicy.getAutoUpdatePolicy());
        policy.setProductAvailabilityPolicy(enterpriseInstallPolicy.getProductAvailabilityPolicy());
        device.setPolicy(policy);
        return device;
    }

}
