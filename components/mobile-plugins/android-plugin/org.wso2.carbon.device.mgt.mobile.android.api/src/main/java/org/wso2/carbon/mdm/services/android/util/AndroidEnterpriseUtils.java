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
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseConfigs;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseApp;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.mdm.services.android.exception.NotFoundException;
import org.wso2.carbon.mdm.services.android.exception.UnexpectedServerErrorException;

import javax.ws.rs.core.Response;
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

    public static EnterpriseConfigs getEnterpriseConfigs() {
        PlatformConfiguration configuration;
        try {
            configuration = AndroidAPIUtils.getDeviceManagementService().
                    getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        } catch (DeviceManagementException e) {
            String errorMessage = "Error while fetching tenant configurations for tenant " +
                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            log.error(errorMessage);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());

        }
        String enterpriseId = AndroidDeviceUtils.getAndroidConfig(configuration,"enterpriseId");
        String esa = AndroidDeviceUtils.getAndroidConfig(configuration,"esa");
        if (enterpriseId == null || enterpriseId.isEmpty() || esa == null || esa.isEmpty()) {
            String errorMessage = "Tenant is not configured to handle Android for work. Please contact Entgra.";
            log.error(errorMessage);
            throw new NotFoundException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage(errorMessage).build());
        }
        EnterpriseConfigs enterpriseConfigs = new EnterpriseConfigs();
        enterpriseConfigs.setEnterpriseId(enterpriseId);
        enterpriseConfigs.setEsa(esa);
        return enterpriseConfigs;
    }

}
