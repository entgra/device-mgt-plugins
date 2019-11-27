/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 *   Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */
package org.wso2.carbon.device.mgt.mobile.android.api.impl;

import com.google.api.client.http.HttpStatusCodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.mobile.android.api.DeviceTypeConfigurationAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.AndroidPlatformConfiguration;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestExceptionDup;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceTypeConfigurationAPIImpl implements DeviceTypeConfigurationAPI {

    private static final Log log = LogFactory.getLog(DeviceTypeConfigurationAPIImpl.class);

    @GET
    @Override
    public Response getConfiguration(
            @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidnService();
            PlatformConfiguration platformConfiguration = androidService.getPlatformConfig();
            return Response.status(Response.Status.OK).entity(platformConfiguration).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the Android tenant configuration";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build());
        }
    }

    @PUT
    @Override
    public Response updateConfiguration(
            @Valid AndroidPlatformConfiguration androidPlatformConfiguration) {
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidnService();
            androidService.updateConfiguration(androidPlatformConfiguration);
            return Response.status(Response.Status.OK)
                    .entity("Android platform configuration has been updated successfully.").build();
        } catch (BadRequestExceptionDup e) {
            String msg = "The payload of the android platform configuration is incorrect.";
            log.error(msg, e);
            throw new BadRequestException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                            .setMessage(msg).build());
        } catch (AndroidDeviceMgtPluginException e) {
            String msg = "Error occurred while modifying configuration settings of Android platform";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build());
        }

    }

    @GET
    @Path("/license")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLicense(
            @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        try {
            License license = AndroidAPIUtils.getDeviceManagementService()
                    .getLicense(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID,
                            DeviceManagementConstants.LanguageCodes.LANGUAGE_CODE_ENGLISH_US);
            return Response.status(Response.Status.OK).entity((license == null) ? null : license.getText()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the license configured for Android device enrolment";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)
                            .setMessage(msg).build());
        }
    }
}
