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
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * The Root Schema
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "taskConfigs",
    "dynoRedisTaskQueueConfig"
})
public class TaskSchedulerConfig {

    /**
     * The Taskconfigs Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("taskConfigs")
    private List<TaskConfig> taskConfigs = null;
    /**
     * The Dynoredistaskqueueconfig Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dynoRedisTaskQueueConfig")
    private DynoRedisTaskQueueConfig dynoRedisTaskQueueConfig;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Taskconfigs Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("taskConfigs")
    public List<TaskConfig> getTaskConfigs() {
        return taskConfigs;
    }

    /**
     * The Taskconfigs Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("taskConfigs")
    public void setTaskConfigs(List<TaskConfig> taskConfigs) {
        this.taskConfigs = taskConfigs;
    }

    /**
     * The Dynoredistaskqueueconfig Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dynoRedisTaskQueueConfig")
    public DynoRedisTaskQueueConfig getDynoRedisTaskQueueConfig() {
        return dynoRedisTaskQueueConfig;
    }

    /**
     * The Dynoredistaskqueueconfig Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dynoRedisTaskQueueConfig")
    public void setDynoRedisTaskQueueConfig(DynoRedisTaskQueueConfig dynoRedisTaskQueueConfig) {
        this.dynoRedisTaskQueueConfig = dynoRedisTaskQueueConfig;
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
