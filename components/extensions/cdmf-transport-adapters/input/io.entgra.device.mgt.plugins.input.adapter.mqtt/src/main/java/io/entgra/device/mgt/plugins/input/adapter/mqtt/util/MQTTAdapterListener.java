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
package io.entgra.device.mgt.plugins.input.adapter.mqtt.util;

import io.entgra.device.mgt.core.apimgt.application.extension.bean.ApiApplicationKey;
import io.entgra.device.mgt.core.apimgt.application.extension.bean.ApiApplicationProfile;
import io.entgra.device.mgt.core.apimgt.application.extension.bean.Token;
import io.entgra.device.mgt.core.apimgt.application.extension.bean.TokenCreationProfile;
import io.entgra.device.mgt.core.apimgt.application.extension.exception.APIManagerException;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.BadRequestException;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.UnexpectedResponseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.ServerStatus;
import org.wso2.carbon.core.multitenancy.utils.TenantAxisUtils;
import io.entgra.device.mgt.plugins.input.adapter.extension.ContentInfo;
import io.entgra.device.mgt.plugins.input.adapter.extension.ContentTransformer;
import io.entgra.device.mgt.plugins.input.adapter.extension.ContentValidator;
import io.entgra.device.mgt.plugins.input.adapter.mqtt.internal.InputAdapterServiceDataHolder;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterListener;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterRuntimeException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.HashMap;
import java.util.Map;

public class MQTTAdapterListener implements MqttCallback, Runnable {
    private static final Log log = LogFactory.getLog(MQTTAdapterListener.class);

    private MqttClient mqttClient;
    private MqttConnectOptions connectionOptions;
    private boolean cleanSession;
    private boolean connectionInitialized;

    private MQTTBrokerConnectionConfiguration mqttBrokerConnectionConfiguration;
    private String topic;
    private int qos;
    private String topicStructure;
    private String tenantDomain;
    private volatile boolean connectionSucceeded = false;
    private ContentValidator contentValidator;
    private ContentTransformer contentTransformer;
    private InputEventAdapterConfiguration inputEventAdapterConfiguration;

    private InputEventAdapterListener eventAdapterListener = null;

    public MQTTAdapterListener(MQTTBrokerConnectionConfiguration mqttBrokerConnectionConfiguration,
                               String topic, InputEventAdapterConfiguration inputEventAdapterConfiguration,
                               InputEventAdapterListener inputEventAdapterListener) {
        String mqttClientId = inputEventAdapterConfiguration.getProperties()
                .get(MQTTEventAdapterConstants.ADAPTER_CONF_CLIENTID);
        if (mqttClientId == null || mqttClientId.trim().isEmpty()) {
            mqttClientId = MqttClient.generateClientId();
        }
        this.inputEventAdapterConfiguration = inputEventAdapterConfiguration;
        this.mqttBrokerConnectionConfiguration = mqttBrokerConnectionConfiguration;
        this.cleanSession = mqttBrokerConnectionConfiguration.isCleanSession();
        int keepAlive = mqttBrokerConnectionConfiguration.getKeepAlive();
        this.topicStructure = new String(topic);
        this.topic = PropertyUtils.replacePlaceholders(topic);
        this.qos = mqttBrokerConnectionConfiguration.getQos();
        this.eventAdapterListener = inputEventAdapterListener;
        this.tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();

        //SORTING messages until the server fetches them
        String temp_directory = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(temp_directory);

        try {
            connectionOptions = new MqttConnectOptions();
            connectionOptions.setCleanSession(cleanSession);
            connectionOptions.setKeepAliveInterval(keepAlive);

            // Construct an MQTT blocking mode client
            mqttClient = new MqttClient(this.mqttBrokerConnectionConfiguration.getBrokerUrl(), mqttClientId,
                    dataStore);

            // Set this wrapper as the callback handler
            mqttClient.setCallback(this);
            String contentValidatorType = this.mqttBrokerConnectionConfiguration.getContentValidatorType();

            if (contentValidatorType == null || contentValidatorType.equals(MQTTEventAdapterConstants.DEFAULT)) {
                contentValidator = InputAdapterServiceDataHolder.getInputAdapterExtensionService()
                        .getDefaultContentValidator();
            } else {
                contentValidator = InputAdapterServiceDataHolder.getInputAdapterExtensionService()
                        .getContentValidator(contentValidatorType);
            }

            String contentTransformerType = this.mqttBrokerConnectionConfiguration.getContentTransformerType();
            if (contentTransformerType == null || contentTransformerType.equals(MQTTEventAdapterConstants.DEFAULT)) {
                contentTransformer = InputAdapterServiceDataHolder.getInputAdapterExtensionService()
                        .getDefaultContentTransformer();
            } else {
                contentTransformer = InputAdapterServiceDataHolder.getInputAdapterExtensionService()
                        .getContentTransformer(contentTransformerType);
            }
        } catch (MqttException e) {
            log.error("Exception occurred while creating an mqtt client to "
                    + mqttBrokerConnectionConfiguration.getBrokerUrl() + " reason code:" + e.getReasonCode());
            throw new InputEventAdapterRuntimeException(e);
        }
    }

