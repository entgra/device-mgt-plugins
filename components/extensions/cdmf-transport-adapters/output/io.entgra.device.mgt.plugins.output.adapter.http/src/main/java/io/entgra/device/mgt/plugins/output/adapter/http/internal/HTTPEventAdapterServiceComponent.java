/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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
package io.entgra.device.mgt.plugins.output.adapter.http.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import io.entgra.device.mgt.plugins.output.adapter.http.HTTPEventAdapterFactory;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterFactory;
import io.entgra.device.mgt.core.identity.jwt.client.extension.service.JWTClientManagerService;
import org.osgi.service.component.annotations.*;

@Component(
        name = "io.entgra.device.mgt.plugins.output.adapter.http.internal.HTTPEventAdapterServiceComponent",
        immediate = true)
public class HTTPEventAdapterServiceComponent {

    private static final Log log = LogFactory.getLog(HTTPEventAdapterServiceComponent.class);
    @Activate
    protected void activate(ComponentContext context) {
        try {
            HTTPEventAdapterFactory httpEventAdaptorFactory = new HTTPEventAdapterFactory();
            context.getBundleContext().registerService(OutputEventAdapterFactory.class.getName(),
                                                       httpEventAdaptorFactory, null);
            if (log.isDebugEnabled()) {
                log.debug("Successfully deployed the output HTTP event adaptor service");
            }
        } catch (RuntimeException e) {
            log.error("Exception occurred when deploying HTTP publisher service", e);
        }
    }

    @Reference(
            name = "jwt.client.manager.service",
            service = io.entgra.device.mgt.core.identity.jwt.client.extension.service.JWTClientManagerService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetJWTClientManagerService")
    protected void setJWTClientManagerService(JWTClientManagerService jwtClientManagerService) {
        OutputAdapterServiceDataHolder.setJwtClientManagerService(jwtClientManagerService);
    }

    protected void unsetJWTClientManagerService(JWTClientManagerService jwtClientManagerService) {
        OutputAdapterServiceDataHolder.setJwtClientManagerService(null);
    }
}
