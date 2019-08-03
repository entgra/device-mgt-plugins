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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.mobile.android.impl.EnterpriseServiceException;
import org.wso2.carbon.device.mgt.mobile.android.impl.dto.AndroidEnterpriseUser;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EnterpriseUser;
import org.wso2.carbon.mdm.services.android.common.GoogleAPIInvoker;
import org.wso2.carbon.mdm.services.android.services.AndroidEnterpriseService;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

        String enterpriseId = "LC01chey4b";// Read this from config
        String token;
        boolean deviceIdExist = false;

        try {
            String googleUserId;
            List<AndroidEnterpriseUser> androidEnterpriseUsers = AndroidAPIUtils.getAndroidPluginService()
                    .getEnterpriseUser();
            GoogleAPIInvoker googleAPIInvoker = new GoogleAPIInvoker();
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
                googleUserId = googleAPIInvoker.insertUser(enterpriseId, CarbonContext.getThreadLocalCarbonContext()
                        .getUsername());
            }
            // Fetching an auth token from Google EMM API
            token = googleAPIInvoker.getToken(enterpriseId, googleUserId);

            if (!deviceIdExist) {
                AndroidEnterpriseUser androidEnterpriseUser = new AndroidEnterpriseUser();
                androidEnterpriseUser.setEmmUsername(CarbonContext.getThreadLocalCarbonContext().getUsername());
                androidEnterpriseUser.setTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantId());
                androidEnterpriseUser.setAndroidPlayDeviceId(enterpriseUser.getAndroidPlayDeviceId());
                androidEnterpriseUser.setEnterpriseId(enterpriseId);
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
    @PUT
    @Path("/user")
    public Response updateUser(@QueryParam("username") String username) {
        return null;
    }

    @Override
    @DELETE
    @Path("/user")
    public Response deleteUser(@QueryParam("username") String username) {
        return null;
    }

    @Override
    @GET
    @Path("/user")
    public Response getUser(@QueryParam("username") String username) {
        return null;
    }

    @Override
    @GET
    @Path("/users")
    public Response getUsers(@QueryParam("username") String username, @QueryParam("limit") String limit
            , @QueryParam("offset") String offset) {
        return null;
    }

    @Override
    public Response generateToken(String username) {
        return null;
    }
}
