/*
 * Copyright (c) 2026, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package io.entgra.device.mgt.plugins.emqx.exhook;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Loads a dedicated MQTT username/password pair used by the exhook to recognize a
 * trusted internal node (e.g. AMI, or any other device type sharing this exhook),
 * bypassing OAuth2 introspection and scope based ACL checks for that exact identity.
 */
public class TrustedNodeCredentialsConfig {

    private static final Log logger = LogFactory.getLog(TrustedNodeCredentialsConfig.class);

    private static final String CONFIG_FILE_NAME = "trusted-node-credentials.xml";
    private static final String CONFIG_PATH_OVERRIDE_PROPERTY = "trusted.node.credentials.config.path";
    private static final String CARBON_CONFIG_DIR_PROPERTY = "carbon.config.dir.path";
    private static final String CARBON_HOME_PROPERTY = "carbon.home";
    private static final String USERNAME_ELEMENT = "Username";
    private static final String PASSWORD_ELEMENT = "Password";

    private static final TrustedNodeCredentialsConfig instance = new TrustedNodeCredentialsConfig();

    private final String username;
    private final String password;

    private TrustedNodeCredentialsConfig() {
        String configuredUsername = null;
        String configuredPassword = null;
        File configFile = resolveConfigFile();
        if (configFile.isFile()) {
            try {
                String[] credentials = parseCredentials(configFile);
                configuredUsername = credentials[0];
                configuredPassword = credentials[1];
                if (StringUtils.isEmpty(configuredUsername) || StringUtils.isEmpty(configuredPassword)) {
                    logger.warn("Trusted node credentials config at " + configFile.getAbsolutePath() +
                            " is missing a Username or Password value - trusted node bypass disabled");
                    configuredUsername = null;
                    configuredPassword = null;
                }
            } catch (Exception e) {
                logger.error("Error while parsing trusted node credentials config at " +
                        configFile.getAbsolutePath() + " - trusted node bypass disabled", e);
            }
        } else {
            logger.warn("Trusted node credentials config file not found at " + configFile.getAbsolutePath() +
                    " - trusted node bypass disabled");
        }
        this.username = configuredUsername;
        this.password = configuredPassword;
    }

    /**
     * Test-only constructor for exercising {@link #isTrustedNode(String, String)} without
     * going through file resolution/parsing.
     */
    TrustedNodeCredentialsConfig(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static TrustedNodeCredentialsConfig getInstance() {
        return instance;
    }

    /**
     * @return true if a dedicated trusted node identity is configured and both the given
     * username and password match it exactly.
     */
    public boolean isTrustedNode(String candidateUsername, String candidatePassword) {
        return StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password) &&
                username.equals(candidateUsername) && password.equals(candidatePassword);
    }

    private static File resolveConfigFile() {
        String configDirPath = System.getProperty(CONFIG_PATH_OVERRIDE_PROPERTY);
        if (StringUtils.isNotEmpty(configDirPath)) {
            File override = new File(configDirPath);
            return override.isDirectory() ? new File(override, CONFIG_FILE_NAME) : override;
        }

        configDirPath = System.getProperty(CARBON_CONFIG_DIR_PROPERTY);
        if (StringUtils.isEmpty(configDirPath)) {
            String carbonHome = System.getProperty(CARBON_HOME_PROPERTY);
            configDirPath = StringUtils.isNotEmpty(carbonHome) ?
                    carbonHome + File.separator + "repository" + File.separator + "conf" :
                    "conf";
        }
        return new File(configDirPath, CONFIG_FILE_NAME);
    }

    /**
     * Parses the trusted node credentials XML file, returning {username, password}.
     * Package-private so it can be unit tested directly against a temp file.
     */
    static String[] parseCredentials(File configFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(configFile);
        Element root = document.getDocumentElement();

        String parsedUsername = getElementText(root, USERNAME_ELEMENT);
        String parsedPassword = getElementText(root, PASSWORD_ELEMENT);
        return new String[]{parsedUsername, parsedPassword};
    }

    private static String getElementText(Element root, String tagName) {
        NodeList nodes = root.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        return nodes.item(0).getTextContent().trim();
    }
}
