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

package io.entgra.device.mgt.plugins.input.adapter.http.authorization.client;

import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import feign.slf4j.Slf4jLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.ServerConfiguration;
import io.entgra.device.mgt.plugins.input.adapter.http.authorization.client.dto.AccessTokenInfo;
import io.entgra.device.mgt.plugins.input.adapter.http.authorization.client.dto.ApiApplicationKey;
import io.entgra.device.mgt.plugins.input.adapter.http.authorization.client.dto.ApiApplicationRegistrationService;
import io.entgra.device.mgt.plugins.input.adapter.http.authorization.client.dto.ApiRegistrationProfile;
import io.entgra.device.mgt.plugins.input.adapter.http.authorization.client.dto.TokenIssuerService;
import io.entgra.device.mgt.plugins.input.adapter.http.util.PropertyUtils;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterException;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;

/**
 * This is a request interceptor to add oauth token header.
 */
public class OAuthRequestInterceptor implements RequestInterceptor {

    private AccessTokenInfo tokenInfo;
    private long refreshTimeOffset;
    private static final String API_APPLICATION_REGISTRATION_CONTEXT = "/api-application-registration";
    private static final String DEVICE_MANAGEMENT_SERVICE_TAG[] = {"device_management"};
    private static final String APPLICATION_NAME = "websocket-app";
    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String REFRESH_GRANT_TYPE = "refresh_token";
    private static final String REQUIRED_SCOPE = "dm:authorization:verify";
    private ApiApplicationRegistrationService apiApplicationRegistrationService;
    private TokenIssuerService tokenIssuerService;

    private static Log log = LogFactory.getLog(OAuthRequestInterceptor.class);

    private static final String CONNECTION_USERNAME = "username";
    private static final String CONNECTION_PASSWORD = "password";
    private static final String TOKEN_REFRESH_TIME_OFFSET = "tokenRefreshTimeOffset";
    private static final String TOKEN_SCOPES = "scopes";
    private static final String DEVICE_MGT_SERVER_URL = "deviceMgtServerUrl";
    private static final String TOKEN_ENDPOINT_CONTEXT = "tokenUrl";
    private static String username;
    private static String password;
    private static String tokenEndpoint;
    private static String deviceMgtServerUrl;
    private static String scopes;
    private static Map<String, String> globalProperties;
    private ApiApplicationKey apiApplicationKey;

    /**
     * Creates an interceptor that authenticates all requests.
     */
    public OAuthRequestInterceptor(Map<String, String> globalProperties) {
        this.globalProperties = globalProperties;
        try {
            deviceMgtServerUrl = getDeviceMgtServerUrl(globalProperties);
            refreshTimeOffset = getRefreshTimeOffset(globalProperties) * 1000;
            username = getUsername(globalProperties);
            password = getPassword(globalProperties);
            tokenEndpoint = getTokenEndpoint(globalProperties);
            apiApplicationRegistrationService = Feign.builder().client(getSSLClient()).logger(new Slf4jLogger()).logLevel(
                    Logger.Level.FULL).requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                    .contract(new JAXRSContract()).encoder(new GsonEncoder()).decoder(new GsonDecoder())
                    .target(ApiApplicationRegistrationService.class,
                            deviceMgtServerUrl + API_APPLICATION_REGISTRATION_CONTEXT);
        } catch (InputEventAdapterException e) {
            log.error("Invalid url: deviceMgtServerUrl" + deviceMgtServerUrl + " or tokenEndpoint:" + tokenEndpoint,
                         e);
        }
    }

    @Override
    public void apply(RequestTemplate template) {
        if (tokenInfo == null) {
            if (apiApplicationKey == null) {
                ApiRegistrationProfile apiRegistrationProfile = new ApiRegistrationProfile();
                apiRegistrationProfile.setApplicationName(APPLICATION_NAME);
                apiRegistrationProfile.setIsAllowedToAllDomains(false);
                apiRegistrationProfile.setIsMappingAnExistingOAuthApp(false);
                apiRegistrationProfile.setTags(DEVICE_MANAGEMENT_SERVICE_TAG);
                apiApplicationKey = apiApplicationRegistrationService.register(apiRegistrationProfile);
            }
            String consumerKey = apiApplicationKey.getConsumerKey();
            String consumerSecret = apiApplicationKey.getConsumerSecret();
            if (tokenIssuerService == null) {
                tokenIssuerService = Feign.builder().client(getSSLClient())
                        .logger(new Slf4jLogger()).logLevel(Logger.Level.FULL)
                        .requestInterceptor(new BasicAuthRequestInterceptor(consumerKey, consumerSecret))
                        .contract(new JAXRSContract()).encoder(new GsonEncoder()).decoder(new GsonDecoder())
                        .target(TokenIssuerService.class, tokenEndpoint);
            }
            tokenInfo = tokenIssuerService.getToken(PASSWORD_GRANT_TYPE, username, password, REQUIRED_SCOPE);
            tokenInfo.setExpires_in(System.currentTimeMillis() + (tokenInfo.getExpires_in() * 1000));
        } else {
            synchronized (this) {
                if (System.currentTimeMillis() + refreshTimeOffset > tokenInfo.getExpires_in()) {
                    try {
                        tokenInfo = tokenIssuerService.getToken(REFRESH_GRANT_TYPE, tokenInfo.getRefresh_token());
                        tokenInfo.setExpires_in(System.currentTimeMillis() + tokenInfo.getExpires_in());
                    } catch (FeignException e) {
                        tokenInfo = null;
                        apply(template);
                    }
                }
            }
        }
        String headerValue = "Bearer " + tokenInfo.getAccess_token();
        template.header("Authorization", headerValue);
    }

