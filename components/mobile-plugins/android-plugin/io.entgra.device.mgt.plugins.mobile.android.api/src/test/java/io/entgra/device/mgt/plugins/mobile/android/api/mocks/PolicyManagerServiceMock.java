/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.entgra.device.mgt.plugins.mobile.android.api.mocks;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.Feature;
import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.PaginationResult;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Profile;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ProfileFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.ComplianceFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.NonComplianceData;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import io.entgra.device.mgt.core.policy.mgt.common.FeatureManagementException;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyAdministratorPoint;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyEvaluationPoint;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyInformationPoint;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyManagementException;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyMonitoringTaskException;
import io.entgra.device.mgt.core.policy.mgt.core.PolicyManagerService;
import io.entgra.device.mgt.core.policy.mgt.core.task.TaskScheduleService;

import java.util.List;

public class PolicyManagerServiceMock implements PolicyManagerService {

    @Override
    public Profile addProfile(Profile profile) throws PolicyManagementException {
        return null;
    }

    @Override
    public Profile updateProfile(Profile profile) throws PolicyManagementException {
        return null;
    }

    @Override
    public Policy addPolicy(Policy policy) throws PolicyManagementException {
        return null;
    }

    @Override
    public Policy updatePolicy(Policy policy) throws PolicyManagementException {
        return null;
    }

    @Override
    public boolean deletePolicy(Policy policy) throws PolicyManagementException {
        return false;
    }

    @Override
    public boolean deletePolicy(int i) throws PolicyManagementException {
        return false;
    }

    @Override
    public Policy getEffectivePolicy(DeviceIdentifier deviceIdentifier) throws PolicyManagementException {
        return null;
    }

    @Override
    public List<ProfileFeature> getEffectiveFeatures(DeviceIdentifier deviceIdentifier)
            throws FeatureManagementException {
        return null;
    }

    @Override
    public List<Policy> getPolicies(String s) throws PolicyManagementException {
        return null;
    }

    @Override
    public List<Feature> getFeatures() throws FeatureManagementException {
        return null;
    }

    @Override
    public PolicyAdministratorPoint getPAP() throws PolicyManagementException {
        return null;
    }

    @Override
    public PolicyInformationPoint getPIP() throws PolicyManagementException {
        return null;
    }

    @Override
    public PolicyEvaluationPoint getPEP() throws PolicyManagementException {
        return null;
    }

    @Override
    public TaskScheduleService getTaskScheduleService() throws PolicyMonitoringTaskException {
        return null;
    }

    @Override
    public int getPolicyCount() throws PolicyManagementException {
        return 0;
    }

    @Override
    public Policy getAppliedPolicyToDevice(DeviceIdentifier deviceIdentifier) throws PolicyManagementException {
        return null;
    }

    @Override
    public Policy getAppliedPolicyToDevice(Device device) throws PolicyManagementException {
        return null;
    }

    @Override
    public List<ComplianceFeature> checkPolicyCompliance(DeviceIdentifier deviceIdentifier, Object o)
            throws PolicyComplianceException {
        return null;
    }

    @Override
    public List<ComplianceFeature> checkPolicyCompliance(Device device, Object o) throws PolicyComplianceException {
        return null;
    }

    @Override
    public boolean checkCompliance(DeviceIdentifier deviceIdentifier, Object o) throws PolicyComplianceException {
        return false;
    }

    @Override
    public boolean checkCompliance(Device device, Object o) throws PolicyComplianceException {
        return false;
    }

    @Override
    public NonComplianceData getDeviceCompliance(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {
        return null;
    }

    @Override
    public NonComplianceData getDeviceCompliance(Device device) throws PolicyComplianceException {
        return null;
    }

    @Override
    public boolean isCompliant(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException {
        return false;
    }

    @Override
    public PaginationResult getPolicyCompliance(PaginationRequest paginationRequest, String s, boolean b, boolean b1, String s1, String s2) throws PolicyComplianceException {
        return null;
    }

    @Override
    public List<ComplianceFeature> getNoneComplianceFeatures(int i) throws PolicyComplianceException {
        return null;
    }
}
