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
import com.google.api.services.androidenterprise.model.AdministratorWebToken;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpec;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecPlaySearch;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecPrivateApps;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecStoreBuilder;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecWebApps;
import com.google.api.services.androidenterprise.model.AuthenticationToken;
import com.google.api.services.androidenterprise.model.Device;
import com.google.api.services.androidenterprise.model.LocalizedText;
import com.google.api.services.androidenterprise.model.ProductsListResponse;
import com.google.api.services.androidenterprise.model.StoreCluster;
import com.google.api.services.androidenterprise.model.StoreLayout;
import com.google.api.services.androidenterprise.model.StoreLayoutClustersListResponse;
import com.google.api.services.androidenterprise.model.StoreLayoutPagesListResponse;
import com.google.api.services.androidenterprise.model.StorePage;
import com.google.api.services.androidenterprise.model.User;
import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.android.impl.EnterpriseServiceException;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseStoreCluster;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseStorePackages;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseStorePage;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseTokenUrl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GoogleAPIInvoker {

    private static final Log log = LogFactory.getLog(GoogleAPIInvoker.class);
    private String esa;

    public GoogleAPIInvoker(String esa) {
        this.esa = esa;
    }

    private GoogleAPIInvoker(){}

    public String insertUser(String enterpriseId, String username) throws EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();

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
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        try {
            AuthenticationToken tokenResponse = androidEnterprise.users()
                    .generateAuthenticationToken(enterpriseId, userId).execute();
            return tokenResponse.getToken();
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs getToken";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }
    }

    public Device installApps(String enterpriseId, String userId , Device device) throws EnterpriseServiceException{
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        try {
            Device deviceResponse = androidEnterprise.devices().update(enterpriseId,
                    userId, device.getAndroidId(), device)
                    .execute();
            return deviceResponse;
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs installApps";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }
    }

    public String getAdministratorWebToken(EnterpriseTokenUrl enterpriseTokenUrl) throws EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        AdministratorWebTokenSpec tokenSpec = new AdministratorWebTokenSpec();
        tokenSpec.setParent(enterpriseTokenUrl.getParentHost());
        tokenSpec.setPlaySearch(new AdministratorWebTokenSpecPlaySearch()
                .setApproveApps(enterpriseTokenUrl.isApproveApps())
                .setEnabled(enterpriseTokenUrl.isSearchEnabled()));
        tokenSpec.setPrivateApps(new AdministratorWebTokenSpecPrivateApps()
                .setEnabled(enterpriseTokenUrl.isPrivateAppsEnabled()));
        tokenSpec.setWebApps(new AdministratorWebTokenSpecWebApps().setEnabled(enterpriseTokenUrl.isWebAppEnabled()));
        tokenSpec.setStoreBuilder(new AdministratorWebTokenSpecStoreBuilder()
                .setEnabled(enterpriseTokenUrl.isOrganizeAppPageVisible()));
        try {
            AdministratorWebToken token = androidEnterprise.enterprises()
                    .createWebToken(enterpriseTokenUrl.getEnterpriseId(), tokenSpec).execute();
            return token.getToken();
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs installApps";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }
    }

    public ProductsListResponse listProduct(String enterpriseId, String token) throws EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        try {
            if (token == null) {
                return androidEnterprise.products().list(enterpriseId).setMaxResults(100l).setApproved(true).execute();
            } else {
                return androidEnterprise.products().list(enterpriseId).setMaxResults(100l).setToken(token)
                        .setApproved(true).execute();
            }
        } catch (IOException e) {
            String msg = "Error occurred while accessing Google APIs installApps";
            log.error(msg, e);
            throw new EnterpriseServiceException(msg, e);
        }
    }

    public String insertPage(String enterpriseId, EnterpriseStorePage storePage) throws IOException,
            EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        List<LocalizedText> names =
                ImmutableList.of(new LocalizedText().setLocale(storePage.getLocale()).setText(storePage.getPageName()));
        StorePage page = new StorePage();
        page.setName(names);
        return androidEnterprise.storelayoutpages().insert(enterpriseId, page).execute().getId();
    }

    public String updatePage(String enterpriseId, EnterpriseStorePage storePage) throws IOException,
            EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        List<LocalizedText> names =
                ImmutableList.of(new LocalizedText().setLocale(storePage.getLocale()).setText(storePage.getPageName()));
        StorePage page = new StorePage();
        page.setName(names);
        page.setLink(storePage.getLinks());
        return androidEnterprise.storelayoutpages().update(enterpriseId, storePage.getPageId(), page).execute().getId();
    }

    public void deletePage(String enterpriseId, String pageId) throws IOException,
            EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        androidEnterprise.storelayoutpages().delete(enterpriseId, pageId).execute();
    }

    public StoreLayoutPagesListResponse listPages(String enterpriseId) throws IOException,
            EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        return androidEnterprise.storelayoutpages().list(enterpriseId).execute();
    }

    public StoreLayout setStoreLayout(String enterpriseId, String homepageId)
            throws EnterpriseServiceException, IOException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        StoreLayout storeLayout = new StoreLayout();
        storeLayout.setStoreLayoutType("custom");
        storeLayout.setHomepageId(homepageId);

        return androidEnterprise
                .enterprises()
                .setStoreLayout(enterpriseId, storeLayout)
                .execute();
    }

    public String insertCluster(String enterpriseId, EnterpriseStoreCluster storeCluster) throws IOException ,
            EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        StoreCluster cluster = new StoreCluster();
        List<LocalizedText> names =
                ImmutableList.of(new LocalizedText().setLocale("en").setText(storeCluster.getName()));
        cluster.setName(names);
        List<String> productIds = new ArrayList<>();
        for (EnterpriseStorePackages packages : storeCluster.getProducts()) {
            productIds.add(packages.getPackageId());
        }
        cluster.setProductId(productIds);
        cluster.setOrderInPage(storeCluster.getOrderInPage());
        return androidEnterprise.storelayoutclusters()
                .insert(enterpriseId, storeCluster.getPageId(), cluster)
                .execute()
                .getId();
    }

    public String updateCluster(String enterpriseId, EnterpriseStoreCluster storeCluster) throws IOException ,
            EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        StoreCluster cluster = new StoreCluster();
        List<LocalizedText> names =
                ImmutableList.of(new LocalizedText().setLocale("en").setText(storeCluster.getName()));
        cluster.setName(names);
        List<String> productIds = new ArrayList<>();
        for (EnterpriseStorePackages packages : storeCluster.getProducts()) {
            productIds.add(packages.getPackageId());
        }
        cluster.setProductId(productIds);
        cluster.setOrderInPage(storeCluster.getOrderInPage());
        cluster.setId(storeCluster.getClusterId());
        return androidEnterprise.storelayoutclusters()
                .update(enterpriseId, storeCluster.getPageId(), storeCluster.getClusterId(), cluster)
                .execute()
                .getId();
    }

    public StoreLayoutClustersListResponse getClusters(String enterpriseId, String pageId)
            throws IOException, EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        return androidEnterprise.storelayoutclusters().list(enterpriseId, pageId).execute();
    }

    public void deleteCluster(String enterpriseId, String pageId, String clusterId)
            throws IOException, EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        androidEnterprise.storelayoutclusters().delete(enterpriseId, pageId, clusterId).execute();
    }

    // Update the pages to include quick links to other pages.
    public void addLinks(String enterpriseId, String pageId, List<String> links)
            throws IOException , EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();
        androidEnterprise.storelayoutpages()
                .patch(enterpriseId, pageId, new StorePage().setLink(links)).execute();
    }

    public StoreLayout getStoreLayout(String enterpriseId)
            throws IOException, EnterpriseServiceException {
        AndroidEnterprise androidEnterprise = getEnterpriseClient();

        return androidEnterprise
                .enterprises()
                .getStoreLayout(enterpriseId)
                .execute();
    }

    private AndroidEnterprise getEnterpriseClient() throws EnterpriseServiceException {

        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        InputStream inputStream = new ByteArrayInputStream(esa.getBytes(StandardCharsets.UTF_8));

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
