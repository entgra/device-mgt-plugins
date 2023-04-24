package org.wso2.carbon.device.mgt.iot.virtualfirealarm.service.impl.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
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
