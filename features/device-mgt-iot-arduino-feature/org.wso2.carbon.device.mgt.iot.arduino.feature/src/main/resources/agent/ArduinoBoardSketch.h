/**
  * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
**/

#ifndef ArduinoWifiAgent_H
#define ArduinoWifiAgent_H

#include "Arduino.h"

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10

#define WLAN_SSID       "SSID"           // cannot be longer than 32 characters!
#define WLAN_PASS       "password"

#define WLAN_SECURITY   WLAN_SEC_WPA
// Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS  3000

#define DEVICE_OWNER "${DEVICE_OWNER}"          
#define DEVICE_ID "${DEVICE_ID}"              
#define DEVICE_TOKEN "${DEVICE_TOKEN}"
#define REFRESH_DEVICE_TOKEN "${REFRESH_DEVICE_TOKEN}"


#define SERVICE_PORT 9763
#define SERVICE_EPOINT "/arduino/controller/"

#define POLL_INTERVAL 1000
#define PUSH_INTERVAL 10000
#define DEBUG true
#define CON_DEBUG true

byte server[4] = { 192, 168, 1, 101 };
String host, jsonPayLoad, replyMsg;
String responseMsg, subStrn;
double cpuTemperature =0;
static unsigned long pushTimestamp = 0;
static unsigned long pollTimestamp = 0;


#endif



