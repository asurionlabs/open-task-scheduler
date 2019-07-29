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
 * The Queueconfig Schema
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "prefix",
    "ackTimeout",
    "ackOnPoll"
})
public class QueueConfig {

    /**
     * The Prefix Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("prefix")
    private String prefix = "";
    /**
     * The Acktimeout Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("ackTimeout")
    private Integer ackTimeout = 0;
    /**
     * The Ackonpoll Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("ackOnPoll")
    private Boolean ackOnPoll = false;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Prefix Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("prefix")
    public String getPrefix() {
        return prefix;
    }

    /**
     * The Prefix Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("prefix")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * The Acktimeout Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("ackTimeout")
    public Integer getAckTimeout() {
        return ackTimeout;
    }

    /**
     * The Acktimeout Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("ackTimeout")
    public void setAckTimeout(Integer ackTimeout) {
        this.ackTimeout = ackTimeout;
    }

    /**
     * The Ackonpoll Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("ackOnPoll")
    public Boolean getAckOnPoll() {
        return ackOnPoll;
    }

    /**
     * The Ackonpoll Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("ackOnPoll")
    public void setAckOnPoll(Boolean ackOnPoll) {
        this.ackOnPoll = ackOnPoll;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
