package org.wso2.carbon.mdm.services.android.bean;

public class ApiRegistrationProfile {

    private String applicationName;
    private String tags[];
    private boolean isAllowedToAllDomains;
    private boolean isMappingAnExistingOAuthApp;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public boolean isAllowedToAllDomains() {
        return isAllowedToAllDomains;
    }

    public void setAllowedToAllDomains(boolean allowedToAllDomains) {
        isAllowedToAllDomains = allowedToAllDomains;
    }

    public boolean isMappingAnExistingOAuthApp() {
        return isMappingAnExistingOAuthApp;
    }

    public void setMappingAnExistingOAuthApp(boolean mappingAnExistingOAuthApp) {
        isMappingAnExistingOAuthApp = mappingAnExistingOAuthApp;
    }
}
