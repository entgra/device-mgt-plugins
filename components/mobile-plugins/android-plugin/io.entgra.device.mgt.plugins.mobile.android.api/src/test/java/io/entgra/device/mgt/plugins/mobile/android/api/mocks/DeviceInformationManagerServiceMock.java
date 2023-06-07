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

package io.entgra.device.mgt.plugins.mobile.android.api.mocks;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceInfo;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceLocation;
import io.entgra.device.mgt.core.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import io.entgra.device.mgt.core.device.mgt.core.device.details.mgt.DeviceInformationManager;

import java.util.List;

public class DeviceInformationManagerServiceMock implements DeviceInformationManager {
    @Override
    public void addDeviceInfo(DeviceIdentifier deviceIdentifier, DeviceInfo deviceInfo)
            throws DeviceDetailsMgtException {

    }

    @Override
    public void addDeviceInfo(Device device, DeviceInfo deviceInfo) throws DeviceDetailsMgtException {

    }

    @Override
    public DeviceInfo getDeviceInfo(DeviceIdentifier deviceIdentifier) throws DeviceDetailsMgtException {
        return null;
    }

    @Override
    public DeviceInfo getDeviceInfo(Device device) throws DeviceDetailsMgtException {
        return null;
    }

    @Override
    public List<DeviceInfo> getDevicesInfo(List<DeviceIdentifier> list) throws DeviceDetailsMgtException {
        return null;
    }

    @Override
    public void addDeviceLocation(DeviceLocation deviceLocation) throws DeviceDetailsMgtException {

    }

    @Override
    public void addDeviceLocation(Device device, DeviceLocation deviceLocation) throws DeviceDetailsMgtException {

    }

    @Override public void addDeviceLocations(Device device, List<DeviceLocation> list)
            throws DeviceDetailsMgtException {

    }

    @Override
    public DeviceLocation getDeviceLocation(DeviceIdentifier deviceIdentifier) throws DeviceDetailsMgtException {
        return null;
    }

    @Override
    public List<DeviceLocation> getDeviceLocations(List<DeviceIdentifier> list) throws DeviceDetailsMgtException {
        return null;
    }

    @Override
    public int publishEvents(String s, String s1, String s2, String s3) throws DeviceDetailsMgtException {
        return 0;
    }
}
