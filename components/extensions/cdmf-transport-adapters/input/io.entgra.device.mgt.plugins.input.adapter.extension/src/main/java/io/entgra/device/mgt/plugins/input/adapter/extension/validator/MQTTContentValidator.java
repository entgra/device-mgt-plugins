/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package io.entgra.device.mgt.plugins.input.adapter.extension.validator;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import io.entgra.device.mgt.plugins.input.adapter.extension.ContentInfo;
import io.entgra.device.mgt.plugins.input.adapter.extension.ContentValidator;

import java.util.Map;

public class MQTTContentValidator implements ContentValidator {
    private static final String JSON_ARRAY_START_CHAR = "[";
    private static final Log log = LogFactory.getLog(MQTTContentValidator.class);
    private static final String CDMF_MQTT_CONTENT_VALIDATOR = "deviceid-topic-content-validator";
    private static final String DEVICE_ID_JSON_PATH = "event.metaData.deviceId";
    private static final String DEVICE_TYPE_JSON_PATH = "event.metaData.deviceId";
    private static final String TOPIC = "topic";
    private static final String DEVICE_ID_INDEX = "deviceIdIndex";
    private static final int DEVICE_ID_TOPIC_HIERARCHY_INDEX = 2;

    @Override
    public String getType() {
        return CDMF_MQTT_CONTENT_VALIDATOR;
    }

    @Override
    public ContentInfo validate(Object msgPayload, Map<String, Object> dynamicParams) {
        String topic = (String) dynamicParams.get(TOPIC);
        if (!dynamicParams.containsKey(DEVICE_ID_INDEX)) {
            log.error("device id not found in topic ");
            return null;
        }
        int deviceIdIndex = (int)dynamicParams.get(DEVICE_ID_INDEX);
        String topics[] = topic.split("/");
        String deviceIdFromTopic = topics[deviceIdIndex];
        boolean status;
        String message = (String) msgPayload;
        if (message.startsWith(JSON_ARRAY_START_CHAR)) {
            status = processMultipleEvents(message, deviceIdFromTopic, DEVICE_ID_JSON_PATH);
        } else {
            status = processSingleEvent(message, deviceIdFromTopic, DEVICE_ID_JSON_PATH);
        }
        return new ContentInfo(status, msgPayload);
    }

    private boolean processSingleEvent(String msg, String deviceIdFromTopic, String deviceIdJsonPath) {
        Object res = JsonPath.read(msg, deviceIdJsonPath);
        String deviceIdFromContent = (res != null) ? res.toString() : "";
        if (deviceIdFromContent.equals(deviceIdFromTopic)) {
            return true;
        }
        return false;
    }

    private boolean processMultipleEvents(String msg, String deviceIdFromTopic, String deviceIdJsonPath) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(msg);
            boolean status = false;
            for (int i = 0; i < jsonArray.size(); i++) {
                status = processSingleEvent(jsonArray.get(i).toString(), deviceIdFromTopic, deviceIdJsonPath);
                if (!status) {
                    return false;
                }
            }
            return status;
        } catch (ParseException e) {
            log.error("Invalid input " + msg, e);
            return false;
        }
    }
}
