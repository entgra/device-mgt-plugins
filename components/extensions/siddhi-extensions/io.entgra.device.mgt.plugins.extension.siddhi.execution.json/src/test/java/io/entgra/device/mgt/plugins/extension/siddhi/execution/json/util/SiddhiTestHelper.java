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

package io.entgra.device.mgt.plugins.extension.siddhi.execution.json.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SiddhiTestHelper {
    public static void waitForEvents(long sleepTime, int expectedCount, AtomicInteger actualCount, long timeout) throws InterruptedException {
        long currentWaitTime = 0;
        long startTime = System.currentTimeMillis();
        while ((actualCount.get() < expectedCount) && (currentWaitTime <= timeout)) {
            Thread.sleep(sleepTime);
            currentWaitTime = System.currentTimeMillis() - startTime;
        }
    }
}
