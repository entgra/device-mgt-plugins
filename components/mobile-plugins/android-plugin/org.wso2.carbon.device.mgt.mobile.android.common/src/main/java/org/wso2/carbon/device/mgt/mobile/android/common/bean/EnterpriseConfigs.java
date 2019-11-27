package org.wso2.carbon.device.mgt.mobile.android.common.bean;

public class EnterpriseConfigs {

    String enterpriseId;
    String esa;
    ErrorResponse errorResponse;

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEsa() {
        return esa;
    }

    public void setEsa(String esa) {
        this.esa = esa;
    }
}
