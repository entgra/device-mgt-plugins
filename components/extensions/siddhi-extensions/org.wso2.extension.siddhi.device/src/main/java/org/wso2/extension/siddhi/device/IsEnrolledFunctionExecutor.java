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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.extension.siddhi.device.utils.DeviceUtils;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

/**
 * isEnrolled(deviceId, deviceType)
 * Returns true if device enrolled.
 * Accept Type(s): (STRING, STRING)
 * Return Type(s): (BOOL)
 */
public class IsEnrolledFunctionExecutor extends FunctionExecutor {

    private static Log log = LogFactory.getLog(IsEnrolledFunctionExecutor.class);
    private Attribute.Type returnType = Attribute.Type.BOOL;

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors,
            ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new ExecutionPlanValidationException(
                    "Invalid no of arguments passed to device:isEnrolled() function, required 2, but found "
                            + attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0].getReturnType()!= Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the first argument (deviceId) of device:isEnrolled() " +
                    "function, required " + Attribute.Type.STRING + ", but found " +
                    attributeExpressionExecutors[0].getReturnType().toString());
        }
        if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the second argument (deviceType) of device:isEnrolled() " +
                    "function, required " + Attribute.Type.STRING + ", but found " +
                    attributeExpressionExecutors[1].getReturnType().toString());
        }
    }

    @Override
    protected Object execute(Object[] data) {
        if (data[0] == null) {
            throw new ExecutionPlanRuntimeException("Invalid input given to device:isEnrolled() function. " +
                                                    "First argument cannot be null");
        }
        if (data[1] == null) {
            throw new ExecutionPlanRuntimeException("Invalid input given to device:isEnrolled() function. " +
                                                    "Second argument cannot be null");
        }

        String deviceId = (String) data[0];
        String deviceType = (String) data[1];

        DeviceIdentifier deviceIdentifier = new DeviceIdentifier(deviceId, deviceType);
        try {
            DeviceManagementProviderService deviceManagementService = DeviceUtils.getDeviceManagementProviderService();
            return deviceManagementService.isEnrolled(deviceIdentifier);
        } catch (DeviceManagementException e) {
            log.error("Error occurred while checking device is enrolled.", e);
        }
        return false;
    }

    @Override
    protected Object execute(Object data) {
        return null;  //Since the getDevicesOfUser function takes in 2 parameters, this method does not get called. Hence,not implemented.
    }

    @Override
    public void start() {
        //Nothing to start
    }

    @Override
    public void stop() {
        //Nothing to stop
    }

    @Override
    public Attribute.Type getReturnType() {
        return returnType;
    }

    @Override
    public Object[] currentState() {
        return null;    //No need to maintain a state.
    }

    @Override
    public void restoreState(Object[] state) {
        //Since there's no need to maintain a state, nothing needs to be done here.
    }
}
