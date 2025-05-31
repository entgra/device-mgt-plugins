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
package io.entgra.device.mgt.plugins.emqx.initializer.internal;

import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.plugins.emqx.initializer.EmqxExhookInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.core.ServerShutdownHandler;
import org.wso2.carbon.core.ServerStartupObserver;
import org.osgi.service.component.annotations.*;

@Component(
        name = "io.entgra.device.mgt.plugins.emqx.initializer.internal.EmqxExhookServiceComponent",
        immediate = true)
public class EmqxExhookServiceComponent {
    private static final Log log = LogFactory.getLog(EmqxExhookServiceComponent.class);

    @Activate
    protected void activate(ComponentContext ctx) {
        try {

            EmqxExhookInitializer initializer = new EmqxExhookInitializer();
            BundleContext bundleContext = ctx.getBundleContext();

            bundleContext.registerService(ServerStartupObserver.class.getName(), initializer, null);
            bundleContext.registerService(ServerShutdownHandler.class.getName(), initializer, null);

            if (log.isDebugEnabled()) {
                log.debug("EmqxExhookServiceComponent has been successfully activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while activating EmqxExhookServiceComponent", e);
        }

    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("EmqxExhookServiceComponent has been successfully de-activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while de-activating EmqxExhookServiceComponent", e);
        }
    }

    @Reference(
            name = "device.mgt.provider.service",
            service = io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDeviceManagementServiceProviderService")
    protected void setDeviceManagementServiceProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        EmqxExhookDataHolder.getInstance().setDeviceManagementProviderService(deviceManagementProviderService);
        if (log.isDebugEnabled()) {
            log.debug("Device management provider service is set successfully");
        }
    }

    protected void unsetDeviceManagementServiceProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        EmqxExhookDataHolder.getInstance().setDeviceManagementProviderService(null);
        if (log.isDebugEnabled()) {
            log.debug("Device management provider service is unset successfully");
        }
    }
}
