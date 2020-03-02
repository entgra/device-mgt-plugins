/*
 * Copyright (c) 2020, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.mobile.android.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.android.common.config.datasource.AndroidDBConfigurations;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class MobileDeviceConfigFactory {

    private static final Log log = LogFactory.getLog(MobileDeviceConfigFactory.class);

    public static final String ANDROID_DB_CONFIG_PATH = CarbonUtils.getCarbonConfigDirPath() + File.separator +
            "/android-dbconfig.xml";
    public static AndroidDBConfigurations androidDBConfigurations;

    public static AndroidDBConfigurations getDataSourceConfigurations() {
        return androidDBConfigurations;
    }

    public static synchronized void init() throws DeviceManagementException {
        try {
            File mobileDeviceMgtConfig = new File(ANDROID_DB_CONFIG_PATH);
            Document doc = convertToDocuments(mobileDeviceMgtConfig);
            JAXBContext androidDeviceMgtContext = JAXBContext.newInstance(AndroidDBConfigurations.class);
            Unmarshaller unmarshaller = androidDeviceMgtContext.createUnmarshaller();
            androidDBConfigurations = (AndroidDBConfigurations) unmarshaller.unmarshal(doc);
        } catch (Exception e) {
            String msg = "Error occurred while initializing Mobile Device Management config";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
    }

    public static Document convertToDocuments(File file) throws DeviceManagementException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            return docBuilder.parse(file);
        } catch (Exception e) {
            String msg = "Error occurred while parsing file, while converting " +
                    "to a org.w3c.dom.Document : " + e.getMessage();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
    }

}