    private String getUsername(Map<String, String> globalProperties) {
        String username = globalProperties.get(CONNECTION_USERNAME);
        if (username == null || username.isEmpty()) {
            log.error("username can't be empty ");
        }
        return username;
    }

    private String getPassword(Map<String, String> globalProperties) {
        String password = globalProperties.get(CONNECTION_PASSWORD);;
        if (password == null || password.isEmpty()) {
            log.error("password can't be empty ");
        }
        return password;
    }

    private String getDeviceMgtServerUrl(Map<String, String> globalProperties) throws InputEventAdapterException {
        String deviceMgtServerUrl = globalProperties.get(DEVICE_MGT_SERVER_URL);
        if (deviceMgtServerUrl == null || deviceMgtServerUrl.isEmpty()) {
            log.error("deviceMgtServerUrl can't be empty ");
        }
        return PropertyUtils.replaceProperty(deviceMgtServerUrl);
    }

    private String getTokenEndpoint(Map<String, String> globalProperties) throws InputEventAdapterException {
        String tokenEndpoint = globalProperties.get(TOKEN_ENDPOINT_CONTEXT);
        if ( tokenEndpoint.isEmpty()) {
            log.error("tokenEndpoint can't be empty ");
        }
        return PropertyUtils.replaceProperty(tokenEndpoint);
    }

    private long getRefreshTimeOffset(Map<String, String> globalProperties) {
        long refreshTimeOffset = 100;
        try {
            refreshTimeOffset = Long.parseLong(globalProperties.get(TOKEN_REFRESH_TIME_OFFSET));
        } catch (NumberFormatException e) {
            log.error("refreshTimeOffset should be a number", e);
        }
        return refreshTimeOffset;
    }

    public static Client getSSLClient() {
        boolean isIgnoreHostnameVerification = Boolean.parseBoolean(System.getProperty("org.wso2.ignoreHostnameVerification"));
        if(isIgnoreHostnameVerification) {
            return new Client.Default(getSimpleTrustedSSLSocketFactory(), new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        }else {
            return new Client.Default(getTrustedSSLSocketFactory(), null);
        }
    }

    private static SSLSocketFactory getSimpleTrustedSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    //FIXME - I know hard-cording values is a bad practice , this code is repeating in
    // several class, so this hard-coding strings will be removed once this code block is moved into a central location
    // this should be done after the 3.1.0 release.
    private static SSLSocketFactory getTrustedSSLSocketFactory() {
        try {
            String keyStorePassword = ServerConfiguration.getInstance().getFirstProperty("Security.KeyStore.Password");
            String keyStoreLocation = ServerConfiguration.getInstance().getFirstProperty("Security.KeyStore.Location");
            String trustStorePassword = ServerConfiguration.getInstance().getFirstProperty(
                    "Security.TrustStore.Password");
            String trustStoreLocation = ServerConfiguration.getInstance().getFirstProperty(
                    "Security.TrustStore.Location");

            KeyStore keyStore = loadKeyStore(keyStoreLocation,keyStorePassword,"JKS");
            KeyStore trustStore = loadTrustStore(trustStoreLocation,trustStorePassword);
            return initSSLConnection(keyStore,keyStorePassword,trustStore);
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException
                |CertificateException | IOException | UnrecoverableKeyException e) {
            log.error("Error while creating the SSL socket factory due to "+e.getMessage(),e);
            return null;
        }
    }

    private static SSLSocketFactory initSSLConnection(KeyStore keyStore,String keyStorePassword,KeyStore trustStore) throws NoSuchAlgorithmException, UnrecoverableKeyException,
            KeyStoreException, KeyManagementException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        // Create and initialize SSLContext for HTTPS communication
        SSLContext sslContext = SSLContext.getInstance("SSLv3");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
        return  sslContext.getSocketFactory();
    }

    private static KeyStore loadKeyStore(String keyStorePath, String ksPassword, String type)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        InputStream fileInputStream = null;
        try {
            char[] keypassChar = ksPassword.toCharArray();
            KeyStore keyStore = KeyStore.getInstance(type);
            fileInputStream = new FileInputStream(keyStorePath);
            keyStore.load(fileInputStream, keypassChar);
            return keyStore;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    private static KeyStore loadTrustStore(String trustStorePath, String tsPassword)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        return loadKeyStore(trustStorePath,tsPassword,"JKS");
    }

}
