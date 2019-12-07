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
 */

package org.wso2.carbon.mdm.services.android.util;

import com.google.api.services.androidenterprise.model.AppVersion;
import com.google.api.services.androidenterprise.model.AutoInstallConstraint;
import com.google.api.services.androidenterprise.model.AutoInstallPolicy;
import com.google.api.services.androidenterprise.model.ConfigurationVariables;
import com.google.api.services.androidenterprise.model.Device;
import com.google.api.services.androidenterprise.model.ManagedConfiguration;
import com.google.api.services.androidenterprise.model.Policy;
import com.google.api.services.androidenterprise.model.Product;
import com.google.api.services.androidenterprise.model.ProductPolicy;

import com.google.api.services.androidenterprise.model.ProductsListResponse;
import com.google.api.services.androidenterprise.model.VariableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.application.mgt.common.ApplicationArtifact;
import org.wso2.carbon.device.application.mgt.common.LifecycleChanger;
import org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException;
import org.wso2.carbon.device.application.mgt.common.response.Application;
import org.wso2.carbon.device.application.mgt.common.response.Category;
import org.wso2.carbon.device.application.mgt.common.services.ApplicationManager;
import org.wso2.carbon.device.application.mgt.common.services.SubscriptionManager;
import org.wso2.carbon.device.application.mgt.common.wrapper.ApplicationUpdateWrapper;
import org.wso2.carbon.device.application.mgt.common.wrapper.PublicAppReleaseWrapper;
import org.wso2.carbon.device.application.mgt.common.wrapper.PublicAppWrapper;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.policy.mgt.ProfileFeature;
import org.wso2.carbon.device.mgt.mobile.android.impl.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseManagedConfig;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;
import org.wso2.carbon.mdm.services.android.bean.BasicUserInfo;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseConfigs;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseApp;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.mdm.services.android.exception.NotFoundException;
import org.wso2.carbon.mdm.services.android.exception.UnexpectedServerErrorException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AndroidEnterpriseUtils {

    private static Log log = LogFactory.getLog(AndroidEnterpriseUtils.class);
    private static List<String> templates = Arrays.asList(AndroidConstants
            .USER_CLAIM_EMAIL_ADDRESS_PLACEHOLDER, AndroidConstants.USER_CLAIM_FIRST_NAME_PLACEHOLDER,
            AndroidConstants.USER_CLAIM_LAST_NAME_PLACEHOLDER);


    public static Device convertToDeviceInstance(EnterpriseInstallPolicy enterpriseInstallPolicy)
            throws EnterpriseServiceException {
        Device device = new Device();
        device.setManagementType(enterpriseInstallPolicy.getManagementType());
        device.setKind(enterpriseInstallPolicy.getKind());
        device.setAndroidId(enterpriseInstallPolicy.getAndroidId());
        Policy policy = new Policy();
        List<ProductPolicy> policyList = new ArrayList<>();

        for (EnterpriseApp app : enterpriseInstallPolicy.getApps()) {
            ProductPolicy productPolicy = new ProductPolicy();
            AutoInstallPolicy autoInstallPolicy = new AutoInstallPolicy();
            autoInstallPolicy.setAutoInstallMode(app.getAutoInstallMode());
            autoInstallPolicy.setAutoInstallPriority(app.getAutoInstallPriority());
            List<AutoInstallConstraint> autoInstallConstraintList = new ArrayList<>();
            AutoInstallConstraint autoInstallConstraint = new AutoInstallConstraint();
            autoInstallConstraint.setChargingStateConstraint(app.getChargingStateConstraint());
            autoInstallConstraint.setDeviceIdleStateConstraint(app.getDeviceIdleStateConstraint());
            autoInstallConstraint.setNetworkTypeConstraint(app.getNetworkTypeConstraint());
            autoInstallConstraintList.add(autoInstallConstraint);
            autoInstallPolicy.setAutoInstallConstraint(autoInstallConstraintList);

            productPolicy.setAutoInstallPolicy(autoInstallPolicy);
            productPolicy.setProductId(app.getProductId());

            // TODO: Cache this against package name
            AndroidEnterpriseManagedConfig configs = AndroidAPIUtils.getAndroidPluginService()
                    .getConfigByPackageName(app.getProductId().replaceFirst("app:", ""));

            if (configs != null && configs.getMcmId() != null) {
                ManagedConfiguration managedConfiguration = new ManagedConfiguration();
                ConfigurationVariables configurationVariables = new ConfigurationVariables();
                configurationVariables.setKind("androidenterprise#configurationVariables");
                configurationVariables.setMcmId(configs.getMcmId());

                List<VariableSet> variableSets = new ArrayList<>();
                BasicUserInfo userInfo = getBasicUserInfo(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
                for (String key : templates) {
                    VariableSet variableSet = new VariableSet();
                    variableSet.setKind("androidenterprise#variableSet");

                    variableSet.setPlaceholder(key);
                    String value = getPlaceholderValue(userInfo, key);
                    if (value == null) {
                        continue;
                    }
                    variableSet.setUserValue(value);
                    variableSets.add(variableSet);
                }

                if (variableSets != null && variableSets.size() > 0) {
                    configurationVariables.setVariableSet(variableSets);
                }
                managedConfiguration.setConfigurationVariables(configurationVariables);
                productPolicy.setManagedConfiguration(managedConfiguration);
            }

            policyList.add(productPolicy);
        }

        policy.setProductPolicy(policyList);
        policy.setAutoUpdatePolicy(enterpriseInstallPolicy.getAutoUpdatePolicy());
        policy.setProductAvailabilityPolicy(enterpriseInstallPolicy.getProductAvailabilityPolicy());
        device.setPolicy(policy);
        return device;
    }

    private static String getPlaceholderValue(BasicUserInfo userInfo, String key) {
        if (userInfo != null) {
            switch (key) {
                case AndroidConstants.USER_CLAIM_EMAIL_ADDRESS_PLACEHOLDER:
                    return userInfo.getEmailAddress();
                case AndroidConstants.USER_CLAIM_FIRST_NAME_PLACEHOLDER:
                    return userInfo.getFirstname();
                case AndroidConstants.USER_CLAIM_LAST_NAME_PLACEHOLDER:
                    return userInfo.getLastname();
            }
        }
        return null;
    }

    private static UserStoreManager getUserStoreManager() throws EnterpriseServiceException {
        RealmService realmService;
        UserStoreManager userStoreManager = null;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
        if (realmService == null) {
            String msg = "Realm service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        int tenantId = ctx.getTenantId();
        try {
            userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            String msg = "Could not create user store manager.";
            log.error(msg);
            throw new EnterpriseServiceException(msg, e);
        }
        return userStoreManager;
    }

    private static BasicUserInfo getBasicUserInfo(String username) throws EnterpriseServiceException {
        UserStoreManager userStoreManager = getUserStoreManager();
        try {
            if (!userStoreManager.isExistingUser(username)) {
                if (log.isDebugEnabled()) {
                    log.debug("User by username: " + username + " does not exist.");
                }
                return null;
            }
        } catch (UserStoreException e) {
            String msg = "Could not get user details of user " + username;
            log.error(msg);
            throw new EnterpriseServiceException(msg, e);
        }

        BasicUserInfo userInfo = new BasicUserInfo();
        userInfo.setUsername(username);
        try {
            userInfo.setEmailAddress(userStoreManager.getUserClaimValue(username,  AndroidConstants.USER_CLAIM_EMAIL_ADDRESS, null));
            userInfo.setFirstname(userStoreManager.getUserClaimValue(username,  AndroidConstants.USER_CLAIM_FIRST_NAME, null));
            userInfo.setFirstname(userStoreManager.getUserClaimValue(username,  AndroidConstants.USER_CLAIM_LAST_NAME, null));
        } catch (UserStoreException e) {
            String msg = "Could not get claims of user " + username;
            log.error(msg);
            throw new EnterpriseServiceException(msg, e);
        }

        return userInfo;
    }

    public static EnterpriseConfigs getEnterpriseConfigs() {
        EnterpriseConfigs enterpriseConfigs = getEnterpriseConfigsFromGoogle();
        if (enterpriseConfigs.getErrorResponse() != null) {
            if (enterpriseConfigs.getErrorResponse().getCode() == 500l) {
                throw new UnexpectedServerErrorException(enterpriseConfigs.getErrorResponse());
            } else if (enterpriseConfigs.getErrorResponse().getCode() == 500l) {
                throw new NotFoundException(enterpriseConfigs.getErrorResponse());
            }
        }
        return enterpriseConfigs;
    }

    public static EnterpriseConfigs getEnterpriseConfigsFromGoogle() {
        PlatformConfiguration configuration = null;
        EnterpriseConfigs enterpriseConfigs = new EnterpriseConfigs();
        try {
            configuration = AndroidAPIUtils.getDeviceManagementService().
                    getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        } catch (DeviceManagementException e) {
            String errorMessage = "Error while fetching tenant configurations for tenant " +
                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            log.error(errorMessage);
            // Error is being used in enterprise APIs as well as from getpending operations which should not fail as
            // these errors can cause due to issues misconfigurations in policy/esa configs which can cause to break all
            // operations, if the error is thrown to cfx error handler
            enterpriseConfigs.setErrorResponse(new ErrorResponse.ErrorResponseBuilder().setCode(500l)
                    .setMessage(errorMessage).build());
        }
        String enterpriseId = AndroidDeviceUtils.getAndroidConfig(configuration,"enterpriseId");
        String esa = AndroidDeviceUtils.getAndroidConfig(configuration,"esa");
        if (enterpriseId == null || enterpriseId.isEmpty() || esa == null || esa.isEmpty()) {
            String errorMessage = "Tenant is not configured to handle Android for work. Please contact Entgra.";
            log.warn(errorMessage);
            enterpriseConfigs.setErrorResponse(new ErrorResponse.ErrorResponseBuilder().setCode(404l)
                    .setMessage(errorMessage).build());
        }

        enterpriseConfigs.setEnterpriseId(enterpriseId);
        enterpriseConfigs.setEsa(esa);
        return enterpriseConfigs;
    }

    public static ApplicationManager getAppManagerServer() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return (ApplicationManager) ctx.getOSGiService(ApplicationManager.class, null);
    }

    public static SubscriptionManager getAppSubscriptionService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return (SubscriptionManager) ctx.getOSGiService(SubscriptionManager.class, null);
    }

    public static void persistApp(ProductsListResponse productListResponse) throws ApplicationManagementException {

        ApplicationManager applicationManager = getAppManagerServer();
        List<Category> categories = applicationManager.getRegisteredCategories();
        if (productListResponse != null && productListResponse.getProduct() != null
                && !productListResponse.getProduct().isEmpty()) {

            List<String> packageNamesOfApps = new ArrayList<>();
            for (Product product1 : productListResponse.getProduct()) {
                String s = (product1.getProductId().replaceFirst("app:", ""));
                packageNamesOfApps.add(s);
            }

            List<Application> existingApps = applicationManager.getApplications(packageNamesOfApps);
            List<Product> products = productListResponse.getProduct();

            for (Application app : existingApps){
                for (Product product : products){
                    if (product.getProductId().replaceFirst("app:", "").equals(app.getPackageName())){
                        ApplicationUpdateWrapper applicationUpdateWrapper = generatePubAppUpdateWrapper(product, categories);
                        applicationManager.updateApplication(app.getId(), applicationUpdateWrapper);

                        PublicAppReleaseWrapper publicAppReleaseWrapper = new PublicAppReleaseWrapper();
                        if (app.getSubMethod()
                                .equalsIgnoreCase(AndroidConstants.ApplicationProperties.FREE_SUB_METHOD)) {
                            publicAppReleaseWrapper.setPrice(0.0);
                        } else {
                            publicAppReleaseWrapper.setPrice(1.0);
                        }

                        publicAppReleaseWrapper.setDescription(product.getRecentChanges());
                        publicAppReleaseWrapper.setReleaseType("ga");
                        publicAppReleaseWrapper.setVersion(getAppString(product.getAppVersion()));
                        publicAppReleaseWrapper
                                .setSupportedOsVersions(String.valueOf(product.getMinAndroidSdkVersion()) + "-ALL");

                        ApplicationArtifact applicationArtifact = generateArtifacts(product);
                        applicationManager.updatePubAppRelease(app.getApplicationReleases().get(0).getUuid(),
                                publicAppReleaseWrapper, applicationArtifact);
                        products.remove(product);
                        break;
                    }
                }
            }

            for (Product product : products) {
                if (product.getAppVersion() == null) { // This is to handled removed apps from playstore
                    continue;
                }

                // Generate App wrapper
                PublicAppWrapper publicAppWrapper = generatePubAppWrapper(product, categories);
                PublicAppReleaseWrapper appReleaseWrapper = new PublicAppReleaseWrapper();

                if (publicAppWrapper.getSubMethod()
                        .equalsIgnoreCase(AndroidConstants.ApplicationProperties.FREE_SUB_METHOD)) {
                    appReleaseWrapper.setPrice(0.0);
                } else {
                    appReleaseWrapper.setPrice(1.0);
                }

                appReleaseWrapper.setDescription(product.getRecentChanges());
                appReleaseWrapper.setReleaseType("ga");
                appReleaseWrapper.setVersion(getAppString(product.getAppVersion()));
                appReleaseWrapper.setPackageName(product.getProductId().replaceFirst("app:", ""));
                appReleaseWrapper.setSupportedOsVersions(String.valueOf(product.getMinAndroidSdkVersion()) + "-ALL");

                publicAppWrapper.setPublicAppReleaseWrappers(
                        Arrays.asList(new PublicAppReleaseWrapper[] { appReleaseWrapper }));

                // Generate artifacts
                ApplicationArtifact applicationArtifact = generateArtifacts(product);

                Application application = applicationManager.createPublicApp(publicAppWrapper, applicationArtifact);
                if (application != null && (application.getApplicationReleases().get(0).getCurrentStatus() == null
                        || application.getApplicationReleases().get(0).getCurrentStatus().equals("CREATED"))) {
                    String uuid = application.getApplicationReleases().get(0).getUuid();
                    LifecycleChanger lifecycleChanger = new LifecycleChanger();
                    lifecycleChanger.setAction("IN-REVIEW");
                    applicationManager.changeLifecycleState(uuid, lifecycleChanger);
                    lifecycleChanger.setAction("APPROVED");
                    applicationManager.changeLifecycleState(uuid, lifecycleChanger);
                    lifecycleChanger.setAction("PUBLISHED");
                    applicationManager.changeLifecycleState(uuid, lifecycleChanger);
                }
            }
        }
    }

    /**
     * To generate {@link ApplicationUpdateWrapper}
     *
     * @param product {@link Product}
     * @param categories List of categories registered with app manager
     * @return {@link ApplicationUpdateWrapper}
     */
    private static ApplicationUpdateWrapper generatePubAppUpdateWrapper(Product product, List<Category> categories) {
        ApplicationUpdateWrapper applicationUpdateWrapper = new ApplicationUpdateWrapper();
        applicationUpdateWrapper.setName(product.getTitle());
        applicationUpdateWrapper.setDescription(product.getDescription());
        applicationUpdateWrapper.setCategories(
                Collections.singletonList(AndroidConstants.GOOGLE_PLAY_SYNCED_APP_CATEGORY));//Default category
        for (Category category : categories) {
            if (product.getCategory() == null) {
                List<String> pubAppCategories = new ArrayList<>();
                pubAppCategories.add(AndroidConstants.GOOGLE_PLAY_SYNCED_APP_CATEGORY);
                applicationUpdateWrapper.setCategories(pubAppCategories);
                break;
            } else if (product.getCategory().equalsIgnoreCase(category.getCategoryName())) {
                List<String> pubAppCategories = new ArrayList<>();
                pubAppCategories.add(category.getCategoryName());
                pubAppCategories.add(AndroidConstants.GOOGLE_PLAY_SYNCED_APP_CATEGORY);
                applicationUpdateWrapper.setCategories(pubAppCategories);
                break;
            }
        }
        if (product.getProductPricing().equalsIgnoreCase(AndroidConstants.ApplicationProperties.FREE_SUB_METHOD)) {
            applicationUpdateWrapper.setSubMethod(AndroidConstants.ApplicationProperties.FREE_SUB_METHOD);
        } else {
            applicationUpdateWrapper.setSubMethod(AndroidConstants.ApplicationProperties.PAID_SUB_METHOD);
        }
        // TODO: purchase an app from Playstore and see how to capture the real value for price field.
        applicationUpdateWrapper.setPaymentCurrency("$");
        return applicationUpdateWrapper;
    }

    /**
     * To generate {@link PublicAppWrapper}
     *
     * @param product {@link Product}
     * @param categories List of categories registered with app manager
     * @return {@link PublicAppWrapper}
     */
    private static PublicAppWrapper generatePubAppWrapper(Product product, List<Category> categories) {
        PublicAppWrapper publicAppWrapper = new PublicAppWrapper();
        publicAppWrapper.setName(product.getTitle());
        publicAppWrapper.setDescription(product.getDescription());
        publicAppWrapper.setCategories(
                Collections.singletonList(AndroidConstants.GOOGLE_PLAY_SYNCED_APP_CATEGORY));//Default category
        for (Category category : categories) {
            if (product.getCategory() == null) {
                List<String> pubAppCategories = new ArrayList<>();
                pubAppCategories.add(AndroidConstants.GOOGLE_PLAY_SYNCED_APP_CATEGORY);
                publicAppWrapper.setCategories(pubAppCategories);
                break;
            } else if (product.getCategory().equalsIgnoreCase(category.getCategoryName())) {
                List<String> pubAppCategories = new ArrayList<>();
                pubAppCategories.add(category.getCategoryName());
                pubAppCategories.add(AndroidConstants.GOOGLE_PLAY_SYNCED_APP_CATEGORY);
                publicAppWrapper.setCategories(pubAppCategories);
                break;
            }
        }
        if (product.getProductPricing().equalsIgnoreCase(AndroidConstants.ApplicationProperties.FREE_SUB_METHOD)) {
            publicAppWrapper.setSubMethod(AndroidConstants.ApplicationProperties.FREE_SUB_METHOD);
        } else {
            publicAppWrapper.setSubMethod(AndroidConstants.ApplicationProperties.PAID_SUB_METHOD);
        }
        // TODO: purchase an app from Playstore and see how to capture the real value for price field.
        publicAppWrapper.setPaymentCurrency("$");
        publicAppWrapper.setDeviceType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        return publicAppWrapper;
    }

    /**
     * To generate {@link ApplicationArtifact}
     *
     * @param product {@link Product}
     * @return {@link ApplicationArtifact}
     * @throws ApplicationManagementException if I/O exception occurred while generating application artifact.
     */
    private static ApplicationArtifact generateArtifacts(Product product) throws ApplicationManagementException {
        ApplicationArtifact applicationArtifact = new ApplicationArtifact();
        try {
            String iconName = product.getIconUrl().split(".com/")[1];
            applicationArtifact.setIconName(iconName);
            InputStream iconInputStream = getInputStream(iconName, product.getIconUrl());
            applicationArtifact.setIconStream(iconInputStream);
            Map<String, InputStream> screenshotMap = new HashMap<>();

            int numberOfScreenShots = 3;// This is to handle some apps in playstore without 3 screenshots.
            if (product.getScreenshotUrls() != null) {
                if (product.getScreenshotUrls().size() < 3) {
                    numberOfScreenShots = product.getScreenshotUrls().size();
                }
                for (int y = 1; y < 4; y++) {
                    int screenshotNumber = y - 1;
                    if (y > numberOfScreenShots) {
                        screenshotNumber = 0;
                    }
                    String screenshot = product.getScreenshotUrls().get(screenshotNumber);
                    String screenshotName = screenshot.split(".com/")[1];
                    InputStream screenshotInputStream = getInputStream(screenshotName, screenshot);
                    screenshotMap.put(screenshotName, screenshotInputStream);
                }
            } else { // Private apps doesn't seem to send screenshots. Handling it.
                for (int a = 0; a < 3; a++) {
                    String screenshot = product.getIconUrl();
                    String screenshotName = screenshot.split(".com/")[1];
                    InputStream screenshotInputStream = getInputStream(screenshotName, screenshot);
                    screenshotMap.put(screenshotName, screenshotInputStream);
                }
            }
            applicationArtifact.setScreenshots(screenshotMap);
            return applicationArtifact;
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while generating Application artifact";
            log.error(msg);
            throw new ApplicationManagementException(msg, e);
        }
    }
    
    private static InputStream getInputStream(String filename, String url) throws ApplicationManagementException {
        URL website;
        try {
            website = new URL(url);
        } catch (MalformedURLException e) {
            String msg = "Error occurred while converting the url " + url;
            log.error(msg);
            throw new ApplicationManagementException(msg, e);
        }
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + File.separator + filename);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }catch (IOException e) {
            String msg = "Error occurred while opening stream for url " + url;
            log.error(msg);
            throw new ApplicationManagementException(msg, e);
        } finally {
            try {
                fos.close();
                rbc.close();
            } catch (IOException e) {}
        }

        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
        InputStream targetStream;
        try {
            targetStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            String msg = "Error occurred while reading the tmp file  " + System.getProperty("java.io.tmpdir")
                    + File.separator + filename;
            log.error(msg);
            throw new ApplicationManagementException(msg, e);
        }
        file.deleteOnExit();
        return targetStream;
    }

    private static String getAppString(List<AppVersion> appVersions) {
        int highestVersionCode = 0;
        String highestVersionString = null;
        for (AppVersion appVersion : appVersions) {
            if (appVersion.getIsProduction() && appVersion.getVersionCode() > highestVersionCode) {
                highestVersionCode = appVersion.getVersionCode();
                highestVersionString = appVersion.getVersionString();
            }
        }
        return highestVersionString;
    }

    public static EnterpriseInstallPolicy getDeviceAppPolicy(String appPolicy,
                                                             ProfileFeature feature,
                                                             AndroidEnterpriseUser userDetail) {
        EnterpriseInstallPolicy enterpriseInstallPolicy = new EnterpriseInstallPolicy();
        List<EnterpriseApp> apps = new ArrayList<>();
        JsonElement appListElement;
        if (appPolicy == null) {
            appListElement = new JsonParser().parse(feature.getContent().toString()).getAsJsonObject()
                    .get(AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_CODE);
        } else {
            appListElement = new JsonParser().parse(appPolicy).getAsJsonObject()
                    .get(AndroidConstants.ApplicationInstall.ENROLLMENT_APP_INSTALL_CODE);
        }
        JsonArray appListArray = appListElement.getAsJsonArray();

        JsonObject googlePolicyPayload = appListArray.get(0).getAsJsonObject();
//                        get(AndroidConstants.ApplicationInstall.GOOGLE_POLICY_PAYLOAD).getAsString()).getAsJsonObject();
        enterpriseInstallPolicy.setAutoUpdatePolicy(googlePolicyPayload.get("autoUpdatePolicy").getAsString());
        enterpriseInstallPolicy.setProductSetBehavior(googlePolicyPayload.get("productSetBehavior").getAsString());

//        enterpriseInstallPolicy.setProductAvailabilityPolicy(googlePolicyPayload.get("productAvailabilityPolicy").getAsString());
        enterpriseInstallPolicy.setManagementType("managedProfile");
        enterpriseInstallPolicy.setKind("androidenterprise#device");
        enterpriseInstallPolicy.setAndroidId(userDetail.getAndroidPlayDeviceId());
        enterpriseInstallPolicy.setUsername(userDetail.getEmmUsername());

        for (JsonElement appElement : appListArray) {

            JsonElement policy = appElement.getAsJsonObject().
                    get(AndroidConstants.ApplicationInstall.GOOGLE_POLICY_PAYLOAD);
            if (policy != null) {
                JsonObject googlePolicyForApp = new JsonParser().parse(policy.getAsString()).getAsJsonObject();
                EnterpriseApp enterpriseApp = new EnterpriseApp();
                enterpriseApp.setProductId("app:" + googlePolicyForApp.get("packageName").getAsString());
                enterpriseApp.setAutoInstallMode(googlePolicyForApp.get("autoInstallMode").getAsString());
                enterpriseApp.setAutoInstallPriority(googlePolicyForApp.get("autoInstallPriority").getAsInt());
                enterpriseApp.setChargingStateConstraint(googlePolicyForApp.get("chargingStateConstraint").getAsString());
                enterpriseApp.setDeviceIdleStateConstraint(googlePolicyForApp.get("deviceIdleStateConstraint").getAsString());
                enterpriseApp.setNetworkTypeConstraint(googlePolicyForApp.get("networkTypeConstraint").getAsString());
                apps.add(enterpriseApp);
            }
        }
        enterpriseInstallPolicy.setApps(apps);
        return enterpriseInstallPolicy;
    }
}
