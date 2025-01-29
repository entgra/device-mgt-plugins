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
package io.entgra.device.mgt.plugins.output.adapter.mqtt.util;

import io.entgra.device.mgt.core.apimgt.application.extension.bean.ApiApplicationKey;
import io.entgra.device.mgt.core.apimgt.application.extension.bean.ApiApplicationProfile;
import io.entgra.device.mgt.core.apimgt.application.extension.bean.Token;
import io.entgra.device.mgt.core.apimgt.application.extension.bean.TokenCreationProfile;
import io.entgra.device.mgt.core.apimgt.application.extension.exception.APIManagerException;
import io.entgra.device.mgt.plugins.output.adapter.mqtt.internal.OutputAdapterServiceDataHolder;
import org.apache.commons.lang.StringUtils;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.BadRequestException;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.UnexpectedResponseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.ssl.Base64;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.output.adapter.core.exception.ConnectionUnavailableException;
import org.wso2.carbon.event.output.adapter.core.exception.OutputEventAdapterException;
import org.wso2.carbon.event.output.adapter.core.exception.OutputEventAdapterRuntimeException;

/**
 * MQTT publisher related configuration initialization and publishing capabilties are implemented here.
 */
public class MQTTAdapterPublisher {

    private static final Log log = LogFactory.getLog(MQTTAdapterPublisher.class);
    private MqttClient mqttClient;
    private MQTTBrokerConnectionConfiguration mqttBrokerConnectionConfiguration;
    String clientId;
    int tenantId;

    private String tenantDomain;

    public MQTTAdapterPublisher(MQTTBrokerConnectionConfiguration mqttBrokerConnectionConfiguration, String clientId
            , int tenantId) {
        this.tenantId = tenantId;
        this.tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        if (clientId == null || clientId.trim().isEmpty()) {
            this.clientId = MqttClient.generateClientId();
        }
        this.mqttBrokerConnectionConfiguration = mqttBrokerConnectionConfiguration;
        connect();
    }

    public void connect() {
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = MqttClient.generateClientId();
        }
        boolean cleanSession = mqttBrokerConnectionConfiguration.isCleanSession();
        int keepAlive = mqttBrokerConnectionConfiguration.getKeepAlive();
        String temp_directory = System.getProperty(MQTTEventAdapterConstants.ADAPTER_TEMP_DIRECTORY_NAME);
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(temp_directory);
        try {
            MqttConnectOptions connectionOptions = new MqttConnectOptions();
            connectionOptions.setCleanSession(cleanSession);
            connectionOptions.setKeepAliveInterval(keepAlive);
            if (mqttBrokerConnectionConfiguration.getUsername() != null) {
                String accessToken = getToken();
                connectionOptions.setUserName(accessToken.substring(0, 18));
                connectionOptions.setPassword(accessToken.substring(19).toCharArray());
            }
            // Construct an MQTT blocking mode client
            mqttClient = new MqttClient(mqttBrokerConnectionConfiguration.getBrokerUrl(), clientId, dataStore);
            mqttClient.connect(connectionOptions);

        } catch (MqttException e) {
            log.error("Error occurred when constructing MQTT client for broker url : "
                              + mqttBrokerConnectionConfiguration.getBrokerUrl(), e);
            handleException(e);
        }
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    public void publish(int qos, String payload, String topic) {
        try {
            // Create and configure a message
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            log.error("Error occurred when publishing message for MQTT server : " + mqttClient.getServerURI(), e);
            handleException(e);
        }
    }

    public void publish(String payload, String topic) {
        try {
            // Create and configure a message
            MqttMessage message = new MqttMessage(payload.getBytes());
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            log.error("Error occurred when publishing message for MQTT server : " + mqttClient.getServerURI(), e);
            handleException(e);
        }
    }

    public void close() throws OutputEventAdapterException {
        try {
            mqttClient.disconnect(1000);
            mqttClient.close();
        } catch (MqttException e) {
            throw new OutputEventAdapterException(e);
        }
    }

    private void handleException(MqttException e) {
        //Check for Client not connected exception code and throw ConnectionUnavailableException
        if (e.getReasonCode() == 32104) {
            throw new ConnectionUnavailableException(e);
        } else {
            throw new OutputEventAdapterRuntimeException(e);
        }
    }

    private String getToken() {
        String dcrUrlString = this.mqttBrokerConnectionConfiguration.getDcrUrl();
        final String applicationName = MQTTEventAdapterConstants.APPLICATION_NAME_PREFIX
                        + mqttBrokerConnectionConfiguration.getAdapterName();

        if (dcrUrlString != null && !dcrUrlString.isEmpty()) {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
            try {
                ApiApplicationProfile apiApplicationProfile = new ApiApplicationProfile();
                apiApplicationProfile.setTokenType(ApiApplicationProfile.TOKEN_TYPE.JWT);
                apiApplicationProfile.setGrantTypes("client_credentials password refresh_token authorization_code");
                apiApplicationProfile.setTags(new String[]{"device_management"});
                apiApplicationProfile.setCallbackUrl(dcrUrlString);
                apiApplicationProfile.setApplicationName(applicationName);
                ApiApplicationKey apiApplicationKey =
                        OutputAdapterServiceDataHolder.getApiManagementProviderService().registerApiApplication(apiApplicationProfile);

                if (apiApplicationKey == null) {
                    String msg = "Null received for the created api application : [ " + applicationName + " ]";
                    log.error(msg);
                    throw new IllegalStateException(msg);
                }

                return getToken(apiApplicationKey.getClientId(), apiApplicationKey.getClientSecret());

            } catch (APIManagerException e) {
                String msg = "Failed to create an oauth token with client_credentials grant type.";
                log.error(msg, e);
                throw new OutputEventAdapterRuntimeException(msg, e);
            } catch (BadRequestException e) {
                String msg = "Application profile contains invalid attributes";
                log.error(msg, e);
                throw new OutputEventAdapterRuntimeException(msg, e);
            } catch (UnexpectedResponseException e) {
                String msg = "Unexpected error encountered while registering api application";
                log.error(msg, e);
                throw new OutputEventAdapterRuntimeException(msg, e);
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
        throw new OutputEventAdapterRuntimeException("Invalid configuration for mqtt publisher");
    }

    private String getToken(String clientId, String clientSecret)
            throws APIManagerException {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
        try {
            String scopes = mqttBrokerConnectionConfiguration.getScopes();
            scopes += " perm:topic:pub:" + tenantDomain + ":+:+:operation";

            if (!StringUtils.isEmpty(mqttBrokerConnectionConfiguration.getTopic())) {
                scopes += " perm:topic:pub:" + mqttBrokerConnectionConfiguration.getTopic().replace("/",":");
            }

            TokenCreationProfile tokenCreationProfile = new TokenCreationProfile();
            tokenCreationProfile.setGrantType("client_credentials");
            tokenCreationProfile.setBasicAuthUsername(clientId);
            tokenCreationProfile.setBasicAuthPassword(clientSecret);
            tokenCreationProfile.setScope(scopes);
            Token token =
                    OutputAdapterServiceDataHolder.getApiManagementProviderService().getToken(tokenCreationProfile);
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
