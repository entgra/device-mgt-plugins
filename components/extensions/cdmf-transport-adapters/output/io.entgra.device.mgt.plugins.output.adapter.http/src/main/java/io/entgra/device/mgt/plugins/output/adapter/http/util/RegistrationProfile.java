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
package io.entgra.device.mgt.plugins.output.adapter.http.util;

/**
 * This class represents the data that are required to register
 * the oauth application.
 */
public class RegistrationProfile {

    private static final String TAG = RegistrationProfile.class.getSimpleName();
    private String callbackUrl;
    private String clientName;
    private String tokenScope;
    private String owner;
    private String grantType;
    private String applicationType;
    private boolean isSaasApp;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callBackUrl) {
        this.callbackUrl = callBackUrl;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTokenScope() {
        return tokenScope;
    }

    public void setTokenScope(String tokenScope) {
        this.tokenScope = tokenScope;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public boolean isSaasApp() {
        return isSaasApp;
    }

    public void setIsSaasApp(boolean isSaasApp) {
        this.isSaasApp = isSaasApp;
    }

    public String toJSON() {
        String jsonString =
                "{\"callbackUrl\": \"" + callbackUrl + "\",\"clientName\": \"" + clientName + "\", \"tokenScope\": " +
                "\"" + tokenScope + "\", \"owner\": \"" + owner + "\"," + "\"grantType\": \"" + grantType +
                "\", \"saasApp\" : " + isSaasApp + " }\n";
        return jsonString;
    }
}