/* Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.android.common.spi;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceNotFoundException;
import org.wso2.carbon.device.mgt.common.exceptions.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;


import org.wso2.carbon.device.mgt.mobile.android.common.Message;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.AndroidPlatformConfiguration;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.EnterpriseTokenUrl;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.GoogleAppSyncResponse;
import org.wso2.carbon.device.mgt.mobile.android.common.bean.wrapper.*;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.AndroidDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseServiceException;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

public interface AndroidService {

    PlatformConfiguration getPlatformConfig() throws DeviceManagementException;

    void updateConfiguration(AndroidPlatformConfiguration androidPlatformConfiguration)
            throws AndroidDeviceMgtPluginException;

    Message isEnrolled(String id, DeviceIdentifier deviceIdentifier) throws DeviceManagementException;

    Activity fileTransfer(FileTransferBeanWrapper fileTransferBeanWrapper) throws OperationManagementException;

    Activity configureDeviceLock(DeviceLockBeanWrapper deviceLockBeanWrapper) throws OperationManagementException;

    Activity configureDeviceUnlock(List<String> deviceIDs) throws OperationManagementException;

    Activity getDeviceLocation(List<String> deviceIDs) throws OperationManagementException;

    Activity removePassword(List<String> deviceIDs) throws OperationManagementException;

    Activity configureCamera(CameraBeanWrapper cameraBeanWrapper) throws OperationManagementException;

    Activity getDeviceInformation(List<String> deviceIDs) throws OperationManagementException;

    Activity getDeviceLogcat(List<String> deviceIDs) throws OperationManagementException;

    Activity wipeDevice(List<String> deviceIDs) throws OperationManagementException;

    Activity wipeData(WipeDataBeanWrapper wipeDataBeanWrapper) throws OperationManagementException;

    Activity getApplications(List<String> deviceIDs) throws OperationManagementException;

    Activity ringDevice(List<String> deviceIDs) throws OperationManagementException;

    Activity rebootDevice(List<String> deviceIDs) throws OperationManagementException;

    Activity changeLockTask(List<String> deviceIDs) throws OperationManagementException;

    Activity muteDevice(List<String> deviceIDs) throws OperationManagementException;

    Activity installApplication(ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper)
            throws OperationManagementException;

    Activity updateApplication(ApplicationUpdateBeanWrapper applicationUpdateBeanWrapper)
            throws OperationManagementException;

    Activity uninstallApplication(ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper)
            throws OperationManagementException;

    Activity blacklistApplications(@Valid BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper)
            throws OperationManagementException;

    Activity upgradeFirmware(UpgradeFirmwareBeanWrapper upgradeFirmwareBeanWrapper)
            throws OperationManagementException;

    Activity configureVPN(VpnBeanWrapper vpnConfiguration) throws OperationManagementException;

    Activity sendNotification(NotificationBeanWrapper notificationBeanWrapper)
            throws OperationManagementException;

    Activity configureWifi(WifiBeanWrapper wifiBeanWrapper) throws OperationManagementException;

    Activity encryptStorage(EncryptionBeanWrapper encryptionBeanWrapper) throws OperationManagementException;

    Activity changeLockCode(LockCodeBeanWrapper lockCodeBeanWrapper) throws OperationManagementException;

    Activity setPasswordPolicy(PasswordPolicyBeanWrapper passwordPolicyBeanWrapper) throws OperationManagementException;

    Activity setWebClip(WebClipBeanWrapper webClipBeanWrapper) throws OperationManagementException;

    Activity setRecommendedGlobalProxy(GlobalProxyBeanWrapper globalProxyBeanWrapper) throws OperationManagementException;

    Activity configureDisplayMessage(DisplayMessageBeanWrapper displayMessageBeanWrapper) throws OperationManagementException;

    //DeviceManagementAPIImpl
    Message updateApplicationList(String id, List<AndroidApplication> androidApplications) throws ApplicationManagementException;

    List<? extends Operation> getPendingOperations(String id, List<? extends Operation> resultOperations, boolean disableGoogleApps)
            throws DeviceManagementException, InvalidDeviceException;

    Response enrollDevice(AndroidDevice androidDevice) throws DeviceManagementException;

    boolean modifyEnrollment(String id, AndroidDevice androidDevice) throws DeviceManagementException;

    boolean disEnrollDevice(String id) throws DeviceManagementException;

    //EventReceiverAPI
    Device publishEvents(EventBeanWrapper eventBeanWrapper) throws DeviceManagementException;

    Response retrieveAlerts(String deviceId,
                        long from,
                        long to,
                        String type,
                        String ifModifiedSince);
}
