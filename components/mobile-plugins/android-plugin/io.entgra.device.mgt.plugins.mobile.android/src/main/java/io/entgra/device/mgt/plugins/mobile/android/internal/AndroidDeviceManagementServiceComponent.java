/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.plugins.mobile.android.internal;

import io.entgra.device.mgt.plugins.mobile.android.impl.AndroidDeviceManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import io.entgra.device.mgt.core.device.mgt.common.spi.DeviceManagementService;
import org.wso2.carbon.ndatasource.core.DataSourceService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.osgi.service.component.annotations.*;

@Component(
        name = "io.entgra.device.mgt.plugins.mobile.android.internal.AndroidDeviceManagementServiceComponent",
        immediate = true)
public class AndroidDeviceManagementServiceComponent {

    private static final Log log = LogFactory.getLog(AndroidDeviceManagementServiceComponent.class);

    @Activate
    protected void activate(ComponentContext ctx) {

        if (log.isDebugEnabled()) {
            log.debug("Activating Android Mobile Device Management Service Component");
        }
        try {
            DeviceManagementService androidDeviceManagementService = new AndroidDeviceManagementService();
            AndroidDeviceManagementDataHolder.getInstance().setAndroidDeviceManagementService(
                    androidDeviceManagementService);
            if (log.isDebugEnabled()) {
                log.debug("Android Mobile Device Management Service Component has been successfully activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while activating Android Mobile Device Management Service Component", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("De-activating Android Mobile Device Management Service Component");
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Android Mobile Device Management Service Component has been successfully de-activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while de-activating Android Mobile Device Management bundle", e);
        }
    }

    @Reference(
            name = "datasource.service",
            service = org.wso2.carbon.ndatasource.core.DataSourceService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDataSourceService")
    protected void setDataSourceService(DataSourceService dataSourceService) {
        /* This is to avoid mobile device management component getting initialized before the underlying datasources
        are registered */
        if (log.isDebugEnabled()) {
            log.debug("Data source service set to android mobile service component");
        }
    }

    protected void unsetDataSourceService(DataSourceService dataSourceService) {
        //do nothing
    }

    @Reference(
            name = "registry.service",
            service = org.wso2.carbon.registry.core.service.RegistryService.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistryService")
    protected void setRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.debug("RegistryService acquired");
        }
        AndroidDeviceManagementDataHolder.getInstance().setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        AndroidDeviceManagementDataHolder.getInstance().setRegistryService(null);
    }

}
