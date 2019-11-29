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
 */
package org.wso2.carbon.device.mgt.mobile.android.api.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.device.mgt.analytics.data.publisher.exception.DataPublisherConfigurationException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.device.mgt.mobile.android.api.EventReceiverAPI;
import org.wso2.carbon.device.mgt.mobile.android.common.AndroidConstants;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.DeviceState;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.ErrorResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.EventBeanWrapper;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.NotFoundException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/events")
public class EventReceiverAPIImpl implements EventReceiverAPI {
    private static final String EVENT_STREAM_DEFINITION = "org.wso2.iot.LocationStream";
    private static final Log log = LogFactory.getLog(EventReceiverAPIImpl.class);
    private Gson gson = new Gson();

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ALTITUDE = "altitude";
    private static final String SPEED = "speed";
    private static final String DISTANCE = "distance";
    private static final String BEARING = "bearing";
    private static final String TIME_STAMP = "timeStamp";
    private static final String LOCATION_EVENT_TYPE = "location";

    @POST
    @Path("/publish")
    @Override
    public Response publishEvents(@Valid EventBeanWrapper eventBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device event logging.");
        }
        Device device;
        try {
            AndroidService androidService = AndroidAPIUtils.getAndroidService();
            device = androidService.publishEvents(eventBeanWrapper);
        } catch (DeviceManagementException e) {
            log.error("Error occurred while checking Operation Analytics is Enabled.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        String eventType = eventBeanWrapper.getType();
        if (!LOCATION_EVENT_TYPE.equals(eventType)) {
            String msg = "Dropping Android " + eventType + " Event.Only Location Event Type is supported.";
            log.warn(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        Message message = new Message();
        Object[] metaData = {eventBeanWrapper.getDeviceIdentifier(), device.getEnrolmentInfo().getOwner(),
                AndroidConstants.DEVICE_TYPE_ANDROID};
        String eventPayload = eventBeanWrapper.getPayload();
        JsonObject jsonObject = gson.fromJson(eventPayload, JsonObject.class);
        Object[] payload = {
                jsonObject.get(TIME_STAMP).getAsLong(),
                jsonObject.get(LATITUDE).getAsDouble(),
                jsonObject.get(LONGITUDE).getAsDouble(),
                jsonObject.get(ALTITUDE).getAsDouble(),
                jsonObject.get(SPEED).getAsFloat(),
                jsonObject.get(BEARING).getAsFloat(),
                jsonObject.get(DISTANCE).getAsDouble()
        };
        try {
            if (AndroidAPIUtils.getEventPublisherService().publishEvent(
                    EVENT_STREAM_DEFINITION, "1.0.0", metaData, new Object[0], payload)) {
                message.setResponseCode("Event is published successfully.");
                return Response.status(Response.Status.CREATED).entity(message).build();
            } else {
                log.warn("Error occurred while trying to publish the event. This could be due to unavailability " +
                        "of the publishing service. Please make sure that analytics server is running and accessible " +
                        "by this server");
                throw new UnexpectedServerErrorException(
                        new ErrorResponse.ErrorResponseBuilder().setCode(503l).setMessage("Error occurred due to " +
                                "unavailability of the publishing service.").build());
            }
        } catch (DataPublisherConfigurationException e) {
            String msg = "Error occurred while getting the Data publisher Service instance.";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(500l).setMessage(msg).build());
        }
    }

    @GET
    @Override
    public Response retrieveAlerts(@QueryParam("id")
                                       @Size(min = 2, max = 45)
                                       String deviceId,
                                   @QueryParam("from") long from,
                                   @QueryParam("to") long to,
                                       @Size(min = 2, max = 45)
                                   @QueryParam("type") String type,
                                   @HeaderParam("If-Modified-Since") String ifModifiedSince) {

        AndroidService androidService = AndroidAPIUtils.getAndroidService();
        Response response = androidService.retrieveAlerts(deviceId, from, to, type, ifModifiedSince);
        return response;
    }
}
