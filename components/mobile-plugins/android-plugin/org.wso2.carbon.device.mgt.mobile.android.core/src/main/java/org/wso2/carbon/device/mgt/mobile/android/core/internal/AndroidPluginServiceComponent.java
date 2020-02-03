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

package org.wso2.carbon.device.mgt.mobile.android.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.device.mgt.common.spi.DeviceTypeCommonService;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidGoogleEnterpriseService;
import org.wso2.carbon.device.mgt.mobile.android.common.spi.AndroidService;
import org.wso2.carbon.device.mgt.mobile.android.core.dao.AbstractMobileDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.mobile.android.core.impl.AndroidGoogleEnterpriseServiceImpl;
import org.wso2.carbon.device.mgt.mobile.android.core.impl.AndroidServiceImpl;
import org.wso2.carbon.device.mgt.mobile.android.core.impl.DeviceTypeCommonServiceImpl;
import org.wso2.carbon.device.mgt.mobile.android.core.util.MobileDeviceManagementUtil;
import org.wso2.carbon.ndatasource.core.DataSourceService;
import org.wso2.carbon.registry.core.service.RegistryService;

/**
 * @scr.component name="org.wso2.carbon.device.mgt.mobile.android.impl.internal.AndroidPluginServiceComponent"
 * immediate="true"
 * @scr.reference name="org.wso2.carbon.ndatasource"
 * interface="org.wso2.carbon.ndatasource.core.DataSourceService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setDataSourceService"
 * unbind="unsetDataSourceService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="0..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * <p>
 * Adding reference to API Manager Configuration service is an unavoidable hack to get rid of NPEs thrown while
 * initializing APIMgtDAOs attempting to register APIs programmatically. APIMgtDAO needs to be proper cleaned up
 * to avoid as an ideal fix
 */
public class AndroidPluginServiceComponent {

    private static final Log log = LogFactory.getLog(AndroidPluginServiceComponent.class);

    protected void activate(ComponentContext ctx) {

        if (log.isDebugEnabled()) {
            log.debug("Activating Android Mobile Device Management Service Component");
        }
        try {
            BundleContext bundleContext = ctx.getBundleContext();

            MobileDeviceManagementUtil.initConfig();
            AbstractMobileDeviceManagementDAOFactory.init("android", MobileDeviceManagementUtil
                    .getIosDataSourceConfigurations().getIosDataSourceConfiguration());

            AndroidService androidService = new AndroidServiceImpl();
            AndroidDeviceManagementDataHolder.getInstance().setAndroidService(
                    androidService);
            bundleContext.registerService(AndroidService.class.getName(), androidService, null);

            DeviceTypeCommonService deviceTypeCommonService = new DeviceTypeCommonServiceImpl();
            AndroidDeviceManagementDataHolder.getInstance().setDeviceTypeCommonService(
                    deviceTypeCommonService);
            bundleContext.registerService(DeviceTypeCommonService.class.getName(), deviceTypeCommonService, null);

            AndroidGoogleEnterpriseService androidPluginService = new AndroidGoogleEnterpriseServiceImpl();
            AndroidDeviceManagementDataHolder.getInstance().setAndroidDeviceManagementService(
                    androidPluginService);
            bundleContext.registerService(AndroidGoogleEnterpriseService.class.getName(), androidPluginService, null);
            if (log.isDebugEnabled()) {
                log.debug("Android Mobile Device Management Service Component has been successfully activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while activating Android Mobile Device Management Service Component", e);
        }
    }

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
