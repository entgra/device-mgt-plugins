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
package org.wso2.carbon.mdm.services.android.services.impl;

import com.google.api.services.androidenterprise.model.ProductsListResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.mobile.android.impl.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseConfigs;
import org.wso2.carbon.mdm.services.android.bean.EnterpriseTokenUrl;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseInstallPolicy;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseUser;
import org.wso2.carbon.mdm.services.android.bean.wrapper.TokenWrapper;
import org.wso2.carbon.mdm.services.android.common.GoogleAPIInvoker;
import org.wso2.carbon.mdm.services.android.exception.NotFoundException;
import org.wso2.carbon.mdm.services.android.services.AndroidEnterpriseService;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.AndroidEnterpriseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/enterprise")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AndroidEnterpriseServiceImpl implements AndroidEnterpriseService {
    private static final Log log = LogFactory.getLog(AndroidEnterpriseServiceImpl.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/user")
    public Response addUser(EnterpriseUser enterpriseUser) {

        EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
        String token;
        boolean deviceIdExist = false;

        try {
            String googleUserId;
            List<AndroidEnterpriseUser> androidEnterpriseUsers = AndroidAPIUtils.getAndroidPluginService()
                    .getEnterpriseUser(CarbonContext.getThreadLocalCarbonContext().getUsername());
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
            if (androidEnterpriseUsers != null && androidEnterpriseUsers.size() > 0) {
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
            if (token == null) {
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage("Error when fetching token").build())
                        .build();
            }

        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when saving user").build()).build();
        }
        return Response.status(Response.Status.OK).entity(token).build();
    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/install-app")
    public Response updateUser(EnterpriseInstallPolicy device) {

        boolean sentToDevice = false;
        EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();

        try {
            List<AndroidEnterpriseUser> enterpriseUserInstances = AndroidAPIUtils.getAndroidPluginService()
                    .getEnterpriseUser(device.getUsername());
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
            for (AndroidEnterpriseUser userDetail : enterpriseUserInstances) {
                if (userDetail.getEnterpriseId() != null && !userDetail.getEnterpriseId().isEmpty() && userDetail
                        .getEmmUsername() != null && userDetail.getEmmUsername().equals(device.getUsername()) && device
                        .getAndroidId().equals(userDetail.getAndroidPlayDeviceId())) {
                    googleAPIInvoker.installApps(enterpriseConfigs.getEnterpriseId(), userDetail.getGoogleUserId(),
                            AndroidEnterpriseUtils
                            .convertToDeviceInstance(device));
                    sentToDevice = true;
                }
            }

        } catch (EnterpriseServiceException e) {
            String errorMessage = "App install failed. No user found for name " + device.getUsername();
            log.error(errorMessage);
            throw new NotFoundException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage(errorMessage).build());
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
                                @QueryParam("host") String host) {

        EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
        GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
        EnterpriseTokenUrl enterpriseTokenUrl = new EnterpriseTokenUrl();
        enterpriseTokenUrl.setEnterpriseId(enterpriseConfigs.getEnterpriseId());
        enterpriseTokenUrl.setApproveApps(approveApps);
        enterpriseTokenUrl.setSearchEnabled(searchEnabled);
        enterpriseTokenUrl.setPrivateAppsEnabled(isPrivateAppsEnabled);
        enterpriseTokenUrl.setWebAppEnabled(isWebAppEnabled);
        enterpriseTokenUrl.setOrganizeAppPageVisible(isOrganizeAppPageVisible);
        enterpriseTokenUrl.setParentHost(host);
        try {
            String token = googleAPIInvoker.getAdministratorWebToken(enterpriseTokenUrl);
            TokenWrapper tokenWrapper = new TokenWrapper();
            tokenWrapper.setToken(token);
            return Response.status(Response.Status.OK).entity(tokenWrapper).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when calling get web token").build())
                    .build();
        }
    }

    @Override
    @GET
    @Path("/products/sync")
    public Response syncApps() {
        EnterpriseConfigs enterpriseConfigs = AndroidEnterpriseUtils.getEnterpriseConfigs();
        GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker(enterpriseConfigs.getEsa());
        try {
            ProductsListResponse productsListResponse = googleAPIInvoker
                    .listProduct(enterpriseConfigs.getEnterpriseId(), null);
            persistProducts(productsListResponse);

            recursiveSync(googleAPIInvoker, enterpriseConfigs.getEnterpriseId(), productsListResponse);
            TokenWrapper tokenWrapper = new TokenWrapper();
            tokenWrapper.setToken("asdsa");
            return Response.status(Response.Status.OK).entity(tokenWrapper).build();
        } catch (EnterpriseServiceException e) {
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage("Error when calling sync").build())
                    .build();
        }
    }

    private void recursiveSync(GoogleAPIInvoker googleAPIInvoker, String enterpriseId, ProductsListResponse productsListResponse) throws EnterpriseServiceException {
        if (productsListResponse == null || productsListResponse.getTokenPagination() == null
                || productsListResponse.getTokenPagination().getNextPageToken() == null) {
            return;
        }

        ProductsListResponse productsListResponseNext = googleAPIInvoker.listProduct(enterpriseId,
                productsListResponse.getTokenPagination().getNextPageToken());
        persistProducts(productsListResponse);
        if (productsListResponseNext != null && productsListResponseNext.getTokenPagination() != null &&
                productsListResponseNext.getTokenPagination().getNextPageToken() != null && !productsListResponseNext
                .getTokenPagination().getNextPageToken().isEmpty()) {
            recursiveSync(googleAPIInvoker, enterpriseId, productsListResponse);
        }
    }

    private void persistProducts(ProductsListResponse productListResponse) {

    }

}
