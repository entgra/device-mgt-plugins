/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.plugins.output.adapter.websocket.endpoint.util;

import io.entgra.device.mgt.plugins.output.adapter.websocket.WebsocketOutputCallbackControllerService;
import io.entgra.device.mgt.plugins.output.adapter.websocket.service.WebsocketValidationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;

public class ServiceHolder {

    private static ServiceHolder instance;
    private WebsocketOutputCallbackControllerService websocketOutputCallbackControllerService;
    private static final Log log = LogFactory.getLog(ServiceHolder.class);

    private ServiceHolder(){
        websocketOutputCallbackControllerService = (WebsocketOutputCallbackControllerService) PrivilegedCarbonContext
                .getThreadLocalCarbonContext().getOSGiService(WebsocketOutputCallbackControllerService.class, null);
    }

    public synchronized static ServiceHolder getInstance() {
        if (instance == null) {
            instance = new ServiceHolder();
        }
        return instance;
    }

    public WebsocketOutputCallbackControllerService getWebsocketOutputCallbackControllerService() {
        return websocketOutputCallbackControllerService;
    }

    public static WebsocketValidationService getWebsocketValidationService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        WebsocketValidationService deviceManagementProviderService =
                (WebsocketValidationService) ctx.getOSGiService(WebsocketValidationService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Websocket Validation service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }
}
