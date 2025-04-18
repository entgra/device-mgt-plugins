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

package io.entgra.device.mgt.plugins.virtualfirealarm.api.service.impl.xmpp;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.HashMap;
import java.util.Map;

public class XmppServerClient {

    public static boolean createAccount(XmppAccount xmppAccount) throws VirtualFirealarmXMPPException {
        if (XmppConfig.getInstance().isEnabled()) {
            if (xmppAccount != null) {
                try {
                    ConnectionConfiguration config = new ConnectionConfiguration(XmppConfig.getInstance().getHost(),
                                                                                 XmppConfig.getInstance().getPort(),
                                                                                 "Accounts");
                    XMPPConnection xmppConnection = new XMPPConnection(config);
                    xmppConnection.connect();
                    xmppConnection.login(XmppConfig.getInstance().getUsername(), XmppConfig.getInstance().getPassword());
                    AccountManager accountManager = xmppConnection.getAccountManager();
                    Map<String, String> attributes = new HashMap<>();
                    attributes.put("username", xmppAccount.getUsername());
                    attributes.put("password", xmppAccount.getPassword());
                    attributes.put("email", xmppAccount.getEmail());
                    attributes.put("name", xmppAccount.getAccountName());
                    accountManager.createAccount(xmppAccount.getUsername(), xmppAccount.getPassword(), attributes);
                    xmppConnection.disconnect();
                    return true;
                } catch (XMPPException e) {
                    if (e.getXMPPError().getCode() == 409) {
                        //AccountAlreadyExist
                        return true;
                    } else {
                        throw new VirtualFirealarmXMPPException(
                                "XMPP account creation failed. Error: " + e.getLocalizedMessage(), e);
                    }
                }
            } else {
                throw new VirtualFirealarmXMPPException("Invalid XMPP attributes");
            }
        } else {
            return true;
        }
    }
}
