/*
 *   Copyright (c) 2018, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.carbon.device.mgt.mobile.windows.api.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.mobile.windows.api.common.PluginConstants;
import org.wso2.carbon.device.mgt.mobile.windows.api.common.exceptions.SyncmlOperationException;
import org.wso2.carbon.device.mgt.mobile.windows.api.common.exceptions.ProfileConfigurationException;
import org.wso2.carbon.device.mgt.mobile.windows.api.operations.*;
import org.wso2.carbon.device.mgt.mobile.windows.api.operations.util.Constants;
import org.wso2.carbon.device.mgt.mobile.windows.api.operations.util.OperationCode;
import org.wso2.carbon.device.mgt.mobile.windows.api.operations.util.SyncmlGenerator;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "EnterpriseApplication", description = "Information related to Enterprise Application.")
public class EnterpriseApplication extends WindowsOperation {

    private static Log log = LogFactory.getLog(EnterpriseApplication.class);

    @ApiModelProperty(name = "packageURL", value = "Package URL.")
    private HostedAppxApplication hostedAppxApplication;
    @ApiModelProperty(name = "packageFamilyName", value = "Package family name.")
    private HostedMSIApplication hostedMSIApplication;

    public HostedAppxApplication getHostedAppxApplication() {
        return hostedAppxApplication;
    }

    public void setHostedAppxApplication(HostedAppxApplication hostedAppxApplication) {
        this.hostedAppxApplication = hostedAppxApplication;
    }

    public HostedMSIApplication getHostedMSIApplication() {
        return hostedMSIApplication;
    }

    public void setHostedMSIApplication(HostedMSIApplication hostedMSIApplication) {
        this.hostedMSIApplication = hostedMSIApplication;
    }

    public void validateRequest() throws ProfileConfigurationException {
        if (getHostedAppxApplication() != null) {
            if (isNullOrEmpty(getHostedAppxApplication().getpackageUri())) {
                throw new ProfileConfigurationException(
                        "Appx package URL is not found for enterprise app installation");
            }
            if (isNullOrEmpty(getHostedAppxApplication().getpackageFamilyName())) {
                throw new ProfileConfigurationException(
                        "Appx package Family Name is not found for enterprise app installation");
            }
        } else if (getHostedMSIApplication() != null) {
            if (isNullOrEmpty(getHostedMSIApplication().getProductId())) {
                throw new ProfileConfigurationException(
                        "MSI product ID is not found for enterprise app installation");
            }
            if (isNullOrEmpty(getHostedMSIApplication().getContentUrl())) {
                throw new ProfileConfigurationException(
                        "MSI product content download URL is not found for enterprise app installation");
            }
            if (isNullOrEmpty(getHostedMSIApplication().getFileHash())) {
                throw new ProfileConfigurationException(
                        "MSI product file hash is not found for enterprise app installation");
            }
        } else {
            throw new ProfileConfigurationException("MSI or APPX payload is not found for enterprise app installation");
        }
    }

    @Override
    public List<Object> createOperationContent(Operation operation) throws WindowsOperationException {
        List<Object> enterpriseApplicationContent = new ArrayList<>();
        if (getHostedAppxApplication() != null) {
            enterpriseApplicationContent.addAll(createAddTag(operation.getId(),
                    OperationCode.Command.INSTALL_ENTERPRISE_APPX_APPLICATION.getCode(),
                    PluginConstants.ApplicationInstallProperties.PACKAGE_FAMILY_NAME,
                    getHostedAppxApplication().getpackageFamilyName(),
                    PluginConstants.ApplicationInstallProperties.TYPE_APPX));
            enterpriseApplicationContent.addAll(createExecuteTag(operation.getId(),
                    OperationCode.Command.INSTALL_ENTERPRISE_APPX_APPLICATION.getCode(),
                    PluginConstants.ApplicationInstallProperties.PACKAGE_FAMILY_NAME,
                    getHostedAppxApplication().getpackageFamilyName(),
                    PluginConstants.ApplicationInstallProperties.TYPE_APPX));
        } else if (getHostedMSIApplication() != null) {
            enterpriseApplicationContent.addAll(createAddTag(operation.getId(),
                    OperationCode.Command.INSTALL_ENTERPRISE_MSI_APPLICATION.getCode(),
                    PluginConstants.ApplicationInstallProperties.PRODUCT_ID, getHostedMSIApplication().getProductId(),
                    PluginConstants.ApplicationInstallProperties.TYPE_MSI));
            enterpriseApplicationContent.addAll(createExecuteTag(operation.getId(),
                    OperationCode.Command.INSTALL_ENTERPRISE_MSI_APPLICATION.getCode(),
                    PluginConstants.ApplicationInstallProperties.PRODUCT_ID, getHostedMSIApplication().getProductId(),
                    PluginConstants.ApplicationInstallProperties.TYPE_MSI));
        }
        return enterpriseApplicationContent;
    }

    private List<AddTag> createAddTag(int operationId, String operationCode, String replaceOld, String replaceNew,
            String appType) {
        List<AddTag> addTagList = new ArrayList<>();
        List<ItemTag> itemTagList = new ArrayList<>();
        AddTag addTag = new AddTag();
        ItemTag itemTag = new ItemTag();
        TargetTag targetTag = new TargetTag();
        String locUri = operationCode.replace(replaceOld, replaceNew);
        if (PluginConstants.ApplicationInstallProperties.TYPE_APPX.equals(appType)) {
            locUri = locUri.substring(0, locUri.lastIndexOf(Constants.FORWARD_SLASH));
        }
        targetTag.setLocURI(locUri);
        itemTag.setTarget(targetTag);
        itemTagList.add(itemTag);
        addTag.setCommandId(operationId);
        addTag.setItems(itemTagList);
        addTagList.add(addTag);
        if (PluginConstants.ApplicationInstallProperties.TYPE_APPX.equals(appType)) {
            if (!isNullOrEmpty(getHostedAppxApplication().getCertificateHash()) && !isNullOrEmpty(
                    getHostedAppxApplication().getEncodedCertificate())) {
                List<ItemTag> certItemTagList = new ArrayList<>();
                AddTag certAddTag = new AddTag();
                ItemTag certItemTag = new ItemTag();
                MetaTag certMetaTag = new MetaTag();
                TargetTag certTargetTag = new TargetTag();
                certTargetTag.setLocURI(OperationCode.Command.INSTALL_ENTERPRISE_APPX_CERTIFICATE.getCode()
                        .replace(PluginConstants.ApplicationInstallProperties.CERT_HASH,
                                getHostedAppxApplication().getCertificateHash()));
                certMetaTag.setFormat(Constants.META_FORMAT_B64);
                certItemTag.setTarget(certTargetTag);
                certItemTag.setMeta(certMetaTag);
                certItemTag.setData(getHostedAppxApplication().getEncodedCertificate());
                certItemTagList.add(certItemTag);
                certAddTag.setCommandId(operationId);
                certAddTag.setItems(certItemTagList);
                addTagList.add(certAddTag);
            }
        }
        return addTagList;
    }

    private List<ExecuteTag> createExecuteTag(int operationId, String operationCode, String replaceOld,
            String replaceNew, String appType) throws WindowsOperationException {
        List<ExecuteTag> executeTagList = new ArrayList<>();
        List<ItemTag> itemTagList = new ArrayList<>();
        ExecuteTag executeTag = new ExecuteTag();
        ItemTag itemTag = new ItemTag();
        MetaTag metaTag = new MetaTag();
        TargetTag targetTag = new TargetTag();
        targetTag.setLocURI(operationCode.replace(replaceOld, replaceNew));
        Document document;
        Element dependencyElement;
        metaTag.setFormat(Constants.META_FORMAT_XML);
        try {
            if (PluginConstants.ApplicationInstallProperties.TYPE_APPX.equals(appType)) {
                document = SyncmlGenerator.generateDocument();
                Element applicationElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.APPLICATION);
                applicationElement.setAttribute(PluginConstants.ApplicationInstallProperties.PACKAGE_URI,
                        getHostedAppxApplication().getpackageUri());
                if (!isNullOrEmpty(getHostedAppxApplication().getDependencyPackageUri())) {
                    Element dependenciesElement = document
                            .createElement(PluginConstants.ApplicationInstallProperties.DEPENDENCIES);
                    for (String dependency : getHostedAppxApplication().getDependencyPackageUri()) {
                        dependencyElement = document
                                .createElement(PluginConstants.ApplicationInstallProperties.DEPENDENCY);
                        dependencyElement
                                .setAttribute(PluginConstants.ApplicationInstallProperties.PACKAGE_URI, dependency);
                        dependenciesElement.appendChild(dependencyElement);
                    }
                    applicationElement.appendChild(dependenciesElement);
                }
                itemTag.setElementData(applicationElement);
            } else if (PluginConstants.ApplicationInstallProperties.TYPE_MSI.equals(appType)) {
                metaTag.setType(Constants.META_TYPE_TEXT_PLAIN);
                document = SyncmlGenerator.generateDocument();
                Element contentURLElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.CONTENT_URL);
                contentURLElement.setTextContent(getHostedMSIApplication().getContentUrl());
                Element contentURLListElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.CONTENT_URL_LIST);
                contentURLListElement.appendChild(contentURLElement);
                Element downloadElement = document.createElement(PluginConstants.ApplicationInstallProperties.DOWNLOAD);
                downloadElement.appendChild(contentURLListElement);
                Element fileHashElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.FILE_HASH);
                fileHashElement.setTextContent(getHostedMSIApplication().getFileHash());
                Element validationElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.VALIDATION);
                validationElement.appendChild(fileHashElement);
                Element enforcementElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.ENFORCEMENT);
                Element commandLineElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.COMMAND_LINE);
                commandLineElement.setTextContent(PluginConstants.ApplicationInstallProperties.COMMAND_LINE_VALUE);
                enforcementElement.appendChild(commandLineElement);
                Element timeOutElement = document.createElement(PluginConstants.ApplicationInstallProperties.TIMEOUT);
                timeOutElement.setTextContent(PluginConstants.ApplicationInstallProperties.TIMEOUT_VALUE);
                enforcementElement.appendChild(timeOutElement);
                Element retryCountElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.RETRY_COUNT);
                retryCountElement.setTextContent(PluginConstants.ApplicationInstallProperties.RETRY_COUNT_VALUE);
                enforcementElement.appendChild(retryCountElement);
                Element retryIntervalElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.RETRY_INTERVAL);
                retryIntervalElement.setTextContent(PluginConstants.ApplicationInstallProperties.RETRY_INTERVAL_VALUE);
                enforcementElement.appendChild(retryIntervalElement);
                Element productElement = document.createElement(PluginConstants.ApplicationInstallProperties.PRODUCT);
                productElement.setAttribute(PluginConstants.ApplicationInstallProperties.VERSION,
                        PluginConstants.ApplicationInstallProperties.VERSION_VALUE);
                productElement.appendChild(downloadElement);
                productElement.appendChild(validationElement);
                productElement.appendChild(enforcementElement);
                Element msiInstallJobElement = document
                        .createElement(PluginConstants.ApplicationInstallProperties.MSI_INSTALL_JOB);
                msiInstallJobElement.setAttribute(PluginConstants.ApplicationInstallProperties.ID,
                        PluginConstants.ApplicationInstallProperties.URL_ESCAPED_OPEN_CURLY + getHostedMSIApplication()
                                .getProductId() + PluginConstants.ApplicationInstallProperties.URL_ESCAPED_CLOSE_CURLY);
                msiInstallJobElement.appendChild(productElement);
                itemTag.setElementData(msiInstallJobElement);
            }
        } catch (SyncmlOperationException e) {
            String errorMsg = "Error occurred while generating a document to add as a node to Data element of Execute "
                    + "command which is required to Install " + appType + " application.";
            log.error(errorMsg);
            throw new WindowsOperationException(errorMsg, e);
        }
        itemTag.setTarget(targetTag);
        itemTag.setMeta(metaTag);
        itemTagList.add(itemTag);
        executeTag.setCommandId(operationId);
        executeTag.setItems(itemTagList);
        executeTagList.add(executeTag);
        return executeTagList;
    }

}
