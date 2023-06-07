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
package io.entgra.device.mgt.plugins.mqtt.notification.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.pull.notification.PullNotificationExecutionFailedException;
import io.entgra.device.mgt.plugins.mqtt.notification.listener.internal.MqttNotificationDataHolder;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterSubscription;
import org.wso2.carbon.user.api.UserStoreException;

/**
 * Creates a event subscription for the input adapter.
 */
public class DeviceTypeOperationAdapterSubscription implements InputEventAdapterSubscription {
    private static final Log log = LogFactory.getLog(DeviceTypeOperationAdapterSubscription.class);

    @Override
    public void onEvent(Object o) {

        if (o == null || !(o instanceof  NotificationMessage)) {
            return;
        }

        NotificationMessage notificationMessage = (NotificationMessage) o;
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(notificationMessage.getTenantDomain(),
                                                                              true);
        String deviceType = "";
        try {
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(PrivilegedCarbonContext.
                    getThreadLocalCarbonContext().getUserRealm().getRealmConfiguration().getAdminUserName());
            deviceType = notificationMessage.getDeviceIdentifier().getType();
            Device device = MqttNotificationDataHolder.getInstance().getDeviceManagementProviderService()
                    .getDevice(notificationMessage.getDeviceIdentifier(), false);
            MqttNotificationDataHolder.getInstance().getDeviceManagementProviderService().
                    notifyPullNotificationSubscriber(device, notificationMessage.getOperation());
        } catch (UserStoreException e) {
            log.error("Failed to retrieve tenant username", e);
        } catch (DeviceManagementException e) {
            log.error("Failed to retrieve device " + notificationMessage.getDeviceIdentifier(), e);
        } catch (PullNotificationExecutionFailedException e) {
            log.error("Failed to execute device type pull notification subscriber execution for device type" + deviceType,
                    e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }
}
