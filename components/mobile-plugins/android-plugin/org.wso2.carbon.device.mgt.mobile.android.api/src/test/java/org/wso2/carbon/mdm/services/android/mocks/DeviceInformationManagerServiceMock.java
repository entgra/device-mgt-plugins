package org.wso2.carbon.mdm.services.android.mocks;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.device.details.DeviceInfo;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;

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
