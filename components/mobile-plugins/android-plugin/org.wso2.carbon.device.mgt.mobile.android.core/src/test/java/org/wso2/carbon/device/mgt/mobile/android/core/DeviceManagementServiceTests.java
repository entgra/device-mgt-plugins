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

import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.AndroidDevice;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.core.impl.AndroidServiceImpl;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.ApplicationManagementProviderServiceMock;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.DeviceInformationManagerServiceMock;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.DeviceManagementProviderServiceMock;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.NotificationManagementServiceMock;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.PolicyManagerServiceMock;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidAPIUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.mokcs.utils.TestUtils;
import org.wso2.carbon.device.mgt.mobile.android.core.util.AndroidDeviceUtils;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@PowerMockIgnore({"javax.ws.rs.*", "org.apache.log4j.*"})
@PrepareForTest(AndroidAPIUtils.class)
public class DeviceManagementServiceTests {

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

    private void mockApplicationManagerService() {
        PowerMockito.stub(PowerMockito.method(AndroidAPIUtils.class, "getApplicationManagerService"))
                .toReturn(new ApplicationManagementProviderServiceMock());
    }

    private void mockPolicyManagerService() {
        PowerMockito.stub(PowerMockito.method(AndroidAPIUtils.class, "getPolicyManagerService"))
                .toReturn(new PolicyManagerServiceMock());
    }

    private void mockDeviceInformationManagerService() {
        PowerMockito.stub(PowerMockito.method(AndroidAPIUtils.class, "getDeviceInformationManagerService"))
                .toReturn(new DeviceInformationManagerServiceMock());
    }

    private void mockNotificationManagementService() {
        PowerMockito.stub(PowerMockito.method(AndroidAPIUtils.class, "getNotificationManagementService"))
                .toReturn(new NotificationManagementServiceMock());
    }

    private void mockUser() {
        PowerMockito.stub(PowerMockito.method(AndroidAPIUtils.class, "getAuthenticatedUser"))
                .toReturn("admin");
    }