    public boolean startListener() throws MqttException {
        if (this.mqttBrokerConnectionConfiguration.getUsername() != null &&
                this.mqttBrokerConnectionConfiguration.getDcrUrl() != null) {
            String dcrUrlString = this.mqttBrokerConnectionConfiguration.getDcrUrl();

            if (dcrUrlString != null && !dcrUrlString.isEmpty()) {
                try {
                    final String applicationName = MQTTEventAdapterConstants.APPLICATION_NAME_PREFIX
                            + mqttBrokerConnectionConfiguration.getAdapterName();
                    ApiApplicationProfile apiApplicationProfile = new ApiApplicationProfile();
                    apiApplicationProfile.setTokenType(ApiApplicationProfile.TOKEN_TYPE.DEFAULT);
                    apiApplicationProfile.setGrantTypes("client_credentials password refresh_token authorization_code");
                    apiApplicationProfile.setTags(new String[]{"device_management"});
                    apiApplicationProfile.setCallbackUrl(dcrUrlString);
                    apiApplicationProfile.setApplicationName(applicationName);
                    ApiApplicationKey apiApplicationKey =
                            InputAdapterServiceDataHolder.getApiManagementProviderService().registerApiApplication(apiApplicationProfile);

                    if (apiApplicationKey == null) {
                        String msg = "Null received for the created api application : [ " + applicationName + " ]";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }

                    String accessToken = getToken(apiApplicationKey.getClientId(), apiApplicationKey.getClientSecret());
                    connectionOptions.setUserName(accessToken.substring(0, 18));
                    connectionOptions.setPassword(accessToken.substring(18).toCharArray());
                } catch (APIManagerException e) {
                    log.error("Failed to create an oauth token with client_credentials grant type.", e);
                    return false;
                } catch (BadRequestException e) {
                    log.error("Application profile contains invalid attributes", e);
                    return false;
                } catch (UnexpectedResponseException e) {
                    log.error("Unexpected error encountered while registering api application", e);
                    return false;
                }
            }
        }

        try {
            mqttClient.connect(connectionOptions);
        } catch (MqttException e) {
            log.warn("Broker is unreachable, Waiting.....");
            return false;
        }
        try {
            mqttClient.subscribe(topic, qos);
            log.info("mqtt receiver subscribed to topic: " + topic);
        } catch (MqttException e) {
            log.error("Failed to subscribe to topic: " + topic + ", Retrying.....");
            try {
                mqttClient.disconnect();
            } catch (MqttException ex) {
                // do nothing.
            }
            return false;
        }
        return true;

    }

