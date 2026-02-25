/*
 * Copyright (c) 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;


public class TopicMatchTest {

    private ExServer.HookProviderImpl hookProvider;
    private Method isTopicMatchMethod;

    @BeforeClass
    public void setUp() throws Exception {
        hookProvider = new ExServer.HookProviderImpl(null);
        isTopicMatchMethod = ExServer.HookProviderImpl.class.getDeclaredMethod("isTopicMatch", String.class, String.class);
        isTopicMatchMethod.setAccessible(true);
    }

    private boolean isTopicMatch(String scopePattern, String exactTopic) throws Exception {
        return (boolean) isTopicMatchMethod.invoke(hookProvider, scopePattern, exactTopic);
    }

    /**
     * Test Case 1: Exact Matches
     */
    @DataProvider(name = "exactMatchData")
    public Object[][] exactMatchData() {
        return new Object[][] {
                // Valid exact matches
                {"perm:topic:sub:ami:reading:billing", "perm:topic:sub:ami:reading:billing", true},
                {"perm:topic:pub:device:data:sensor", "perm:topic:pub:device:data:sensor", true},
                {"perm:topic:sub:tenant:device:12345", "perm:topic:sub:tenant:device:12345", true},
                {"perm:topic:pub:carbon.super:ami:operation", "perm:topic:pub:carbon.super:ami:operation", true},

                // Invalid exact matches
                {"perm:topic:sub:ami:reading:billing", "perm:topic:sub:ami:reading:power", false},
                {"perm:topic:pub:device:data", "perm:topic:sub:device:data", false},
                {"perm:topic:sub:tenant:device:12345", "perm:topic:sub:tenant:device:67890", false},
                {"perm:topic:pub:carbon.super:ami", "perm:topic:pub:carbon.super:ami:operation", false},
                {"perm:topic:sub:ami:reading", "perm:topic:sub:ami:reading:billing", false}
        };
    }

    @Test(dataProvider = "exactMatchData")
    public void testExactMatches(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Exact match test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }


    /**
     * Test Case 2: Single-level Wildcard Matching (+) - Valid Cases
     */
    @DataProvider(name = "singleLevelWildcardValidData")
    public Object[][] singleLevelWildcardValidData() {
        return new Object[][] {
                // Valid single-level wildcard matches (same length)
                {"perm:topic:sub:ami:reading:+", "perm:topic:sub:ami:reading:billing", true},
                {"perm:topic:pub:+:device:data", "perm:topic:pub:tenant1:device:data", true},
                {"perm:topic:sub:+:ami:+", "perm:topic:sub:tenant:ami:12345", true},
                {"perm:topic:pub:carbon.super:+:+", "perm:topic:pub:carbon.super:ami:operation", true},
                {"perm:topic:sub:+:+:+", "perm:topic:sub:tenant:device:12345", true},
                {"perm:topic:pub:+:device:+:sensor", "perm:topic:pub:tenant:device:abc:sensor", true},
                {"+:topic:sub:ami:reading:billing", "perm:topic:sub:ami:reading:billing", true},
                {"perm:topic:+:+:+:+", "perm:topic:sub:tenant:device:12345", true}
        };
    }

    @Test(dataProvider = "singleLevelWildcardValidData")
    public void testSingleLevelWildcardValid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Single-level wildcard valid test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    //

    /**
     * Test Case 3: Single-level Wildcard Matching (+) - Invalid Cases
     */
    @DataProvider(name = "singleLevelWildcardInvalidData")
    public Object[][] singleLevelWildcardInvalidData() {
        return new Object[][] {
                // Invalid - different lengths
                {"perm:topic:sub:ami:reading:+", "perm:topic:sub:ami:reading:billing:extra", false},
                {"perm:topic:pub:+:device:data", "perm:topic:pub:tenant1", false},
                {"perm:topic:sub:+:ami:+", "perm:topic:sub:tenant:ami", false},
                {"perm:topic:pub:+:+", "perm:topic:pub:carbon.super:ami:operation", false},
                {"perm:topic:sub:+:+:+:+", "perm:topic:sub:tenant:device", false},

                // Invalid - wrong content
                {"perm:topic:sub:ami:reading:+", "perm:topic:pub:ami:reading:billing", false},
                {"perm:topic:pub:tenant1:device:+", "perm:topic:pub:tenant2:device:data", false},
                {"perm:topic:sub:+:device:data", "perm:topic:sub:tenant:ami:data", false}
        };
    }

    @Test(dataProvider = "singleLevelWildcardInvalidData")
    public void testSingleLevelWildcardInvalid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Single-level wildcard invalid test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 4: Multi-level Wildcard in Scope Pattern (#) - Valid Cases
     */
    @DataProvider(name = "multiLevelWildcardInScopeValidData")
    public Object[][] multiLevelWildcardInScopeValidData() {
        return new Object[][] {
                // Valid multi-level wildcard matches in scope
                {"perm:topic:sub:ami:reading:#", "perm:topic:sub:ami:reading:billing:node:sensor", true},
                {"perm:topic:pub:carbon.super:#", "perm:topic:pub:carbon.super:ami:12345:operation", true},
                {"perm:topic:sub:tenant:#", "perm:topic:sub:tenant:device:abc:data", true},
                {"perm:topic:pub:#", "perm:topic:pub:any:deep:nested:structure", true},
                {"perm:topic:sub:ami:reading:#", "perm:topic:sub:ami:reading", true},
                {"perm:topic:pub:+:ami:#", "perm:topic:pub:tenant:ami:operation:data", true},
                {"perm:topic:sub:+:device:#", "perm:topic:sub:tenant:device:12345", true},
                {"+:topic:sub:#", "any:topic:sub:deep:structure", true}
        };
    }

    @Test(dataProvider = "multiLevelWildcardInScopeValidData")
    public void testMultiLevelWildcardInScopeValid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Multi-level wildcard in scope valid test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     *Test Case 5: Multi-level Wildcard in Scope Pattern (#) - Invalid Cases
     */
    @DataProvider(name = "multiLevelWildcardInScopeInvalidData")
    public Object[][] multiLevelWildcardInScopeInvalidData() {
        return new Object[][] {
                // Invalid multi-level wildcard matches in scope
                {"perm:topic:sub:ami:reading:#", "perm:topic:sub:ami:power:billing", false},
                {"perm:topic:pub:carbon.super:#", "perm:topic:sub:carbon.super:ami:12345", false},
                {"perm:topic:sub:tenant:#", "perm:topic:pub:tenant:device:abc", false},
                {"perm:topic:pub:tenant:ami:#", "perm:topic:pub:tenant:device:12345", false},
                {"perm:topic:sub:+:device:#", "perm:topic:sub:tenant:ami:12345", false},
                {"perm:topic:pub:tenant:#", "perm:topic:pub:other:device:data", false},
                {"+:topic:sub:#", "any:topic:pub:structure", false}
        };
    }

    @Test(dataProvider = "multiLevelWildcardInScopeInvalidData")
    public void testMultiLevelWildcardInScopeInvalid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Multi-level wildcard in scope invalid test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 6: Multi-level Wildcard in Topic (Subscription Request) - Valid Cases
     */
    @DataProvider(name = "multiLevelWildcardInTopicValidData")
    public Object[][] multiLevelWildcardInTopicValidData() {
        return new Object[][] {
                // Valid - only when scope also ends with # (# to # matching)
                {"perm:topic:sub:ami:reading:#", "perm:topic:sub:ami:reading:#", true},
                {"perm:topic:pub:+:device:#", "perm:topic:pub:+:device:#", true},
                {"perm:topic:sub:+:ami:#", "perm:topic:sub:tenant:ami:#", true},
                {"perm:topic:pub:carbon.super:#", "perm:topic:pub:carbon.super:#", true},

                // Valid - scope ends with # and topic prefix matches with wildcards
                {"perm:topic:sub:+:+:#", "perm:topic:sub:tenant:device:#", true},
                {"perm:topic:pub:+:ami:#", "perm:topic:pub:carbon.super:ami:#", true},
                {"perm:topic:sub:+:+:+:#", "perm:topic:sub:tenant:device:sensor:#", true}
        };
    }

    @Test(dataProvider = "multiLevelWildcardInTopicValidData")
    public void testMultiLevelWildcardInTopicValid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Multi-level wildcard in topic valid test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 7: Multi-level Wildcard in Topic (Subscription Request) - Invalid Cases
     */
    @DataProvider(name = "multiLevelWildcardInTopicInvalidData")
    public Object[][] multiLevelWildcardInTopicInvalidData() {
        return new Object[][] {
                // Invalid - scope does not end with # but topic requests #
                {"perm:topic:sub:ami:reading:+:+:+", "perm:topic:sub:ami:reading:#", false},
                {"perm:topic:pub:+:+:+:+", "perm:topic:pub:tenant:#", false},
                {"perm:topic:sub:carbon.super:+:+", "perm:topic:sub:carbon.super:#", false},
                {"perm:topic:sub:+:ami:+:+", "perm:topic:sub:tenant:ami:#", false},
                {"perm:topic:pub:+:+:+", "perm:topic:pub:tenant:#", false},

                // Invalid - exact scope cannot allow wildcard extension
                {"perm:topic:sub:ami:reading:billing", "perm:topic:sub:ami:reading:#", false},
                {"perm:topic:pub:carbon.super:ami:12345", "perm:topic:pub:carbon.super:ami:#", false},
                {"perm:topic:sub:tenant:device:abc", "perm:topic:sub:tenant:device:#", false},

                // Invalid - prefix mismatch even when both end with #
                {"perm:topic:sub:ami:reading:#", "perm:topic:sub:device:#", false},
                {"perm:topic:pub:tenant1:#", "perm:topic:pub:tenant2:#", false},
                {"perm:topic:sub:+:device:#", "perm:topic:sub:tenant:ami:#", false}
        };
    }

    @Test(dataProvider = "multiLevelWildcardInTopicInvalidData")
    public void testMultiLevelWildcardInTopicInvalid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Multi-level wildcard in topic invalid test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 8: Backward Compatibility - Scope with Single Wildcard should NOT accept Topic with Multi-level Wildcard
     */
    @DataProvider(name = "backwardCompatibilityInvalidData")
    public Object[][] backwardCompatibilityInvalidData() {
        return new Object[][] {
                // Scope with single-level wildcard should NOT accept topic with multi-level wildcard
                {"perm:topic:sub:ami:reading:+", "perm:topic:sub:ami:#", false},
                {"perm:topic:pub:+:device:data", "perm:topic:pub:#", false},
                {"perm:topic:sub:tenant:+:+", "perm:topic:sub:tenant:#", false},
                {"perm:topic:pub:carbon.super:+", "perm:topic:pub:carbon.super:#", false},
                {"perm:topic:sub:+:ami:+:operation", "perm:topic:sub:tenant:ami:#", false},
                {"perm:topic:pub:+:+:+:sensor", "perm:topic:pub:tenant:device:#", false},

                // Single-level exact scope should NOT accept any wildcard topic
                {"perm:topic:sub:ami:reading:billing", "perm:topic:sub:ami:reading:+", false},
                {"perm:topic:pub:carbon.super:ami:12345", "perm:topic:pub:carbon.super:ami:+", false},
                {"perm:topic:sub:tenant:device:abc", "perm:topic:sub:tenant:device:+", false}
        };
    }

    @Test(dataProvider = "backwardCompatibilityInvalidData")
    public void testBackwardCompatibilityInvalid(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Backward compatibility test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 9: Complex Mixed Scenarios
     */
    @DataProvider(name = "complexMixedData")
    public Object[][] complexMixedData() {
        return new Object[][] {
                // Valid complex combinations
                {"perm:topic:sub:+:reading:#", "perm:topic:sub:ami:reading:billing:node", true},
                {"perm:topic:pub:+:device:#", "perm:topic:pub:tenant:device:12345:sensor:data", true},
                {"+:topic:sub:+:ami:#", "perm:topic:sub:tenant:ami:operation:status", true},
                {"perm:topic:+:carbon.super:+:#", "perm:topic:sub:carbon.super:ami:12345:operation", true},

                // Invalid complex combinations
                {"perm:topic:sub:ami:#", "perm:topic:pub:ami:reading", false},
                {"+:topic:sub:+:ami:#", "perm:topic:sub:tenant:device:data", false},
                {"perm:topic:pub:+:device:#", "perm:topic:sub:tenant:device:12345", false},
                {"perm:topic:sub:tenant:+:#", "perm:topic:sub:other:device:data", false}
        };
    }

    @Test(dataProvider = "complexMixedData")
    public void testComplexMixedScenarios(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("Complex mixed scenario test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 10: Real-world IoT Device Scenarios
     */
    @DataProvider(name = "iotDeviceData")
    public Object[][] iotDeviceData() {
        return new Object[][] {
                // Valid IoT patterns
                {"perm:topic:pub:carbon.super:ami:12345", "perm:topic:pub:carbon.super:ami:12345", true},
                {"perm:topic:sub:+:ami:+", "perm:topic:sub:carbon.super:ami:12345", true},
                {"perm:topic:pub:carbon.super:+:+", "perm:topic:pub:carbon.super:ami:12345", true},
                {"perm:topic:sub:carbon.super:ami:#", "perm:topic:sub:carbon.super:ami:12345:operation", true},
                {"perm:topic:pub:+:+:+:operation", "perm:topic:pub:carbon.super:ami:12345:operation", true},
                {"perm:topic:sub:+:device:#", "perm:topic:sub:tenant1:device:sensor:temperature", true},

                // Invalid IoT patterns
                {"perm:topic:sub:carbon.super:+:+:operation", "perm:topic:sub:carbon.super:ami:#", false},
                {"perm:topic:pub:carbon.super:ami:12345", "perm:topic:sub:carbon.super:ami:12345", false},
                {"perm:topic:sub:tenant1:device:+", "perm:topic:sub:tenant2:device:sensor", false},
                {"perm:topic:pub:+:ami:12345", "perm:topic:pub:tenant:device:12345", false}
        };
    }

    @Test(dataProvider = "iotDeviceData")
    public void testIoTDeviceScenarios(String scopePattern, String exactTopic, boolean expected) throws Exception {
        boolean result = isTopicMatch(scopePattern, exactTopic);
        Assert.assertEquals(result, expected,
                String.format("IoT device scenario test failed for scope: '%s' and topic: '%s'", scopePattern, exactTopic));
    }

    /**
     * Test Case 11: Performance Test with Large Topics
     */
    @Test
    public void testLargeTopicStrings() throws Exception {
        StringBuilder largeScopeBuilder = new StringBuilder("perm:topic:sub");
        StringBuilder largeTopicBuilder = new StringBuilder("perm:topic:sub");

        // Create large topic strings with 100 additional levels
        for (int i = 0; i < 100; i++) {
            largeScopeBuilder.append(":+");
            largeTopicBuilder.append(":level").append(i);
        }

        String largeScope = largeScopeBuilder.toString();
        String largeTopic = largeTopicBuilder.toString();

        boolean result = isTopicMatch(largeScope, largeTopic);
        Assert.assertTrue(result, "Large topic string matching should work");

        // Test with multi-level wildcard
        String largeScopeWithHash = "perm:topic:sub:#";
        result = isTopicMatch(largeScopeWithHash, largeTopic);
        Assert.assertTrue(result, "Large topic string with multi-level wildcard should work");
    }


    /**
     * Test Case 12: Null and Invalid Input Handling
     */
    @Test(expectedExceptions = {Exception.class})
    public void testNullScopeInput() throws Exception {
        isTopicMatch(null, "perm:topic:sub:ami");
    }

    @Test(expectedExceptions = {Exception.class})
    public void testNullTopicInput() throws Exception {
        isTopicMatch("perm:topic:sub:ami", null);
    }

    @Test(expectedExceptions = {Exception.class})
    public void testBothNullInputs() throws Exception {
        isTopicMatch(null, null);
    }
}