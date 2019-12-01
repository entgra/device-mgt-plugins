/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.android.core;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.mobile.android.core.impl.AndroidServiceImpl;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.DeviceManagementProviderServiceMock;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.utils.TestUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;

@PowerMockIgnore({"javax.ws.rs.*", "org.apache.log4j.*"})
@PrepareForTest(AndroidAPIUtils.class)
public class DeviceManagementAdminServiceTests {

    private AndroidServiceImpl androidService;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        androidService = new AndroidServiceImpl();
    }

    private void mockDeviceManagementService() {
        PowerMockito.stub(PowerMockito.method(AndroidAPIUtils.class, "getDeviceManagementService"))
                .toReturn(new DeviceManagementProviderServiceMock());
    }

    @Test
    public void testConfigureDeviceLock() throws OperationManagementException {
        mockDeviceManagementService();
        Activity activity = androidService.configureDeviceLock(TestUtils.getDeviceLockBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testConfigureDeviceUnlock()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.configureDeviceUnlock(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testGetDeviceLocation()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.getDeviceLocation(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testRemovePassword()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.removePassword(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testConfigureCamera()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.configureCamera(TestUtils.getCamerabeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testGetDeviceInformation()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.getDeviceInformation(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testGetDeviceLogcat()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.getDeviceLogcat(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testWipeDevice()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.wipeDevice(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testWipeData()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.wipeData(TestUtils.getWipeDataBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testGetApplications()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.getApplications(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testRingDevice()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.ringDevice(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testRebootDevice()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.rebootDevice(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testChangeLockTask()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.changeLockTask(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testMuteDevice()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.muteDevice(TestUtils.getDeviceIds());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testInstallApplication()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService
                .installApplication(TestUtils.getApplicationInstallationBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testUpdateApplication()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.updateApplication(TestUtils.getApplicationUpdateBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testUninstallApplicationPublic()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService
                .uninstallApplication(TestUtils.getApplicationUninstallationBeanWrapperPublic());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testUninstallApplicationWebApp()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService
                .uninstallApplication(TestUtils.getApplicationUninstallationBeanWrapperWebApp());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testBlacklistApplications()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService
                .blacklistApplications(TestUtils.getBlacklistApplicationsBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testUpgradeFirmware()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.upgradeFirmware(TestUtils.getUpgradeFirmwareBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testConfigureVPN()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.configureVPN(TestUtils.getVpnBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testSendNotification()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.sendNotification(TestUtils.getNotificationBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testConfigureWifi()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.configureWifi(TestUtils.getWifiBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testEncryptStorage()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.encryptStorage(TestUtils.getEncryptionBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testChangeLockCode()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.changeLockCode(TestUtils.getLockCodeBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testSetPasswordPolicy()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.setPasswordPolicy(TestUtils.getPasswordPolicyBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }

    @Test
    public void testSetWebClip()
            throws DeviceManagementException, OperationManagementException, InvalidDeviceException {
        mockDeviceManagementService();
        Activity activity = androidService.setWebClip(TestUtils.getWebClipBeanWrapper());
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getActivityId());
    }
}