    public void stopListener(String adapterName) {
        if (connectionSucceeded) {
            try {
                if (!ServerStatus.getCurrentStatus().equals(ServerStatus.STATUS_SHUTTING_DOWN) || cleanSession) {
                    mqttClient.unsubscribe(topic);
                }
                mqttClient.disconnect(3000);
            } catch (MqttException e) {
                log.error("Can not unsubscribe from the destination " + topic +
                        " with the event adapter " + adapterName, e);
            }
        }
        connectionSucceeded = true;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("MQTT connection not reachable " + throwable);
        connectionSucceeded = false;
        new Thread(this).start();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        try {
            String mqttMsgString =  mqttMessage.toString();
            String msgText = mqttMsgString.substring(mqttMsgString.indexOf("{"), mqttMsgString.lastIndexOf("}") + 1);
            if (log.isDebugEnabled()) {
                log.debug(msgText);
            }
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
            if (!tenantDomain.equalsIgnoreCase(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
                TenantAxisUtils.getTenantConfigurationContext(tenantDomain, InputAdapterServiceDataHolder.getMainServerConfigContext());
            }

            InputEventAdapterListener inputEventAdapterListener = InputAdapterServiceDataHolder
                    .getInputEventAdapterService().getInputAdapterRuntime(PrivilegedCarbonContext.getThreadLocalCarbonContext()
                            .getTenantId(), inputEventAdapterConfiguration.getName());

            if (log.isDebugEnabled()) {
                log.debug("Event received in MQTT Event Adapter - " + msgText);
            }


            if (contentValidator != null && contentTransformer != null) {
                ContentInfo contentInfo;
                Map<String, Object> dynamicProperties = new HashMap<>();
                dynamicProperties.put(MQTTEventAdapterConstants.TOPIC, topic);
                int deviceIdIndex = -1;
                for (String topicStructurePart : topicStructure.split("/")) {
                    deviceIdIndex++;
                    if (topicStructurePart.contains("+")) {
                        dynamicProperties.put(MQTTEventAdapterConstants.DEVICE_ID_INDEX, deviceIdIndex);
                    }
                }
                Object transformedMessage = contentTransformer.transform(msgText, dynamicProperties);
                contentInfo = contentValidator.validate(transformedMessage, dynamicProperties);
                if (contentInfo != null && contentInfo.isValidContent()) {
                    inputEventAdapterListener.onEvent(contentInfo.getMessage());
                }
            } else {
                inputEventAdapterListener.onEvent(msgText);
            }
        } catch (Exception ex) {
          log.error("Error in message arrive : ", ex);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void run() {
        int connectionDuration = MQTTEventAdapterConstants.INITIAL_RECONNECTION_DURATION;
        while (!connectionSucceeded) {
            try {
                connectionDuration = connectionDuration * MQTTEventAdapterConstants.RECONNECTION_PROGRESS_FACTOR;
                if (connectionDuration > MQTTEventAdapterConstants.MAXIMUM_RECONNECTION_DURATION) {
                    connectionDuration = MQTTEventAdapterConstants.MAXIMUM_RECONNECTION_DURATION;
                }
                Thread.sleep(connectionDuration);
                if (startListener()) {
                    connectionSucceeded = true;
                    log.info("MQTT Connection successful");
                }
            } catch (InterruptedException e) {
                log.error("Interruption occurred while waiting for reconnection", e);
            } catch (MqttException e) {
                log.error("MQTT Exception occurred when starting listener", e);
            }
        }
    }

    public void createConnection() {
        connectionInitialized = true;
        new Thread(this).start();
    }

    public boolean isConnectionInitialized() {
        return connectionInitialized;
    }

    private String getToken(String clientId, String clientSecret)
            throws APIManagerException {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
        try {
            String scopes = mqttBrokerConnectionConfiguration.getBrokerScopes();
            scopes += " perm:topic:sub:" + this.topic.replace("/",":");

            TokenCreationProfile tokenCreationProfile = new TokenCreationProfile();
            tokenCreationProfile.setGrantType("client_credentials");
            tokenCreationProfile.setBasicAuthUsername(clientId);
            tokenCreationProfile.setBasicAuthPassword(clientSecret);
            tokenCreationProfile.setScope(scopes);
            Token token =
                    InputAdapterServiceDataHolder.getApiManagementProviderService().getToken(tokenCreationProfile);
            if (token == null) {
                String msg = "Null received for the token request. Failed to start listening process";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            return token.getAccessToken();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private String getBase64Encode(String key, String value) {
        return new String(Base64.encodeBase64((key + ":" + value).getBytes()));
    }
}
