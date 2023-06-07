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

package io.entgra.device.mgt.plugins.virtualfirealarm.api.service.impl.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
/**
 * This stores sensor event data for virtual fire alarm sense.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorRecord {

    @XmlElementWrapper(required = true, name = "stats")
    private Map<Long, Float> stats;

    public Map<Long, Float> getStats() {
        return stats;
    }

    public void setStats(Map<Long, Float> stats) {
        this.stats = stats;
    }

    public SensorRecord(Map<Long, Float> stats) {
        this.stats = stats;
    }
}
