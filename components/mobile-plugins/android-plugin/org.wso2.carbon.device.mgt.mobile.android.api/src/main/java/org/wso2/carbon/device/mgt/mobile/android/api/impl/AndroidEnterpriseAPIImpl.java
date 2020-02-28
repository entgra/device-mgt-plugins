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
package org.wso2.carbon.device.mgt.mobile.android.api.impl;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.androidenterprise.model.ProductsListResponse;
import com.google.api.services.androidenterprise.model.StoreCluster;
import com.google.api.services.androidenterprise.model.StoreLayout;
import com.google.api.services.androidenterprise.model.StoreLayoutClustersListResponse;
import com.google.api.services.androidenterprise.model.StoreLayoutPagesListResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.application.mgt.common.dto.ApplicationPolicyDTO;
import org.wso2.carbon.device.application.mgt.common.dto.ApplicationReleaseDTO;
import org.wso2.carbon.device.application.mgt.common.exception.ApplicationManagementException;
import org.wso2.carbon.device.application.mgt.common.services.ApplicationManager;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.policy.mgt.ProfileFeature;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.mobile.android.api.AndroidEnterpriseAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseConfigs;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStoreCluster;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStorePackages;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStorePage;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseStorePageLinks;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseTokenUrl;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.GoogleAppSyncResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseApp;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.TokenWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseManagedConfig;
import org.wso2.carbon.device.mgt.mobile.android.common.dto.AndroidEnterpriseUser;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.NotFoundException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.mobile.android.api.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.api.util.AndroidDeviceUtils;
import org.wso2.carbon.device.mgt.mobile.android.api.util.AndroidEnterpriseUtils;
import org.wso2.carbon.device.mgt.mobile.android.api.invoker.GoogleAPIInvoker;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/enterprise")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AndroidEnterpriseAPIImpl implements AndroidEnterpriseAPI {
    private static final Log log = LogFactory.getLog(AndroidEnterpriseAPIImpl.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/user")
    public Response addUser(EnterpriseUser enterpriseUser) {
        if (enterpriseUser == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        } else if (enterpriseUser.getEmmDeviceIdentifier() == null || enterpriseUser.getEmmDeviceIdentifier().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("EMM ID is incorrect").build())
                    .build();
        } else if (enterpriseUser.getAndroidPlayDeviceId() == null || enterpriseUser.getAndroidPlayDeviceId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Google Play ID is incorrect").build())
                    .build();
        }

        String token;
        try {
            token = insertUser(enterpriseUser);
            if (token == null) {
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching token").build())
                        .build();
            }

        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving user").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while adding user";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
        return Response.status(Response.Status.OK).entity(token).build();
    }

    public String insertUser(EnterpriseUser enterpriseUser)
            throws EnterpriseServiceException, AndroidDeviceMgtPluginException {
        EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
        String token;
        boolean deviceIdExist = false;

        String googleUserId;
        List<AndroidEnterpriseUser> androidEnterpriseUsers = AndroidAPIUtils.getAndroidPluginService()
                .getEnterpriseUser(CarbonContext.getThreadLocalCarbonContext().getUsername());
        GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
        if (androidEnterpriseUsers != null && !androidEnterpriseUsers.isEmpty()) {
            googleUserId = androidEnterpriseUsers.get(0).getGoogleUserId();
            // If this device is also present, only need to provide a token for this request.
            for (AndroidEnterpriseUser enterprise : androidEnterpriseUsers) {
                if (enterprise.getEmmDeviceId() != null
                        && enterprise.getEmmDeviceId().equals(enterpriseUser.getAndroidPlayDeviceId())) {
                    deviceIdExist = true;
                }
            }
        } else {
            googleUserId = googleAPIInvoker.insertUser(enterpriseConfigs.getEnterpriseId(), CarbonContext
                    .getThreadLocalCarbonContext()
                    .getUsername());
        }
        // Fetching an auth token from Google EMM API
        token = googleAPIInvoker.getToken(enterpriseConfigs.getEnterpriseId(), googleUserId);

        if (!deviceIdExist) {
            AndroidEnterpriseUser androidEnterpriseUser = new AndroidEnterpriseUser();
            androidEnterpriseUser.setEmmUsername(CarbonContext.getThreadLocalCarbonContext().getUsername());
            androidEnterpriseUser.setTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantId());
            androidEnterpriseUser.setAndroidPlayDeviceId(enterpriseUser.getAndroidPlayDeviceId());
            androidEnterpriseUser.setEnterpriseId(enterpriseConfigs.getEnterpriseId());
            androidEnterpriseUser.setEmmDeviceId(enterpriseUser.getEmmDeviceIdentifier());
            androidEnterpriseUser.setGoogleUserId(googleUserId);

            AndroidAPIUtils.getAndroidPluginService().addEnterpriseUser(androidEnterpriseUser);
        }

        return token;

    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/available-app")
    public Response updateUser(EnterpriseInstallPolicy device) {

        boolean sentToDevice = false;

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            List<AndroidEnterpriseUser> enterpriseUserInstances = AndroidAPIUtils.getAndroidPluginService()
                    .getEnterpriseUser(device.getUsername());
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
            for (AndroidEnterpriseUser userDetail : enterpriseUserInstances) {
                if (userDetail.getEnterpriseId() != null && !userDetail.getEnterpriseId().isEmpty() && userDetail
                        .getEmmUsername() != null && userDetail.getEmmUsername().equals(device.getUsername())
                        && device.getAndroidId().equals(userDetail.getAndroidPlayDeviceId())) {
                    googleAPIInvoker.updateAppsForUser(enterpriseConfigs.getEnterpriseId(), userDetail.getGoogleUserId(),
                            AndroidEnterpriseUtils.convertToDeviceInstance(device));
                    sentToDevice = true;
                }
            }

        } catch (EnterpriseServiceException e) {
            String errorMessage = "App install failed. No user found for name " + device.getUsername();
            log.error(errorMessage);
            return Response.status(Response.Status.NOT_FOUND).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_NOT_FOUND)
                            .setMessage(errorMessage).build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while updating user";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
        if (sentToDevice) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Could not install on the device of user "
                            + device.getUsername()).build()).build();
        }
    }

    @Override
    @GET
    @Path("/store-url")
    public Response getStoreUrl(@QueryParam("approveApps") boolean approveApps,
                                @QueryParam("searchEnabled") boolean searchEnabled,
                                @QueryParam("isPrivateAppsEnabled") boolean isPrivateAppsEnabled,
                                @QueryParam("isWebAppEnabled") boolean isWebAppEnabled,
                                @QueryParam("isOrganizeAppPageVisible") boolean isOrganizeAppPageVisible,
                                @QueryParam("isManagedConfigEnabled") boolean isManagedConfigEnabled,
                                @QueryParam("host") String host) {
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();

            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
            EnterpriseTokenUrl enterpriseTokenUrl = new EnterpriseTokenUrl();
            if (enterpriseConfigs == null || enterpriseConfigs.getEnterpriseId() == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage("Not configured for AFW").build()).build();
            }
            enterpriseTokenUrl.setEnterpriseId(enterpriseConfigs.getEnterpriseId());
            enterpriseTokenUrl.setApproveApps(approveApps);
            enterpriseTokenUrl.setSearchEnabled(searchEnabled);
            enterpriseTokenUrl.setPrivateAppsEnabled(isPrivateAppsEnabled);
            enterpriseTokenUrl.setWebAppEnabled(isWebAppEnabled);
            enterpriseTokenUrl.setOrganizeAppPageVisible(isOrganizeAppPageVisible);
            enterpriseTokenUrl.setParentHost(host);
            enterpriseTokenUrl.setManagedConfigEnabled(isManagedConfigEnabled);

            String token = googleAPIInvoker.getAdministratorWebToken(enterpriseTokenUrl);
            TokenWrapper tokenWrapper = new TokenWrapper();
            tokenWrapper.setToken(token);
            return Response.status(Response.Status.OK).entity(tokenWrapper).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when calling get web token").build())
                    .build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while getting store url";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @Override
    @GET
    @Path("/products/sync")
    public Response syncApps() {
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            ProductsListResponse productsListResponse = googleAPIInvoker
                    .listProduct(enterpriseConfigs.getEnterpriseId(), null);
            AndroidEnterpriseUtils.persistApp(productsListResponse);

            int total = productsListResponse.getProduct().size()
                    + recursiveSync(googleAPIInvoker, enterpriseConfigs.getEnterpriseId(), productsListResponse);
            GoogleAppSyncResponse appSyncResponse = new GoogleAppSyncResponse();
            appSyncResponse.setTotalApps(total);
            return Response.status(Response.Status.OK).entity(appSyncResponse).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when calling sync").build())
                    .build();
        } catch (ApplicationManagementException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when persisting app").build())
                    .build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while syncing apps";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    private int recursiveSync(GoogleAPIInvoker googleAPIInvoker, String enterpriseId, ProductsListResponse
            productsListResponse) throws EnterpriseServiceException, ApplicationManagementException {
        // Are there more pages
        if (productsListResponse == null || productsListResponse.getTokenPagination() == null
                || productsListResponse.getTokenPagination().getNextPageToken() == null) {
            return 0;
        }

        // Get next page
        ProductsListResponse productsListResponseNext = googleAPIInvoker.listProduct(enterpriseId,
                productsListResponse.getTokenPagination().getNextPageToken());
        AndroidEnterpriseUtils.persistApp(productsListResponseNext);
        if (productsListResponseNext != null && productsListResponseNext.getTokenPagination() != null &&
                productsListResponseNext.getTokenPagination().getNextPageToken() != null) {
            return recursiveSync(googleAPIInvoker, enterpriseId, productsListResponseNext)
                    + productsListResponseNext.getProduct().size();
        } else {
            return productsListResponseNext.getProduct().size();
        }
    }

    @POST
    @Path("/store-layout/page")
    @Override
    public Response addPage(EnterpriseStorePage page) {
        if (page == null || page.getPageName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        }
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            String id = googleAPIInvoker.insertPage(enterpriseConfigs.getEnterpriseId(), page);
            page.setPageId(id);
            return Response.status(Response.Status.OK).entity(page).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when inserting page "
                            + page.getPageName()).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when inserting page "
                            + page.getPageName() + " , due to an error with ESA").build() ).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while adding page";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @PUT
    @Path("/store-layout/page")
    @Override
    public Response updatePage(EnterpriseStorePage page) {
        if (page == null || page.getPageName() == null || page.getPageId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        }

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            String id = googleAPIInvoker.updatePage(enterpriseConfigs.getEnterpriseId(), page);
            page.setPageId(id);
            return Response.status(Response.Status.OK).entity(page).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating page "
                            + page.getPageName()).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating page "
                            + page.getPageName()  + " , due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while updating page";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @DELETE
    @Path("/store-layout/page/{id}")
    @Consumes(MediaType.WILDCARD)
    @Override
    public Response deletePage(@PathParam("id") String id) {
        if (id == null || id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Page Id cannot be empty").build())
                    .build();
        }

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            googleAPIInvoker.deletePage(enterpriseConfigs.getEnterpriseId(), id);
            return Response.status(Response.Status.OK).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when deleting page "
                            + id).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating page "
                            + id  + " , Due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while deleting page";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @GET
    @Path("/store-layout/page")
    @Override
    public Response getPages() {
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            StoreLayoutPagesListResponse pages = googleAPIInvoker.listPages(enterpriseConfigs.getEnterpriseId());
            return Response.status(Response.Status.OK).entity(pages).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching all pages").build())
                    .build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching page "
                            + " , Due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while getting pages";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }


    @PUT
    @Path("/store-layout/home-page/{id}")
    @Override
    public Response setHome(@PathParam("id") String id) {
        if (id == null || id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Id cannot be empty").build())
                    .build();
        }
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            StoreLayout layout = googleAPIInvoker.setStoreLayout(enterpriseConfigs.getEnterpriseId(), id);
            return Response.status(Response.Status.OK).entity(layout).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when inserting home page "
                            + id).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when inserting home page "
                            + id + " , due to an error with ESA").build() ).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while setting home";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @GET
    @Path("/store-layout/home-page")
    @Override
    public Response getHome() {
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            StoreLayout layout = googleAPIInvoker.getStoreLayout(enterpriseConfigs.getEnterpriseId());
            return Response.status(Response.Status.OK).entity(layout).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching home page").build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching home page.").build() )
                    .build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while getting home";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @POST
    @Path("/store-layout/cluster")
    @Override
    public Response addCluster(EnterpriseStoreCluster storeCluster) {
        if (storeCluster == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        } else if (storeCluster.getName() == null || storeCluster.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Cluster name cannot be empty").build()).build();
        } else if (storeCluster.getProducts() == null || storeCluster.getProducts().size() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Products cannot be empty").build()).build();
        } else if (storeCluster.getOrderInPage() == null || storeCluster.getOrderInPage().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Cluster order cannot be empty").build()).build();
        }

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            String id = googleAPIInvoker.insertCluster(enterpriseConfigs.getEnterpriseId(), storeCluster);
            storeCluster.setClusterId(id);
            return Response.status(Response.Status.OK).entity(storeCluster).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating cluster "
                            + storeCluster.getName()).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating cluster "
                            + storeCluster.getName()  + " , due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while adding cluster";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @PUT
    @Path("/store-layout/cluster")
    @Override
    public Response updatePage(EnterpriseStoreCluster storeCluster) {
        if (storeCluster == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        } else if (storeCluster.getName() == null || storeCluster.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Cluster name cannot be empty").build()).build();
        } else if (storeCluster.getProducts() == null || storeCluster.getProducts().size() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Products cannot be empty").build()).build();
        } else if (storeCluster.getOrderInPage() == null || storeCluster.getOrderInPage().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Cluster order cannot be empty").build()).build();
        }

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            String id = googleAPIInvoker.updateCluster(enterpriseConfigs.getEnterpriseId(), storeCluster);
            storeCluster.setClusterId(id);
            return Response.status(Response.Status.OK).entity(storeCluster).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating cluster "
                            + storeCluster.getName()).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when updating cluster "
                            + storeCluster.getName() + " , due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while updating cluster";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @DELETE
    @Path("/store-layout/cluster/{clusterId}/page/{pageId}")
    @Consumes(MediaType.WILDCARD)
    @Override
    public Response deleteCluster( @PathParam("clusterId") String clusterId,  @PathParam("pageId")  String pageId) {
        if (clusterId == null || clusterId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Cluster id cannot be empty").build()).build();
        } else if (pageId == null || pageId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Page id cannot be empty").build()).build();
        }

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            googleAPIInvoker.deleteCluster(enterpriseConfigs.getEnterpriseId(), pageId, clusterId);
            return Response.status(Response.Status.OK).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when deleting cluster "
                            + clusterId).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when deleting cluster "
                            + clusterId + " , due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while deleting cluster";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @GET
    @Path("/store-layout/page/{pageId}/clusters")
    @Override
    public Response getClustersInPage(@PathParam("pageId") String pageId) {
        if (pageId == null || pageId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Page id cannot be empty").build()).build();
        }

        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            StoreLayoutClustersListResponse response = googleAPIInvoker.getClusters(enterpriseConfigs.getEnterpriseId(), pageId);
            if (response == null || response.getCluster() == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage("Page id cannot be found").build()).build();
            }
            List<EnterpriseStoreCluster> clusters = new ArrayList<>();
            for (StoreCluster cluster : response.getCluster()) {
                EnterpriseStoreCluster storeCluster = new EnterpriseStoreCluster();
                storeCluster.setClusterId(cluster.getId());
                storeCluster.setName(cluster.getName().get(0).getText());
                storeCluster.setOrderInPage(cluster.getOrderInPage());


                List<String> productIds = new ArrayList<>();
                for (String productId : cluster.getProductId()) {
                    String trimmedPackage = productId.replaceFirst("app:", "");
                    productIds.add(trimmedPackage);
                }
                ApplicationManager appManager = AndroidAPIUtils.getAppManagerService();
                List<ApplicationReleaseDTO> packageDetails = appManager.getReleaseByPackageNames(productIds);


                List<EnterpriseStorePackages> enterpriseStorePackages = new ArrayList<>();
                for (String productId : cluster.getProductId()) {
                    String trimmedPackage = productId.replaceFirst("app:", "");

                    EnterpriseStorePackages storePackages = new EnterpriseStorePackages();
                    storePackages.setPackageId(productId);
                    for (ApplicationReleaseDTO releaseDTO : packageDetails) {
                        if (releaseDTO.getPackageName().equalsIgnoreCase(trimmedPackage)) {
                            storePackages.setIconUrl(releaseDTO.getIconName());
                            break;
                        }
                    }

                    enterpriseStorePackages.add(storePackages);
                }
                storeCluster.setProducts(enterpriseStorePackages);
                clusters.add(storeCluster);

            }
            return Response.status(Response.Status.OK).entity(clusters).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching clusters in pageId "
                            + pageId).build()).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching clusters in pageId "
                            + pageId + " , due to an error with ESA").build()).build();
        } catch (ApplicationManagementException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching all details in PageId "
                            + pageId).build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while getting clusters in page";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @PUT
    @Path("/store-layout/page-link")
    @Override
    public Response updateLinks(EnterpriseStorePageLinks link) {
        try {
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

            googleAPIInvoker.addLinks(enterpriseConfigs.getEnterpriseId(),
                    link.getPageId(), link.getLinks());
            return Response.status(Response.Status.OK).build();
        } catch (IOException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching all pages").build())
                    .build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching page "
                            + " , Due to an error with ESA").build()).build();
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while updating links";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/managed-configs/package/{packageName}")
    public Response getConfig(@PathParam("packageName") String packageName) {
        if (packageName== null || packageName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Package name is incorrect").build()).build();
        }

        AndroidEnterpriseManagedConfig managedConfig;
        try {
            managedConfig = AndroidAPIUtils.getAndroidPluginService().getConfigByPackageName(packageName);
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving configs").build()).build();
        }
        return Response.status(Response.Status.OK).entity(managedConfig).build();
    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/managed-configs")
    public Response addManagedConfigs(AndroidEnterpriseManagedConfig managedConfig) {
        if (managedConfig == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        } else if (managedConfig.getPackageName() == null || managedConfig.getPackageName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Package name is incorrect").build()).build();
        } else if (managedConfig.getProfileName() == null || managedConfig.getProfileName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Profile name is incorrect").build()).build();
        }

        try {
            AndroidAPIUtils.getAndroidPluginService().addConfig(managedConfig);
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving configs for "
                                    +  managedConfig.getPackageName()).build()).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/managed-configs")
    public Response updateManagedConfigs(AndroidEnterpriseManagedConfig managedConfig) {
        if (managedConfig == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Message body is empty or incorrect").build())
                    .build();
        } else if (managedConfig.getProfileName() == null || managedConfig.getProfileName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Profile name is incorrect").build()).build();
        }

        try {
            AndroidAPIUtils.getAndroidPluginService().updateMobileDevice(managedConfig);
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving configs").build()).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    @DELETE
    @Path("/managed-configs/mcm/{mcmId}")
    @Consumes(MediaType.WILDCARD)
    public Response deleteManagedConfigs(@PathParam("mcmId") String mcmId) {
        if (mcmId == null || mcmId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("MCM Id is incorrect").build()).build();
        }

        try {
            AndroidAPIUtils.getAndroidPluginService().deleteMobileDevice(mcmId);
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving configs").build()).build();
        }
        return Response.status(Response.Status.OK).build();
    }


    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/change-app")
    public Response updateUser(ApplicationPolicyDTO applicationPolicyDTO) {

        boolean sentToDevice = false;


        for (DeviceIdentifier deviceIdentifier : applicationPolicyDTO.getDeviceIdentifierList()) {
            try {
                EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
                GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());

                AndroidEnterpriseUser userDetail = AndroidAPIUtils.getAndroidPluginService()
                        .getEnterpriseUserByDevice(deviceIdentifier.getId());
                    if (userDetail != null && userDetail.getEnterpriseId() != null && !userDetail.getEnterpriseId()
                            .isEmpty() && userDetail.getEmmUsername() != null) {

                        if (applicationPolicyDTO.getAction().equals(AndroidConstants.ApplicationInstall.INSTALL)) {
                            if (applicationPolicyDTO.getPolicy() == null) {
                                ProfileFeature feature = AndroidDeviceUtils.getEnrollmentFeature(deviceIdentifier);
                                EnterpriseInstallPolicy enterpriseInstallPolicy = AndroidEnterpriseUtils
                                        .getDeviceAppPolicy(null, feature, userDetail);

                                List<String> apps = new ArrayList<>();
                                boolean isAppWhitelisted = false;
                                for (EnterpriseApp enterpriseApp : enterpriseInstallPolicy.getApps()) {
                                    apps.add(enterpriseApp.getProductId());
                                    String packageName = enterpriseApp.getProductId().replace("app:", "");
                                    if (applicationPolicyDTO.getApplicationDTO().getPackageName().equals(packageName)) {
                                        isAppWhitelisted = true;
                                    }
                                }

                                if (enterpriseInstallPolicy.getProductSetBehavior().equals(AndroidConstants
                                        .ApplicationInstall.BEHAVIOUR_WHITELISTED_APPS_ONLY)) {
                                    // This app can only be installed if the app is approved by whitelist to user.
                                    if (!isAppWhitelisted) {
                                        String errorMessage = "App: " + applicationPolicyDTO.getApplicationDTO()
                                                .getPackageName() + " for device " + deviceIdentifier.getId();
                                        log.error(errorMessage);
                                        return Response.status(Response.Status.BAD_REQUEST).entity(
                                                new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                                                        .setMessage(errorMessage).build()).build();
                                    }
                                }
                                googleAPIInvoker.installApps(enterpriseConfigs.getEnterpriseId(), userDetail
                                        .getGoogleUserId(), userDetail.getAndroidPlayDeviceId(), "app:" +
                                        applicationPolicyDTO.getApplicationDTO().getPackageName());

                                sentToDevice = true;
                            }
                        } else if (applicationPolicyDTO.getAction().equals(AndroidConstants.ApplicationInstall.UNINSTALL)) {

                            googleAPIInvoker.uninstallApps(enterpriseConfigs.getEnterpriseId(), userDetail
                                    .getGoogleUserId(), userDetail.getAndroidPlayDeviceId(), "app:" +
                                    applicationPolicyDTO.getApplicationDTO().getPackageName());
                            sentToDevice = true;
                        }

                    }

            } catch (EnterpriseServiceException e) {
                String errorMessage = "App install failed for device " + deviceIdentifier.getId();
                log.error(errorMessage);
                return Response.status(Response.Status.NOT_FOUND).entity(
                        new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_NOT_FOUND)
                                .setMessage(errorMessage).build()).build();
            } catch (FeatureManagementException e) {
                String errorMessage = "Could not fetch effective policy for device " + deviceIdentifier.getId();
                log.error(errorMessage);
                return Response.status(Response.Status.NOT_FOUND).entity(
                        new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_NOT_FOUND)
                                .setMessage(errorMessage).build()).build();
            } catch (NotFoundException e) {
                String errorMessage = "Not found";
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
            } catch (UnexpectedServerErrorException e) {
                String errorMessage = "Unexpected server error";
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
            } catch (AndroidDeviceMgtPluginException e) {
                String errorMessage = "Error occured while updating user";
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
            }
        }

        if (sentToDevice) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Could not install on the device of user "
                            ).build()).build();
        }
    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/wipe-device")
    public Response wipeEnterprise() {
        log.warn("Wiping all devices!!!");
        try{
            EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
            try {
                // Take all enterprise devices in the DB.
                List<AndroidEnterpriseUser> androidEnterpriseUsers = AndroidAPIUtils.getAndroidPluginService()
                        .getAllEnterpriseDevices(enterpriseConfigs.getEnterpriseId());

                // Extract the device identifiers of enterprise devices.
                List<String> deviceID = new ArrayList<>();
                if (androidEnterpriseUsers != null && !androidEnterpriseUsers.isEmpty()) {
                    for (AndroidEnterpriseUser userDevice: androidEnterpriseUsers) {
                        deviceID.add(userDevice.getEmmDeviceId());
                    }
                }

                List<String> byodDevices = new ArrayList<>();
                List<String> copeDevices = new ArrayList<>();
                // Get all registered device
                List<Device> devices = AndroidAPIUtils.getDeviceManagementService().
                        getAllDevices(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID, false);
                for (Device device : devices) { // Go through all enrolled devices
                    if (deviceID.contains(device.getDeviceIdentifier())) { // Filter out only enterprise enrolled devices.
                        if (device.getEnrolmentInfo().getOwnership().equals(EnrolmentInfo.OwnerShip.BYOD)) {
                            byodDevices.add(device.getDeviceIdentifier());
                        } else {
                            copeDevices.add(device.getDeviceIdentifier());
                        }
                    }
                }

                CommandOperation operation = new CommandOperation();
                operation.setType(Operation.Type.COMMAND);//TODO: Check if this should be profile
                // type when implementing COPE/COSU
                if (byodDevices != null && !byodDevices.isEmpty()) { // BYOD devices only needs a data wipe(work profile)
                    log.warn("Wiping " + byodDevices.size() + " BYOD devices");
                    operation.setCode(AndroidConstants.OperationCodes.ENTERPRISE_WIPE);
                } else if (copeDevices != null && !copeDevices.isEmpty()) {
                    log.warn("Wiping " + copeDevices.size() + " COPE/COSU devices");
                    operation.setCode(AndroidConstants.OperationCodes.WIPE_DATA);
                }
                AndroidDeviceUtils.getOperationResponse(deviceID, operation);
                log.warn("Added wipe to all devices");
                return Response.status(Response.Status.OK).build();
            } catch (EnterpriseServiceException e) {
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving configs").build()).build();
            } catch (OperationManagementException e) {
                String errorMessage = "Could not add wipe command to enterprise " + enterpriseConfigs.getEnterpriseId();
                log.error(errorMessage);
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
            } catch (DeviceManagementException e) {
                String errorMessage = "Could not add wipe command to enterprise " + enterpriseConfigs.getEnterpriseId() +
                        " due to an error in device management";
                log.error(errorMessage);
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
            } catch (InvalidDeviceException e) {
                String errorMessage = "Could not add wipe command to enterprise due to invalid device ids";
                log.error(errorMessage);
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
            }
        } catch (NotFoundException e) {
            String errorMessage = "Not found";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (UnexpectedServerErrorException e) {
            String errorMessage = "Unexpected server error";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        } catch (AndroidDeviceMgtPluginException e) {
            String errorMessage = "Error occured while executing wipe enterprice command";
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(errorMessage).build()).build();
        }
    }

}
