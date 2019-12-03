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
import org.wso2.carbon.device.mgt.mobile.android.common.exception.BadRequestExceptionDup;
import org.wso2.carbon.device.mgt.mobile.android.common.exception.EnterpriseServiceException;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

public interface AndroidService {

    /**
     * Method to retrieve platform configurations
     *
     * @return {@link PlatformConfiguration}
     * @throws {@link DeviceManagementException}
     */
    PlatformConfiguration getPlatformConfig() throws DeviceManagementException;

    /**
     * Method to update configurations
     *
     * @param androidPlatformConfiguration
     * @throws {@link AndroidDeviceMgtPluginException}
     */
    void updateConfiguration(AndroidPlatformConfiguration androidPlatformConfiguration)
            throws AndroidDeviceMgtPluginException;

    /**
     * Method for file transfer operation
     *
     * @param fileTransferBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity fileTransfer(FileTransferBeanWrapper fileTransferBeanWrapper)
            throws AndroidDeviceMgtPluginException, OperationManagementException;

    /**
     * Method for device lock operation
     *
     * @param deviceLockBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity configureDeviceLock(DeviceLockBeanWrapper deviceLockBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for device unlock operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity configureDeviceUnlock(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for get device location operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity getDeviceLocation(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for remove password operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity removePassword(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for configure camera operation
     *
     * @param cameraBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity configureCamera(CameraBeanWrapper cameraBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method to get device information
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity getDeviceInformation(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method to get device logcat
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity getDeviceLogcat(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for wipe device operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity wipeDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for wipe data operation
     *
     * @param wipeDataBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity wipeData(WipeDataBeanWrapper wipeDataBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for get applications operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity getApplications(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for ring device operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity ringDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for reboot device operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity rebootDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for change lock task operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity changeLockTask(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for mute device operation
     *
     * @param deviceIDs
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity muteDevice(List<String> deviceIDs)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for install application operation
     *
     * @param applicationInstallationBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity installApplication(ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for update application operation
     *
     * @param applicationUpdateBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity updateApplication(ApplicationUpdateBeanWrapper applicationUpdateBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for uninstall application operation
     *
     * @param applicationUninstallationBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity uninstallApplication(ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for blacklist application operation
     *
     * @param blacklistApplicationsBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity blacklistApplications(@Valid BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for upgrade firmware operation
     *
     * @param upgradeFirmwareBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity upgradeFirmware(UpgradeFirmwareBeanWrapper upgradeFirmwareBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for configure vpn operation
     *
     * @param vpnConfiguration
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity configureVPN(VpnBeanWrapper vpnConfiguration)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for send notification operation
     *
     * @param notificationBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity sendNotification(NotificationBeanWrapper notificationBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for configure wifi operation
     *
     * @param wifiBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity configureWifi(WifiBeanWrapper wifiBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for encypt storage operation
     *
     * @param encryptionBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity encryptStorage(EncryptionBeanWrapper encryptionBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for change lock code operation
     *
     * @param lockCodeBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity changeLockCode(LockCodeBeanWrapper lockCodeBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for set password policy operation
     *
     * @param passwordPolicyBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity setPasswordPolicy(PasswordPolicyBeanWrapper passwordPolicyBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for set webclip operation
     *
     * @param webClipBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity setWebClip(WebClipBeanWrapper webClipBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for set recommended global proxy operation
     *
     * @param globalProxyBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity setRecommendedGlobalProxy(GlobalProxyBeanWrapper globalProxyBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for send application configuration operation
     *
     * @param applicationRestrictionBeanWrapper
     * @return
     * @throws {@link AndroidDeviceMgtPluginException}
     */
    Response sendApplicationConfiguration(
            ApplicationRestrictionBeanWrapper applicationRestrictionBeanWrapper)
            throws AndroidDeviceMgtPluginException;

    /**
     * method for configure display message operation
     *
     * @param displayMessageBeanWrapper
     * @return {@link Activity}
     * @throws {@link AndroidDeviceMgtPluginException}
     * @throws {@link OperationManagementException}
     */
    Activity configureDisplayMessage(DisplayMessageBeanWrapper displayMessageBeanWrapper)
            throws OperationManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method for update application list
     *
     * @param id
     * @param androidApplications
     * @return {@link Message}
     * @throws {@link ApplicationManagementException}
     */
    Message updateApplicationList(String id, List<AndroidApplication> androidApplications)
            throws ApplicationManagementException;

    /**
     * Method for get pending operations
     *
     * @param id
     * @param resultOperations
     * @param disableGoogleApps
     * @return
     * @throws {@link DeviceManagementException}
     * @throws {@link InvalidDeviceException}
     */
    List<? extends Operation> getPendingOperations
            (String id, List<? extends Operation> resultOperations, boolean disableGoogleApps)
            throws DeviceManagementException, InvalidDeviceException;

    /**
     * Method to enroll device
     *
     * @param androidDevice
     * @return {@link Response}
     * @throws {@link DeviceManagementException}
     */
    Response enrollDevice(AndroidDevice androidDevice) throws DeviceManagementException;

    /**
     * Method to check if a device is enrolled
     *
     * @param id
     * @param deviceIdentifier
     * @return {@link Message}
     * @throws {@link DeviceManagementException}
     */
    Message isEnrolled(String id, DeviceIdentifier deviceIdentifier) throws DeviceManagementException;

    /**
     * Method to modify enrollment
     *
     * @param id
     * @param androidDevice
     * @return
     * @throws {@link DeviceManagementException}
     * @throws {@link AndroidDeviceMgtPluginException}
     */
    boolean modifyEnrollment(String id, AndroidDevice androidDevice)
            throws DeviceManagementException, AndroidDeviceMgtPluginException;

    /**
     * Method to disenroll a device
     *
     * @param id
     * @return
     * @throws {@link DeviceManagementException}
     */
    boolean disEnrollDevice(String id) throws DeviceManagementException;

    /**
     * Method to publish events
     *
     * @param eventBeanWrapper
     * @return {@link Device}
     * @throws {@link DeviceManagementException}
     */
    Device publishEvents(EventBeanWrapper eventBeanWrapper) throws DeviceManagementException;

    /**
     * Method to retrieve alerts
     *
     * @param deviceId
     * @param from
     * @param to
     * @param type
     * @param ifModifiedSince
     * @return {@link Response}
     * @throws {@link AndroidDeviceMgtPluginException}
     */
    Response retrieveAlerts(String deviceId, long from, long to, String type, String ifModifiedSince)
            throws AndroidDeviceMgtPluginException;
}
