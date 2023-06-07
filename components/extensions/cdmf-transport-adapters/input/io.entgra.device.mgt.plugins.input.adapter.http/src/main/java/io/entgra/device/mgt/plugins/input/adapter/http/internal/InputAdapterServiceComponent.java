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
package io.entgra.device.mgt.plugins.input.adapter.http.internal;

import io.entgra.device.mgt.plugins.input.adapter.http.HTTPEventAdapterFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.HttpService;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterFactory;
import org.wso2.carbon.user.core.service.RealmService;
import io.entgra.device.mgt.plugins.input.adapter.extension.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.*;

@Component(
		name = "io.entgra.device.mgt.plugins.input.adapter.http.internal.InputAdapterServiceComponent",
		immediate = true)
public class InputAdapterServiceComponent {

	private static final Log log = LogFactory.getLog(
			InputAdapterServiceComponent.class);

	@Activate
	protected void activate(ComponentContext context) {
		try {
			InputEventAdapterFactory httpEventEventAdapterFactory = new HTTPEventAdapterFactory();
			context.getBundleContext().registerService(InputEventAdapterFactory.class.getName(),
													   httpEventEventAdapterFactory, null);
			if (log.isDebugEnabled()) {
				log.debug("Successfully deployed the input adapter service");
			}
		} catch (RuntimeException e) {
			log.error("Can not create the input adapter service ", e);
		}
	}

	@Reference(
			name = "realm.service",
			service = org.wso2.carbon.user.core.service.RealmService.class,
			cardinality = ReferenceCardinality.MANDATORY,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "unsetRealmService")
	protected void setRealmService(RealmService realmService) {
		InputAdapterServiceDataHolder.registerRealmService(realmService);
	}

	protected void unsetRealmService(RealmService realmService) {
		InputAdapterServiceDataHolder.registerRealmService(null);
	}

	@Reference(
			name = "http.service",
			service = org.osgi.service.http.HttpService.class,
			cardinality = ReferenceCardinality.MANDATORY,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "unsetHttpService")
	protected void setHttpService(HttpService httpService) {
		InputAdapterServiceDataHolder.registerHTTPService(httpService);
	}

	protected void unsetHttpService(HttpService httpService) {
		InputAdapterServiceDataHolder.registerHTTPService(null);
	}

	@Reference(
			name = "input.adaptor.extension.service",
			service = io.entgra.device.mgt.plugins.input.adapter.extension.InputAdapterExtensionService.class,
			cardinality = ReferenceCardinality.MANDATORY,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "unsetInputAdapterExtensionService")
    protected void setInputAdapterExtensionService(InputAdapterExtensionService inputAdapterExtensionService) {
        InputAdapterServiceDataHolder.setInputAdapterExtensionService(inputAdapterExtensionService);
    }

    protected void unsetInputAdapterExtensionService(InputAdapterExtensionService inputAdapterExtensionService) {
        InputAdapterServiceDataHolder.setInputAdapterExtensionService(null);
    }

}
