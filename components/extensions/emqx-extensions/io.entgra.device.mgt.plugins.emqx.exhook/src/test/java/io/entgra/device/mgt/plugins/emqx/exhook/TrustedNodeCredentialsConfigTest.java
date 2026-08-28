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

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class TrustedNodeCredentialsConfigTest {

    private File tempFile;

    @Test
    public void testParseCredentialsFromValidFile() throws Exception {
        tempFile = writeConfig(
                "<TrustedNodeCredentials><Username>trusted-node</Username><Password>secret</Password></TrustedNodeCredentials>");

        String[] credentials = TrustedNodeCredentialsConfig.parseCredentials(tempFile);

        Assert.assertEquals(credentials[0], "trusted-node");
        Assert.assertEquals(credentials[1], "secret");
    }

    @Test
    public void testParseCredentialsMissingElementsReturnsNull() throws Exception {
        tempFile = writeConfig("<TrustedNodeCredentials></TrustedNodeCredentials>");

        String[] credentials = TrustedNodeCredentialsConfig.parseCredentials(tempFile);

        Assert.assertNull(credentials[0]);
        Assert.assertNull(credentials[1]);
    }

    @Test(expectedExceptions = Exception.class)
    public void testParseCredentialsMalformedXmlThrows() throws Exception {
        tempFile = writeConfig("not xml at all");

        TrustedNodeCredentialsConfig.parseCredentials(tempFile);
    }

    @Test(expectedExceptions = Exception.class)
    public void testParseCredentialsRejectsDoctypeDeclaration() throws Exception {
        tempFile = writeConfig(
                "<!DOCTYPE TrustedNodeCredentials [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" +
                        "<TrustedNodeCredentials><Username>&xxe;</Username><Password>secret</Password></TrustedNodeCredentials>");

        TrustedNodeCredentialsConfig.parseCredentials(tempFile);
    }

    @Test
    public void testIsTrustedNodeMatchesExactCredentials() {
        TrustedNodeCredentialsConfig config = new TrustedNodeCredentialsConfig("trusted-node", "secret");

        Assert.assertTrue(config.isTrustedNode("trusted-node", "secret"));
        Assert.assertFalse(config.isTrustedNode("trusted-node", "wrong-password"));
        Assert.assertFalse(config.isTrustedNode("other-user", "secret"));
        Assert.assertFalse(config.isTrustedNode(null, null));
    }

    @Test
    public void testIsTrustedNodeDisabledWhenUnconfigured() {
        TrustedNodeCredentialsConfig config = new TrustedNodeCredentialsConfig(null, null);

        Assert.assertFalse(config.isTrustedNode("", ""));
        Assert.assertFalse(config.isTrustedNode(null, null));
        Assert.assertFalse(config.isTrustedNode("trusted-node", "secret"));
    }

    @AfterMethod
    public void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    private File writeConfig(String xml) throws Exception {
        File file = File.createTempFile("trusted-node-credentials", ".xml");
        try (Writer writer = new FileWriter(file)) {
            writer.write(xml);
        }
        return file;
    }
}
