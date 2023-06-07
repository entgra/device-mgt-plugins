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
package io.entgra.device.mgt.plugins.emqx.initializer;


import io.entgra.device.mgt.plugins.emqx.exhook.ExServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.ServerShutdownHandler;
import org.wso2.carbon.core.ServerStartupObserver;

import java.io.IOException;

public class EmqxExhookInitializer implements ServerShutdownHandler, ServerStartupObserver {
    ExServer exServer = null;

    private static final Log log = LogFactory.getLog(EmqxExhookInitializer.class);

    @Override
    public void completingServerStartup() {

    }

    @Override
    public void completedServerStartup() {
        log.info("completedServerStartup() ");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                exServer = new ExServer();
                try {
                    exServer.start();
                    exServer.blockUntilShutdown();
                } catch (IOException e) {
                    log.error("Error while starting the EMQX extension server");
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    log.error("Error while blocking until shutdown");
                    throw new RuntimeException(e);
                }
            }
        };

        new Thread(r).start();
    }

    @Override
    public void invoke() {
        try {
            exServer.stop();
        } catch (InterruptedException e) {
            log.error("Error while stopping the EMQX Extension server");

        }
    }
}
