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

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class HostedAppxApplication {

    @ApiModelProperty(name = "packageUri", value = "Package URL.", required = true)
    private String packageUri;
    @ApiModelProperty(name = "packageFamilyName", value = "Package family name.", required = true)
    private String packageFamilyName;
    @ApiModelProperty(name = "dependencyPackageURLs", value = "Dependency Package URLs.")
    private List<String> dependencyPackageUri;
    @ApiModelProperty(name = "certificateHash", value = "Application signed SHA1 certificate hash.")
    private String certificateHash;
    @ApiModelProperty(name = "encodedCertificate", value = "Application signed Base64 encoded certificate.")
    private String encodedCertificate;

    public String getpackageUri() {
        return packageUri;
    }

    public void setpackageUri (String packageUri) {
        this.packageUri = packageUri;
    }

    public String getpackageFamilyName() {
        return packageFamilyName;
    }

    public void setpackageFamilyName(String packageFamilyName) {
        this.packageFamilyName = packageFamilyName;
    }

    public List<String> getDependencyPackageUri() {
        return dependencyPackageUri;
    }

    public void setDependencyPackageUri(List<String> dependencyPackageUri) {
        this.dependencyPackageUri = dependencyPackageUri;
    }

    public String getCertificateHash() {
        return certificateHash;
    }

    public void setCertificateHash(String certificateHash) {
        this.certificateHash = certificateHash;
    }

    public String getEncodedCertificate() {
        return encodedCertificate;
    }

    public void setEncodedCertificate(String encodedCertificate) {
        this.encodedCertificate = encodedCertificate;
    }
}
