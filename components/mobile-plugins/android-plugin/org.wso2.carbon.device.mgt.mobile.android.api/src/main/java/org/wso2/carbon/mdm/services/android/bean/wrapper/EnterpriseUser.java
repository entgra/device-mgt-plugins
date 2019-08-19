/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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
 *
 */
package org.wso2.carbon.mdm.services.android.bean.wrapper;

public class EnterpriseUser {

    String token;
    String emmDeviceIdentifier;
    String androidPlayDeviceId;

    public String getEmmDeviceIdentifier() {
        return emmDeviceIdentifier;
    }

    public void setEmmDeviceIdentifier(String emmDeviceIdentifier) {
        this.emmDeviceIdentifier = emmDeviceIdentifier;
    }

    public String getAndroidPlayDeviceId() {
        return androidPlayDeviceId;
    }

    public void setAndroidPlayDeviceId(String androidPlayDeviceId) {
        this.androidPlayDeviceId = androidPlayDeviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
