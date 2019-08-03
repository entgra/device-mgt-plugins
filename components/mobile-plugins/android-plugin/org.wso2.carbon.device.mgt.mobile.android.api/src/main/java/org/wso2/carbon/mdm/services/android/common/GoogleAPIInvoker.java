/*
 *   Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.carbon.mdm.services.android.common;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidenterprise.AndroidEnterprise;
import com.google.api.services.androidenterprise.AndroidEnterpriseScopes;
import com.google.api.services.androidenterprise.model.AuthenticationToken;
import com.google.api.services.androidenterprise.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.android.impl.EnterpriseServiceException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GoogleAPIInvoker {

    private static final Log log = LogFactory.getLog(GoogleAPIInvoker.class);

    public String insertUser(String enterpriseId, String username) throws EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = createAndroidEnterprise();

        User user = new User();
        user.setAccountIdentifier(username);
        user.setDisplayName(username);
        user.setAccountType("userAccount");
        user.setManagementType("emmManaged");
        try {
            User addedUser = androidEnterprise.users().insert(enterpriseId, user)
                    .execute();
            return addedUser.getId();
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }
    }

    public String getToken(String enterpriseId, String userId) throws EnterpriseServiceException{
        AndroidEnterprise androidEnterprise = createAndroidEnterprise();
        try {
            AuthenticationToken tokenResponse = androidEnterprise.users()
                    .generateAuthenticationToken(enterpriseId, userId).execute();
            return tokenResponse.getToken();
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }
    }

    private AndroidEnterprise createAndroidEnterprise() throws EnterpriseServiceException {


        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        String x = "";
        InputStream inputStream = new ByteArrayInputStream(x.getBytes(StandardCharsets.UTF_8));

        final Credential credential;
        try {
            credential = GoogleCredential.fromStream(inputStream, httpTransport, jsonFactory)
                    .createScoped(AndroidEnterpriseScopes.all());
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }

        HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                credential.initialize(request);
            }
        };


        return new AndroidEnterprise.Builder(httpTransport, jsonFactory, httpRequestInitializer)
                .build();
    }
}
