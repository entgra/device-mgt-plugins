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
package io.entgra.device.mgt.plugins.mqtt.notification.listener.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.core.ServerStartupObserver;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.plugins.input.adapter.extension.ContentTransformer;
import io.entgra.device.mgt.plugins.mqtt.notification.listener.PullNotificationMqttContentTransformer;
import io.entgra.device.mgt.plugins.mqtt.notification.listener.PullNotificationStartupListener;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterService;

import org.osgi.service.component.annotations.*;

@Component(
        name = "io.entgra.device.mgt.plugins.mqtt.notification.listener.internal.PullNotificationListenerServiceComponent",
        immediate = true)
public class PullNotificationListenerServiceComponent {

    private static final Log log = LogFactory.getLog(PullNotificationListenerServiceComponent.class);

    @Activate
    @SuppressWarnings("unused")
    protected void activate(ComponentContext componentContext) {
        try {
            //Do nothing
            if (log.isDebugEnabled()) {
                log.debug("Pull notification provider implementation bundle has been successfully " +
                        "initialized");
            }
            BundleContext bundleContext = componentContext.getBundleContext();
            bundleContext.registerService(ServerStartupObserver.class.getName(), new PullNotificationStartupListener(),
                                          null);
            bundleContext.registerService(ContentTransformer.class, new PullNotificationMqttContentTransformer(), null);
        } catch (Throwable e) {
            log.error("Error occurred while initializing pull notification provider implementation bundle", e);
        }
    }

    protected void deactivate(ComponentContext componentContext) {
        //Do nothing
    }

    @Reference(
            name = "device.mgt.provider.service",
            service = io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDeviceManagementProviderService")
    protected void setDeviceManagementProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        MqttNotificationDataHolder.getInstance().setDeviceManagementProviderService(deviceManagementProviderService);
    }

    protected void unsetDeviceManagementProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        MqttNotificationDataHolder.getInstance().setDeviceManagementProviderService(deviceManagementProviderService);
    }

    @Reference(
            name = "input.event.adaptor.service",
            service = org.wso2.carbon.event.input.adapter.core.InputEventAdapterService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetInputEventAdapterService")
    protected void setInputEventAdapterService(InputEventAdapterService inputEventAdapterService) {
        MqttNotificationDataHolder.getInstance().setInputEventAdapterService(inputEventAdapterService);
    }

    protected void unsetInputEventAdapterService(InputEventAdapterService inputEventAdapterService) {
        MqttNotificationDataHolder.getInstance().setInputEventAdapterService(null);
    }

}
