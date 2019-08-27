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
import com.google.api.services.androidenterprise.model.Device;
import com.google.api.services.androidenterprise.model.Policy;
import com.google.api.services.androidenterprise.model.Product;
import com.google.api.services.androidenterprise.model.ProductPolicy;

import com.google.api.services.androidenterprise.model.ProductsListResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.application.mgt.common.ApplicationArtifact;
import org.wso2.carbon.device.application.mgt.common.LifecycleChanger;
import org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException;
import org.wso2.carbon.device.application.mgt.common.response.Application;
import org.wso2.carbon.device.application.mgt.common.response.Category;
import org.wso2.carbon.device.application.mgt.common.services.ApplicationManager;
import org.wso2.carbon.device.application.mgt.common.wrapper.PublicAppReleaseWrapper;
import org.wso2.carbon.device.application.mgt.common.wrapper.PublicAppWrapper;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseConfigs;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseApp;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.mdm.services.android.exception.NotFoundException;
import org.wso2.carbon.mdm.services.android.exception.UnexpectedServerErrorException;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidEnterpriseUtils {

    private static Log log = LogFactory.getLog(AndroidEnterpriseUtils.class);

    public static Device convertToDeviceInstance(EnterpriseInstallPolicy enterpriseInstallPolicy) {
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
            policyList.add(productPolicy);
        }

        policy.setProductPolicy(policyList);
        policy.setAutoUpdatePolicy(enterpriseInstallPolicy.getAutoUpdatePolicy());
        policy.setProductAvailabilityPolicy(enterpriseInstallPolicy.getProductAvailabilityPolicy());
        device.setPolicy(policy);
        return device;
    }

    public static EnterpriseConfigs getEnterpriseConfigs() {
        PlatformConfiguration configuration;
        try {
            configuration = AndroidAPIUtils.getDeviceManagementService().
                    getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        } catch (DeviceManagementException e) {
            String errorMessage = "Error while fetching tenant configurations for tenant " +
                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            log.error(errorMessage);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(errorMessage).build());

        }
        String enterpriseId = AndroidDeviceUtils.getAndroidConfig(configuration,"enterpriseId");
        String esa = AndroidDeviceUtils.getAndroidConfig(configuration,"esa");
        if (enterpriseId == null || enterpriseId.isEmpty() || esa == null || esa.isEmpty()) {
            String errorMessage = "Tenant is not configured to handle Android for work. Please contact Entgra.";
            log.error(errorMessage);
            throw new NotFoundException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage(errorMessage).build());
        }
        EnterpriseConfigs enterpriseConfigs = new EnterpriseConfigs();
        enterpriseConfigs.setEnterpriseId(enterpriseId);
        enterpriseConfigs.setEsa(esa);
        return enterpriseConfigs;
    }

    public static ApplicationManager getAppManagerServer() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return (ApplicationManager) ctx.getOSGiService(ApplicationManager.class, null);
    }

    public static void persistApp(ProductsListResponse productListResponse) throws ApplicationManagementException {

        ApplicationManager applicationManager = getAppManagerServer();
        List<Category> categories = applicationManager.getRegisteredCategories();
        if (productListResponse != null && productListResponse.getProduct() != null
                && productListResponse.getProduct().size() >0) {

            for (Product product : productListResponse.getProduct()) {

                // Generate App wrapper
                PublicAppWrapper publicAppWrapper = new PublicAppWrapper();
                PublicAppReleaseWrapper appReleaseWrapper = new PublicAppReleaseWrapper();
                publicAppWrapper.setName(product.getTitle());
                publicAppWrapper.setDescription(product.getDescription());
                publicAppWrapper.setCategories(Arrays.asList(new String[]{"GooglePlaySyncedApp"}));//Default category
                for (Category category :categories) {
                    if(product.getCategory().equalsIgnoreCase(category.getCategoryName())) {
                        publicAppWrapper.setCategories(Arrays.asList(new String[]{category.getCategoryName()}));
                        break;
                    }
                }
                if (product.getProductPricing().equalsIgnoreCase("free")) {
                    publicAppWrapper.setSubMethod("FREE");
                } else {
                    publicAppWrapper.setSubMethod("PAID");
                }
                // TODO: purchase an app from Playstore and see how to capture the real value for price field.
                publicAppWrapper.setPaymentCurrency("$");
                appReleaseWrapper.setPrice(1.0);

                publicAppWrapper.setDeviceType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
                appReleaseWrapper.setDescription(product.getRecentChanges());
                appReleaseWrapper.setReleaseType("ga");
                appReleaseWrapper.setVersion(getAppString(product.getAppVersion()));
                appReleaseWrapper.setPackageName(product.getProductId().replaceFirst("app:", ""));
                appReleaseWrapper.setSupportedOsVersions(String.valueOf(product.getMinAndroidSdkVersion())+ "-ALL");

                publicAppWrapper.setPublicAppReleaseWrappers(Arrays.asList(new PublicAppReleaseWrapper[]{appReleaseWrapper}));

                // Generate artifacts
                ApplicationArtifact applicationArtifact = new ApplicationArtifact();

                String iconName = product.getIconUrl().split(".com/")[1];
                applicationArtifact.setIconName(iconName);


                    InputStream iconInputStream = getInputStream(iconName, product.getIconUrl());
                    applicationArtifact.setIconStream(iconInputStream);
                    Map<String, InputStream> screenshotMap = new HashMap<>();

                    for (int x = 0; x < 3; x++) {
                        String screenshot = product.getScreenshotUrls().get(x);
                        String screenshotName = screenshot.split(".com/")[1];
                        InputStream screenshotInputStream = getInputStream(screenshotName, screenshot);
                        screenshotMap.put(screenshotName, screenshotInputStream);
                    }
                    applicationArtifact.setScreenshots(screenshotMap);


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
}
