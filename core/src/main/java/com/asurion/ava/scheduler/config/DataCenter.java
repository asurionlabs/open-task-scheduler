/**
 Copyright (C) 2018-2019  Asurion, LLC

 Open Task Scheduler is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Open Task Scheduler is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Open Task Scheduler.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.asurion.ava.scheduler.config;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * The Datacenter Schema
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "region",
    "localDC",
    "rack",
    "host",
    "port"
})
public class DataCenter {

    /**
     * The Region Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("region")
    private String region = "";
    /**
     * The Localdc Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("localDC")
    private String localDC = "";
    /**
     * The Rack Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("rack")
    private String rack = "";
    
    /**
     * The Host Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("host")
    private String host = "";
    
    /**
     * The Port Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("port")
    private Integer port = null;
    
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Region Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    /**
     * The Region Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("region")
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The Localdc Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("localDC")
    public String getLocalDC() {
        return localDC;
    }

    /**
     * The Localdc Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("localDC")
    public void setLocalDC(String localDC) {
        this.localDC = localDC;
    }

    /**
     * The Rack Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("rack")
    public String getRack() {
        return rack;
    }

    /**
     * The Rack Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("rack")
    public void setRack(String rack) {
        this.rack = rack;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    @JsonProperty("host")
    public String getHost() {
        return host;
    }
    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }
    
    @JsonProperty("port")
    public Integer getPort() {
        return port;
    }
    @JsonProperty("port")
    public void setPort(Integer port) {
        this.port = port;
    }

}
