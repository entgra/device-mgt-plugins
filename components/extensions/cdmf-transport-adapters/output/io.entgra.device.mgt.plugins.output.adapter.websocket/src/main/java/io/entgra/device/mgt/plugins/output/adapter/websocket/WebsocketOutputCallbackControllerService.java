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

package io.entgra.device.mgt.plugins.output.adapter.websocket;

import javax.websocket.Session;

/**
 * This interface is exposed as an OSGI service, which will be invoked by the local websocket endpoint to inform new subscriptions; and do un-subscriptions..
 */
public interface WebsocketOutputCallbackControllerService {

    /**
     * Used to subscribe the session id and stream id for later web socket connectivity
     *
     * @param streamName - Stream name which user register to.
     * @param version - Stream version which user uses.
     * @param session - Session which user registered.
     * @return
     */
    void subscribeWebsocket(String streamName, String version, Session session);

    /**
     * Used to return events per streamId
     *
     * @param streamName - Stream name which user register to.
     * @param version - Stream version which user uses.
     * @param session - Session which user subscribed to.
     * @return the events list.
     */
    void unsubscribeWebsocket(String streamName, String version, Session session);

}
