/*
 *   Copyright (c) 2020, Entgra (pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 *   Entgra (pvt) Ltd. licenses this file to you under the Apache License,
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

package io.entgra.healthcheck;

import io.entgra.healthcheck.utils.APIUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class HealthCheck {

    private static Log log = LogFactory.getLog(HealthCheck.class);

    @GET
    public Response check() {
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext()
                    .setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);
            DeviceManagementProviderService dms = APIUtils.getDeviceManagementService();
            return Response.ok("OK")
                    .entity(dms.getFunctioningDevicesInSystem()).build();
        } catch (Exception e) {
            log.error("Error occurred while invoking health check,", e);
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }
}
