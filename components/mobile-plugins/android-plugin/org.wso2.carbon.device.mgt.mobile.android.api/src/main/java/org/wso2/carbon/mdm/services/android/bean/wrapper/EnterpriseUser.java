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
