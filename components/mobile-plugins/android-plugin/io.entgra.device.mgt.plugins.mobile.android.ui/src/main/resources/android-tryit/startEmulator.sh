#!/bin/bash
#
# Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
#
# Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

echo Welcome

OS_SUFFIX="linux"
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS_SUFFIX="macosx"
fi

java\
 -Dsdk.tools.url="https://dl.google.com/android/repository/tools_r25.2.5-$OS_SUFFIX.zip"\
 -Dplatform.tools.url="http://dl.google.com/android/repository/platform-tools_r25.0.3-$OS_SUFFIX.zip"\
 -Dbuild.tools.url="https://dl.google.com/android/repository/build-tools_r25.0.2-$OS_SUFFIX.zip"\
 -Dplatform.url="https://dl.google.com/android/repository/platform-23_r03.zip"\
 -Dsys.img.url="https://dl.google.com/android/repository/sys-img/android/x86-23_r09.zip"\
 -Dhaxm.url="https://dl.google.com/android/repository/extras/intel/haxm-macosx_r6_0_5.zip"\
 -Ddownloaded.build.tool.name="android-7.1.1"\
 -Dbuild.tool.version="25.0.2"\
 -Ddownloaded.platform.name="android-6.0"\
 -Dtarget.version="android-23"\
 -Dos.target="x86"\
 -jar JavaApp.jar

