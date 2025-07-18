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
package io.entgra.device.mgt.plugins.output.adapter.mqtt;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import io.entgra.device.mgt.plugins.output.adapter.mqtt.util.MQTTAdapterPublisher;
import io.entgra.device.mgt.plugins.output.adapter.mqtt.util.MQTTEventAdapterConstants;
import io.entgra.device.mgt.plugins.output.adapter.mqtt.util.MQTTBrokerConnectionConfiguration;
import org.wso2.carbon.event.output.adapter.core.EventAdapterUtil;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapter;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterConfiguration;
import org.wso2.carbon.event.output.adapter.core.exception.OutputEventAdapterException;
import org.wso2.carbon.event.output.adapter.core.exception.TestConnectionNotSupportedException;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Output MQTTEventAdapter will be used to publish events with MQTT protocol to specified broker and topic.
 */
public class MQTTEventAdapter implements OutputEventAdapter {

    private OutputEventAdapterConfiguration eventAdapterConfiguration;
    private Map<String, String> globalProperties;
    private MQTTAdapterPublisher mqttAdapterPublisher;
    private int connectionKeepAliveInterval;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final Log log = LogFactory.getLog(MQTTEventAdapter.class);
    private int tenantId;
    private MQTTBrokerConnectionConfiguration mqttBrokerConnectionConfiguration;

    public MQTTEventAdapter(OutputEventAdapterConfiguration eventAdapterConfiguration,
                            Map<String, String> globalProperties) {
        this.eventAdapterConfiguration = eventAdapterConfiguration;
        this.globalProperties = globalProperties;
        Object keeAliveInternal = globalProperties.get(MQTTEventAdapterConstants.CONNECTION_KEEP_ALIVE_INTERVAL);
        if (keeAliveInternal != null) {
            try {
                connectionKeepAliveInterval = Integer.parseInt(keeAliveInternal.toString());
            } catch (NumberFormatException e) {
                log.error("Error when configuring user specified connection keep alive time, using default value", e);
                connectionKeepAliveInterval = MQTTEventAdapterConstants.DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL;
            }
        } else {
            connectionKeepAliveInterval = MQTTEventAdapterConstants.DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL;
        }
    }

    @Override
    public void init() throws OutputEventAdapterException {
        tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        //ThreadPoolExecutor will be assigned  if it is null
        if (threadPoolExecutor == null) {
            int minThread;
            int maxThread;
            int jobQueSize;
            long defaultKeepAliveTime;
            //If global properties are available those will be assigned else constant values will be assigned
            if (globalProperties.get(MQTTEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE_NAME) != null) {
                minThread = Integer.parseInt(globalProperties.get(
                        MQTTEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE_NAME));
            } else {
                minThread = MQTTEventAdapterConstants.DEFAULT_MIN_THREAD_POOL_SIZE;
            }

            if (globalProperties.get(MQTTEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE_NAME) != null) {
                maxThread = Integer.parseInt(globalProperties.get(
                        MQTTEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE_NAME));
            } else {
                maxThread = MQTTEventAdapterConstants.DEFAULT_MAX_THREAD_POOL_SIZE;
            }

            if (globalProperties.get(MQTTEventAdapterConstants.ADAPTER_KEEP_ALIVE_TIME_NAME) != null) {
                defaultKeepAliveTime = Integer.parseInt(globalProperties.get(
                        MQTTEventAdapterConstants.ADAPTER_KEEP_ALIVE_TIME_NAME));
            } else {
                defaultKeepAliveTime = MQTTEventAdapterConstants.DEFAULT_KEEP_ALIVE_TIME_IN_MILLIS;
            }

            if (globalProperties.get(MQTTEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME) != null) {
                jobQueSize = Integer.parseInt(globalProperties.get(
                        MQTTEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME));
            } else {
                jobQueSize = MQTTEventAdapterConstants.DEFAULT_EXECUTOR_JOB_QUEUE_SIZE;
            }

            threadPoolExecutor = new ThreadPoolExecutor(minThread, maxThread, defaultKeepAliveTime,
                                                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
                    jobQueSize));
        }
    }

    @Override
    public void testConnect() throws TestConnectionNotSupportedException {
        throw new TestConnectionNotSupportedException("Test connection is not available");
    }

    @Override
    public void connect() {
        mqttBrokerConnectionConfiguration =
                new MQTTBrokerConnectionConfiguration(eventAdapterConfiguration, globalProperties);
        String clientId = eventAdapterConfiguration.getStaticProperties().get(
                MQTTEventAdapterConstants.ADAPTER_CONF_CLIENTID);

        mqttAdapterPublisher = new MQTTAdapterPublisher(mqttBrokerConnectionConfiguration, clientId, tenantId);
    }

    @Override
    public void publish(Object message, Map<String, String> dynamicProperties) {
        String topic = dynamicProperties.get(MQTTEventAdapterConstants.ADAPTER_MESSAGE_TOPIC);
        try {
            threadPoolExecutor.submit(new MQTTSender(topic, message));
        } catch (RejectedExecutionException e) {
            EventAdapterUtil.logAndDrop(eventAdapterConfiguration.getName(), message, "Job queue is full", e, log,
                                        tenantId);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (mqttAdapterPublisher != null) {
                mqttAdapterPublisher.close();
                mqttAdapterPublisher = null;
            }
        } catch (OutputEventAdapterException e) {
            log.error("Exception when closing the mqtt publisher connection on Output MQTT Adapter '" +
                              eventAdapterConfiguration.getName() + "'", e);
        }
    }

    @Override
    public void destroy() {
        //not required
    }

    @Override
    public boolean isPolled() {
        return false;
    }

    public static String convertToJsonString(Object message) {
        // If the message is a Map (like LinkedTreeMap or TreeMap)
        if (message instanceof Map) {
            // Convert the LinkedTreeMap to a JSON string using Gson
            Gson gson = new Gson();
            return gson.toJson(message);  // Convert Map or TreeMap to JSON
        }
        // If it's a simple string or other type, return it as-is
        return message.toString();  // Return the message directly as a string
    }

    class MQTTSender implements Runnable {

        String topic;
        Object message;

        MQTTSender(String topic, Object message) {
            this.topic = topic;
            this.message = message;
        }

        @Override
        public void run() {
            try {
                if (!mqttAdapterPublisher.isConnected()) {
                    synchronized (MQTTEventAdapter.class) {
                        if (!mqttAdapterPublisher.isConnected()) {
                            mqttAdapterPublisher.connect();
                        }
                    }
                }
                // Convert and publish the message
                String formattedMessage = convertToJsonString(message);
                mqttAdapterPublisher.publish(mqttBrokerConnectionConfiguration.getQos(), formattedMessage, topic);
            } catch (Throwable t) {
                EventAdapterUtil.logAndDrop(eventAdapterConfiguration.getName(), message, null, t, log, tenantId);
            }
        }
    }
}
