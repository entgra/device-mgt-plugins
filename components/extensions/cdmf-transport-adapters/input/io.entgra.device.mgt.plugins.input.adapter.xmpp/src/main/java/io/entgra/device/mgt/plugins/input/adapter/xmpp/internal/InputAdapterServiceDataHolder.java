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
package io.entgra.device.mgt.plugins.input.adapter.xmpp.internal;

import org.osgi.service.http.HttpService;
import io.entgra.device.mgt.plugins.input.adapter.extension.InputAdapterExtensionService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * common place to hold some OSGI service references.
 */
public final class InputAdapterServiceDataHolder {

	private static RealmService realmService;
	private static HttpService httpService;
    private static InputAdapterExtensionService inputAdapterExtensionService;

	private InputAdapterServiceDataHolder() {
	}

	public static void registerRealmService(
			RealmService realmService) {
		InputAdapterServiceDataHolder.realmService = realmService;
	}

	public static RealmService getRealmService() {
		return realmService;
	}

	public static void registerHTTPService(
			HttpService httpService) {
		InputAdapterServiceDataHolder.httpService = httpService;
	}

	public static HttpService getHTTPService() {
		return httpService;
	}

    public static void setInputAdapterExtensionService(InputAdapterExtensionService inputAdapterExtensionService) {
        InputAdapterServiceDataHolder.inputAdapterExtensionService = inputAdapterExtensionService;
    }

    public static InputAdapterExtensionService getInputAdapterExtensionService() {
        return inputAdapterExtensionService;
    }

}
