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

package org.wso2.carbon.device.mgt.mobile.android.impl.dto;

public class AndroidEnterpriseUser {
    String emmUsername;
    int tenantId;
    String enterpriseId;//from configs
    String googleUserId;// generated internally
    String androidPlayDeviceId;  //sent by device
    String emmDeviceId; //set internally
    String lastUpdatedTime; //set internally

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getEmmUsername() {
        return emmUsername;
    }

    public void setEmmUsername(String emmUsername) {
        this.emmUsername = emmUsername;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getGoogleUserId() {
        return googleUserId;
    }

    public void setGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    public String getAndroidPlayDeviceId() {
        return androidPlayDeviceId;
    }

    public void setAndroidPlayDeviceId(String androidPlayDeviceId) {
        this.androidPlayDeviceId = androidPlayDeviceId;
    }

    public String getEmmDeviceId() {
        return emmDeviceId;
    }

    public void setEmmDeviceId(String emmDeviceId) {
        this.emmDeviceId = emmDeviceId;
    }
}
