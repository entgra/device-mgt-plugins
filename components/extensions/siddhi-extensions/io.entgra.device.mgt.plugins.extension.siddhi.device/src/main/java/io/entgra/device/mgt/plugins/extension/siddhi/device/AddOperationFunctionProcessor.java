/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package io.entgra.device.mgt.plugins.extension.siddhi.device;

import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Activity;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.entgra.device.mgt.plugins.extension.siddhi.device.utils.DeviceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.query.processor.stream.function.StreamFunctionProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddOperationFunctionProcessor extends StreamFunctionProcessor {

    private static final Log log = LogFactory.getLog(AddOperationFunctionProcessor.class);
    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static final String DEVICE_MGT_BASE_CONTEXT = "/api/device-mgt/v1.0";

    /**
     * The init method of the StreamProcessor, this method will be called before other methods
     *
     * @param abstractDefinition           the incoming stream definition
     * @param attributeExpressionExecutors the executors of each function parameters
     * @param executionPlanContext         the context of the execution plan
     * @return the additional output attributes introduced by the function
     */
    @Override
    protected List<Attribute> init(AbstractDefinition abstractDefinition,
                                   ExpressionExecutor[] attributeExpressionExecutors,
                                   ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 6) {
            throw new ExecutionPlanValidationException(
                    "Invalid no of arguments passed to device:addOperation() function, required 3 but found " +
                            attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0].getReturnType() != Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the first argument (deviceIdentifiers) of device:addOperation() " +
                            "function, required " + Attribute.Type.STRING + " as deviceIdentifiers, but found " +
                            attributeExpressionExecutors[0].getReturnType().toString());
        }
        if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the second argument (deviceType) of device:addOperation() " +
                            "function, required " + Attribute.Type.STRING + " as deviceType, but found " +
                            attributeExpressionExecutors[1].getReturnType().toString());
        }
        if (attributeExpressionExecutors[2].getReturnType() != Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the third argument (code) of device:addOperation() " +
                            "function, required " + Attribute.Type.STRING + " as code, but found " +
                            attributeExpressionExecutors[2].getReturnType().toString());
        }
        if (attributeExpressionExecutors[3].getReturnType() != Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the fourth argument (type) of device:addOperation() " +
                            "function, required " + Attribute.Type.STRING + " as type, but found " +
                            attributeExpressionExecutors[3].getReturnType().toString());
        }
        if (attributeExpressionExecutors[4].getReturnType() != Attribute.Type.BOOL) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the fifth argument (isEnabled) of device:addOperation() " +
                            "function, required " + Attribute.Type.BOOL + " as isEnabled, but found " +
                            attributeExpressionExecutors[4].getReturnType().toString());
        }
        if (attributeExpressionExecutors[5].getReturnType() != Attribute.Type.STRING) {
            throw new ExecutionPlanValidationException(
                    "Invalid parameter type found for the fifth argument (payLoad) of device:addOperation() " +
                            "function, required " + Attribute.Type.STRING + " as payLoad, but found " +
                            attributeExpressionExecutors[5].getReturnType().toString());
        }
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("activity_id", Attribute.Type.STRING));
        return attributes;
    }

    /**
     * The process method of the StreamFunction, used when more than one function parameters are provided
     *
     * @param data the data values for the function parameters
     * @return the data for additional output attributes introduced by the function
     */
    @Override
    protected Object[] process(Object[] data) {
        if (data[0] == null || data[1] == null || data[2] == null || data[3] == null || data[4] == null || data[5] == null) {
            throw new ExecutionPlanRuntimeException("Invalid input given to device:addOperation() function. " +
                    "Neither of any three arguments cannot be null");
        }

        JSONArray deviceIds = new JSONArray((String) data[0]);
        String deviceType = (String) data[1];
        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
        for (int i = 0; i < deviceIds.length(); i++) {
            deviceIdentifiers.add(new DeviceIdentifier(deviceIds.getString(i), deviceType));
        }

        Operation operation = new Operation();
        operation.setType(Operation.Type.valueOf((String) data[3]));
        operation.setStatus(Operation.Status.PENDING);
        operation.setCode((String) data[2]);
        operation.setEnabled((Boolean) data[4]);
        String payloadString = (String) data[5];
        operation.setPayLoad(payloadString.replaceAll("'", "\""));

        String date = new SimpleDateFormat(DATE_FORMAT_NOW).format(new Date());
        operation.setCreatedTimeStamp(date);

        try {
            Activity activity = DeviceUtils.getDeviceManagementProviderService().addOperation(deviceType, operation, deviceIdentifiers);
            return new Object[]{activity.getActivityId()};
        } catch (Exception e) {
            log.error("Error occurred while adding the operation " + operation.toString(), e);
            return new Object[]{null};
        }
    }

    /**
     * The process method of the StreamFunction, used when zero or one function parameter is provided
     *
     * @param data null if the function parameter count is zero or runtime data value of the function parameter
     * @return the data for additional output attribute introduced by the function
     */
    @Override
    protected Object[] process(Object data) {
        return new Object[0];
    }

    /**
     * This will be called only once and this can be used to acquire
     * required resources for the processing element.
     * This will be called after initializing the system and before
     * starting to process the events.
     */
    @Override
    public void start() {

    }

    /**
     * This will be called only once and this can be used to release
     * the acquired resources for processing.
     * This will be called before shutting down the system.
     */
    @Override
    public void stop() {

    }

    /**
     * Used to collect the serializable state of the processing element, that need to be
     * persisted for the reconstructing the element to the same state on a different point of time
     *
     * @return stateful objects of the processing element as an array
     */
    @Override
    public Object[] currentState() {
        return new Object[0];
    }

    /**
     * Used to restore serialized state of the processing element, for reconstructing
     * the element to the same state as if was on a previous point of time.
     *
     * @param objects the stateful objects of the element as an array on
     *                the same order provided by currentState().
     */
    @Override
    public void restoreState(Object[] objects) {
        //Since there's no need to maintain a state, nothing needs to be done here.
    }
}