    @Test
    public void testUpdateApplicationList()
            throws ApplicationManagementException {
        mockApplicationManagerService();
        Message message = androidService
                .updateApplicationList(TestUtils.getDeviceId(), TestUtils.getAndroidApplications());
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getResponseCode(), Response.Status.ACCEPTED.toString());
    }

    @Test (expectedExceptions = {InvalidDeviceException.class})
    public void testGetPendingOperationsForNullDevice()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        List<? extends Operation> resultOperations = new ArrayList<>();
        androidService.getPendingOperations(null, resultOperations);
    }

    @Test (expectedExceptions = {InvalidDeviceException.class})
    public void testGetPendingOperationsForInvalidDevice()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        List<? extends Operation> resultOperations = new ArrayList<>();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject("1234");
        androidService.getPendingOperations(deviceIdentifier, resultOperations);
    }

    @Test
    public void testGetPendingOperationsNullResponse()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(TestUtils.getDeviceId());
        List<? extends Operation> pendingOperations = androidService
                .getPendingOperations(deviceIdentifier, null);
        Assert.assertNotNull(pendingOperations);
        Assert.assertFalse((pendingOperations.isEmpty()));
    }

    @Test
    public void testGetPendingOperationsWithMonitorResponse()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockPolicyManagerService();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(TestUtils.getDeviceId());
        List<? extends Operation> pendingOperations = androidService
                .getPendingOperations(deviceIdentifier, TestUtils.getSuccessMonitorOperationResponse());
        Assert.assertNotNull(pendingOperations);
        Assert.assertFalse((pendingOperations.isEmpty()));
    }

    @Test
    public void testGetPendingOperationsWithApplicationResponse()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockApplicationManagerService();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(TestUtils.getDeviceId());
        List<? extends Operation> pendingOperations = androidService
                .getPendingOperations(deviceIdentifier, TestUtils.getSuccessApplicationOperationResponse());
        Assert.assertNotNull(pendingOperations);
        Assert.assertFalse((pendingOperations.isEmpty()));
    }

    @Test
    public void testGetPendingOperationsWithDeviceInfoResponse()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockDeviceInformationManagerService();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(TestUtils.getDeviceId());
        List<? extends Operation> pendingOperations = androidService
                .getPendingOperations(deviceIdentifier, TestUtils.getSuccessInfoOperationResponse());
        Assert.assertNotNull(pendingOperations);
        Assert.assertFalse((pendingOperations.isEmpty()));
    }

    @Test
    public void testGetPendingOperationsWithInProgressResponse()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(TestUtils.getDeviceId());
        List<? extends Operation> pendingOperations = androidService
                .getPendingOperations(deviceIdentifier, TestUtils.getInProgressOperationResponse());
        Assert.assertNotNull(pendingOperations);
        Assert.assertFalse((pendingOperations.isEmpty()));
    }

    @Test
    public void testGetPendingOperationsWithErrorResponse()
            throws DeviceManagementException, InvalidDeviceException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockNotificationManagementService();
        DeviceIdentifier deviceIdentifier = AndroidDeviceUtils.convertToDeviceIdentifierObject(TestUtils.getDeviceId());
        List<? extends Operation> pendingOperations = androidService
                .getPendingOperations(deviceIdentifier, TestUtils.getErrorOperationResponse());
        Assert.assertNotNull(pendingOperations);
        Assert.assertFalse((pendingOperations.isEmpty()));
    }

    @Test
    public void testEnrollDeviceWithoutLocationSuccess()
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockPolicyManagerService();
        mockUser();
        Message message = androidService.enrollDevice(TestUtils.getBasicAndroidDevice());
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getResponseCode(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testEnrollDeviceWithLocationSuccess()
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockDeviceInformationManagerService();
        mockPolicyManagerService();
        mockUser();
        AndroidDevice androidDevice = TestUtils.getBasicAndroidDevice();

        List<Device.Property> properties = new ArrayList<>();
        Device.Property property = new Device.Property();
        property.setName("LATITUDE");
        property.setValue("79.5");
        properties.add(property);
        property = new Device.Property();
        property.setName("LONGITUDE");
        property.setValue("6.9");
        properties.add(property);
        property = new Device.Property();
        property.setName("ALTITUDE");
        property.setValue("-59.8373726");
        properties.add(property);
        property = new Device.Property();
        property.setName("SPEED");
        property.setValue("0.5123423333");
        properties.add(property);
        property = new Device.Property();
        property.setName("BEARING");
        property.setValue("44.0");
        properties.add(property);
        property = new Device.Property();
        property.setName("DISTANCE");
        property.setValue("44.0");
        properties.add(property);
        androidDevice.setProperties(properties);

        Message message = androidService.enrollDevice(androidDevice);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getResponseCode(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testEnrollDeviceUnSuccess()
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockUser();
        AndroidDevice androidDevice = TestUtils.getBasicAndroidDevice();
        androidDevice.setDeviceIdentifier("1234");
        Message message = androidService.enrollDevice(androidDevice);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getResponseCode(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testIsEnrolledExists()
            throws DeviceManagementException {
        mockDeviceManagementService();
        Message message = androidService.isEnrolled(TestUtils.getDeviceId(), null);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getResponseCode(), Response.Status.OK.toString());
    }

    @Test
    public void testIsEnrolledNonExist()
            throws DeviceManagementException {
        mockDeviceManagementService();
        Message message = androidService.isEnrolled("1234", null);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getResponseCode(), Response.Status.NOT_FOUND.toString());
    }

    @Test
    public void testIsEnrolledNull()
            throws DeviceManagementException {
        mockDeviceManagementService();
        Message response = androidService.isEnrolled(null, null);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), Response.Status.NOT_FOUND.toString());
    }

    @Test
    public void testModifyEnrollmentSuccess()
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockUser();
        boolean status = androidService.modifyEnrollment(TestUtils.getDeviceId(), TestUtils.getBasicAndroidDevice());
        Assert.assertTrue(status);
    }

    @Test
    public void testModifyEnrollmentUnSuccess()
            throws DeviceManagementException, AndroidDeviceMgtPluginException {
        mockDeviceManagementService();
        mockUser();
        AndroidDevice androidDevice = TestUtils.getBasicAndroidDevice();
        androidDevice.setDeviceIdentifier("1234");
        boolean status = androidService
                .modifyEnrollment(TestUtils.getDeviceId(), androidDevice);
        Assert.assertFalse(status);
    }

    @Test
    public void testDisEnrollDeviceSuccess()
            throws DeviceManagementException {
        mockDeviceManagementService();
        boolean status = androidService.disEnrollDevice(TestUtils.getDeviceId());
        Assert.assertTrue(status);
    }

    @Test
    public void testDisEnrollUnSuccess()
            throws DeviceManagementException {
        mockDeviceManagementService();
        boolean status = androidService.disEnrollDevice("1234");
        Assert.assertFalse(status);
    }
}

