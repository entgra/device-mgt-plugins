/*
 * Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package io.entgra.device.mgt.plugins.emqx.exhook;

import io.entgra.device.mgt.core.device.mgt.core.internal.DeviceManagementDataHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.config.RealmConfigXMLProcessor;
import org.wso2.carbon.utils.CarbonUtils;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class HandlerUtil {

    private static KeyStore keyStore;
    private static KeyStore trustStore;
    private static char[] keyStorePassword;
    private static SSLContext sslContext;

    private static final String KEY_STORE_TYPE = "JKS";
    /**
     * Default truststore type of the client
     */
    private static final String TRUST_STORE_TYPE = "JKS";

    private static final String KEY_MANAGER_TYPE = "SunX509"; //Default Key Manager Type
    /**
     * Default trustmanager type of the client
     */
    private static final String TRUST_MANAGER_TYPE = "SunX509"; //Default Trust Manager Type

    private static final String SSLV3 = "SSLv3";

    private static final Log log = LogFactory.getLog(HandlerUtil.class);

    public static final String CDM_CONFIG_FILE_NAME = "cdm-config.xml";

    private static final String CDM_CONFIG_PATH = CarbonUtils.getCarbonConfigDirPath() + File.separator +
            CDM_CONFIG_FILE_NAME;

    /***
     *
     * @param httpRequest - httpMethod e.g:- HttpPost, HttpGet
     * @return response as string
     * @throws IOException IO exception returns if error occurs when executing the httpMethod
     */
    public static ProxyResponse execute(HttpRequestBase httpRequest) throws IOException {
        try (CloseableHttpClient client = getHttpClient()) {
            HttpResponse response = client.execute(httpRequest);
            ProxyResponse proxyResponse = new ProxyResponse();

            if (response == null) {
                log.error("Received null response for http request : " + httpRequest.getMethod() + " " + httpRequest
                        .getURI().toString());
                proxyResponse.setCode(HandlerConstants.INTERNAL_ERROR_CODE);
                proxyResponse.setStatus(ProxyResponse.Status.ERROR);
                proxyResponse.setExecutorResponse(HandlerConstants.EXECUTOR_EXCEPTION_PREFIX + getStatusKey(
                        HandlerConstants.INTERNAL_ERROR_CODE));
                return proxyResponse;
            } else {
                int statusCode = response.getStatusLine().getStatusCode();
                String jsonString = getResponseString(response);
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                    proxyResponse.setCode(statusCode);
                    proxyResponse.setData(jsonString);
                    proxyResponse.setStatus(ProxyResponse.Status.SUCCESS);
                    proxyResponse.setExecutorResponse("SUCCESS");
                    proxyResponse.setHeaders(response.getAllHeaders());
                    return proxyResponse;
                } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    if (isTokenExpired(jsonString)) {
                        proxyResponse.setCode(statusCode);
                        proxyResponse.setStatus(ProxyResponse.Status.ERROR);
                        proxyResponse.setExecutorResponse(HandlerConstants.TOKEN_IS_EXPIRED);
                    } else {
                        log.error(
                                "Received " + statusCode + " response for http request : " + httpRequest.getMethod()
                                        + " " + httpRequest.getURI().toString() + ". Error message: " + jsonString);
                        proxyResponse.setCode(statusCode);
                        proxyResponse.setData(jsonString);
                        proxyResponse.setStatus(ProxyResponse.Status.ERROR);
                        proxyResponse.setExecutorResponse(
                                HandlerConstants.EXECUTOR_EXCEPTION_PREFIX + getStatusKey(statusCode));
                    }
                    return proxyResponse;
                }
                log.error("Received " + statusCode +
                        " response for http request : " + httpRequest.getMethod() + " " + httpRequest.getURI()
                        .toString() + ". Error message: " + jsonString);
                proxyResponse.setCode(statusCode);
                proxyResponse.setData(jsonString);
                proxyResponse.setStatus(ProxyResponse.Status.ERROR);
                proxyResponse
                        .setExecutorResponse(HandlerConstants.EXECUTOR_EXCEPTION_PREFIX + getStatusKey(statusCode));
                return proxyResponse;
            }
        }
    }

    public static boolean isTokenExpired(String jsonBody) {
        return jsonBody.contains("Access token expired") || jsonBody
                .contains("Invalid input. Access token validation failed");
    }

    /***
     *
     * @param statusCode Provide status code, e.g:- 400, 401, 500 etc
     * @return relative status code key for given status code.
     */
    public static String getStatusKey(int statusCode) {
        String statusCodeKey;

        switch (statusCode) {
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                statusCodeKey = "internalServerError";
                break;
            case HttpStatus.SC_BAD_REQUEST:
                statusCodeKey = "badRequest";
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                statusCodeKey = "unauthorized";
                break;
            case HttpStatus.SC_FORBIDDEN:
                statusCodeKey = "forbidden";
                break;
            case HttpStatus.SC_NOT_FOUND:
                statusCodeKey = "notFound";
                break;
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                statusCodeKey = "methodNotAllowed";
                break;
            case HttpStatus.SC_NOT_ACCEPTABLE:
                statusCodeKey = "notAcceptable";
                break;
            case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE:
                statusCodeKey = "unsupportedMediaType";
                break;
            default:
                statusCodeKey = "defaultPage";
                break;
        }
        return statusCodeKey;
    }

    /**
     * Retrieve Http client based on hostname verification.
     *
     * @return {@link CloseableHttpClient} http client
     */
    public static CloseableHttpClient getHttpClient() {

        boolean isIgnoreHostnameVerification = Boolean.parseBoolean(System.
                getProperty("org.wso2.ignoreHostnameVerification"));
        if (isIgnoreHostnameVerification) {
            return HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        } else {
            return HttpClients.createDefault();
        }

//        String keyStorePassword = "wso2carbon";
//        String trustStorePassword = "wso2carbon";
//        String keyStoreLocation = "/home/amalka/entgra/source/product-switchgear/switchgear-plugin/integration-test/distro-prep/target/entgra-iot-pro-switchgear-repacked-1.0.0-SNAPSHOT/repository/resources/security/wso2carbon.jks";
//        String trustStoreLocation = "/home/amalka/entgra/source/product-switchgear/switchgear-plugin/integration-test/distro-prep/target/entgra-iot-pro-switchgear-repacked-1.0.0-SNAPSHOT/repository/resources/security/client-truststore.jks";
//
//        //Call to load the keystore.
//        try {
//            loadKeyStore(keyStoreLocation, keyStorePassword);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (CertificateException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        } catch (KeyStoreException e) {
//            throw new RuntimeException(e);
//        }
//        //Call to load the TrustStore.
//        try {
//            loadTrustStore(trustStoreLocation, trustStorePassword);
//        } catch (KeyStoreException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (CertificateException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        //Create the SSL context with the loaded TrustStore/keystore.
//        try {
//            initSSLConnection();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        } catch (UnrecoverableKeyException e) {
//            throw new RuntimeException(e);
//        } catch (KeyStoreException e) {
//            throw new RuntimeException(e);
//        } catch (KeyManagementException e) {
//            throw new RuntimeException(e);
//        }
//
//        return HttpClients.createDefault();

    }

    private static void loadKeyStore(String keyStorePath, String ksPassword)
            throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        InputStream fis = null;
        try {
            keyStorePassword = ksPassword.toCharArray();
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            fis = new FileInputStream(keyStorePath);
            keyStore.load(fis, keyStorePassword);

        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static void loadTrustStore(String trustStorePath, String tsPassword)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        InputStream fis = null;
        try {
            trustStore = KeyStore.getInstance(TRUST_STORE_TYPE);
            fis = new FileInputStream(trustStorePath);
            trustStore.load(fis, tsPassword.toCharArray());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static void initSSLConnection() throws NoSuchAlgorithmException, UnrecoverableKeyException,
            KeyStoreException, KeyManagementException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KEY_MANAGER_TYPE);
        keyManagerFactory.init(keyStore, keyStorePassword);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TRUST_MANAGER_TYPE);
        trustManagerFactory.init(trustStore);

        // Create and initialize SSLContext for HTTPS communication
        sslContext = SSLContext.getInstance(SSLV3);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
    }

    public static String getResponseString(HttpResponse response) throws IOException {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }
            return responseBuilder.toString();
        }
    }

    public static KeyManagerConfigurations extractKeyManagerConfig() throws JAXBException,
            ParserConfigurationException, IOException, SAXException {
        File xmlFile = new File(CDM_CONFIG_PATH);
        // Step 1: Parse the whole XML document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlFile);

        // Step 2: Find the <KeyManagerConfiguration> node
        Node keyManagerNode = doc.getElementsByTagName("KeyManagerConfiguration").item(0);

        if (keyManagerNode == null) {
            throw new RuntimeException("KeyManagerConfiguration element not found in XML");
        }

        // Step 3: Unmarshall only that node into KeyManagerConfigurations class
        JAXBContext context = JAXBContext.newInstance(KeyManagerConfigurations.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Important: Wrap node into a JAXBElement because we are unmarshalling part of a document
        JAXBElement<KeyManagerConfigurations> je = unmarshaller.unmarshal(keyManagerNode, KeyManagerConfigurations.class);

        // Step 4: Return the Java object
        return je.getValue();
    }

    public static String replaceSystemProperty(String text) {

        int indexOfStartingChars = -1;
        int indexOfClosingBrace;

        // The following condition deals with properties.
        // Properties are specified as ${system.property},
        // and are assumed to be System properties
        while (indexOfStartingChars < text.indexOf("${")
                && (indexOfStartingChars = text.indexOf("${")) != -1
                && (indexOfClosingBrace = text.indexOf('}')) != -1) { // Is a
            // property
            // used?
            String sysProp = text.substring(indexOfStartingChars + 2,
                    indexOfClosingBrace);
            String propValue = System.getProperty(sysProp);

            if (propValue == null) {
                if ("carbon.context".equals(sysProp)) {
                    propValue = DeviceManagementDataHolder.getInstance().getConfigurationContextService()
                            .getServerConfigContext().getContextRoot();
                } else if ("admin.username".equals(sysProp) || "admin.password".equals(sysProp)) {
                    try {
                        RealmConfiguration realmConfig =
                                new RealmConfigXMLProcessor().buildRealmConfigurationFromFile();
                        if ("admin.username".equals(sysProp)) {
                            propValue = realmConfig.getAdminUserName();
                        } else {
                            propValue = realmConfig.getAdminPassword();
                        }
                    } catch (UserStoreException e) {
                        // Can't throw an exception because the server is
                        // starting and can't be halted.
                        log.error("Unable to build the Realm Configuration", e);
                        return null;
                    }
                }
            }
            //Derive original text value with resolved system property value
            if (propValue != null) {
                text = text.substring(0, indexOfStartingChars) + propValue
                        + text.substring(indexOfClosingBrace + 1);
            }
            if ("carbon.home".equals(sysProp) && propValue != null
                    && ".".equals(propValue)) {
                text = new File(".").getAbsolutePath() + File.separator + text;
            }
        }
        return text;
    }

}
