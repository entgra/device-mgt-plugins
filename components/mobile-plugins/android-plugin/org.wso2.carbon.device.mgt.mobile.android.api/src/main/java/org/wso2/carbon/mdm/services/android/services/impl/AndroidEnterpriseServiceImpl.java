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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.device.mgt.analytics.data.publisher.exception.DataPublisherConfigurationException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.mdm.services.android.bean.DeviceState;
import org.wso2.carbon.mdm.services.android.bean.ErrorResponse;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EventBeanWrapper;
import org.wso2.carbon.mdm.services.android.exception.BadRequestException;
import org.wso2.carbon.mdm.services.android.exception.NotFoundException;
import org.wso2.carbon.mdm.services.android.exception.UnexpectedServerErrorException;
import org.wso2.carbon.mdm.services.android.services.AndroidEnterpriseService;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.AndroidConstants;
import org.wso2.carbon.mdm.services.android.util.AndroidDeviceUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/enterprise")
public class AndroidEnterpriseServiceImpl implements AndroidEnterpriseService {
    private static final Log log = LogFactory.getLog(AndroidEnterpriseServiceImpl.class);


    @Override
    @POST
    @Path("/users")
    public Response addUser(@QueryParam("username") String username) {
        return null;
    }

    @Override
    @PUT
    @Path("/users")
    public Response updateUser(@QueryParam("username") String username) {
        return null;
    }

    @Override
    @DELETE
    @Path("/users")
    public Response deleteUser(@QueryParam("username") String username) {
        return null;
    }

    @Override
    @GET
    @Path("/user")
    public Response getUser(@QueryParam("username") String username) {
        String x =AndroidAPIUtils.getAndroidPluginService().addEnterpriseUser();
        return Response.status(Response.Status.OK).entity(x).build();
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
