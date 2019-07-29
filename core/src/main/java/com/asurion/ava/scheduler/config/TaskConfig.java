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
 * The Items Schema
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "scheduler",
    "pollingInterval",
    "dispatchRate",
    "delayInSecond",
    "repeatCount",
    "batchSize",
    "taskQueue",
    "dispatcher"
})
public class TaskConfig {

    /**
     * The Name Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    private String name = "";
    /**
     * The Scheduler Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("scheduler")
    private String scheduler = "";
    /**
     * The Pollinginterval Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("pollingInterval")
    private Integer pollingInterval = 0;
    /**
     * The Dispatchrate Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dispatchRate")
    private Integer dispatchRate = 0;
    /**
     * The Delayinsecond Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("delayInSecond")
    private Integer delayInSecond = 0;
    /**
     * The Repeatcount Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("repeatCount")
    private Integer repeatCount = 0;
    /**
     * The Batchsize Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("batchSize")
    private Integer batchSize = 0;
    /**
     * The Taskqueue Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("taskQueue")
    private TaskQueue taskQueue;
    /**
     * The Dispatcher Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dispatcher")
    private List<Dispatcher> dispatcher = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Type Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * The Type Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The Pollinginterval Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("pollingInterval")
    public Integer getPollingInterval() {
        return pollingInterval;
    }

    /**
     * The Pollinginterval Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("pollingInterval")
    public void setPollingInterval(Integer pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    /**
     * The Dispatchrate Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dispatchRate")
    public Integer getDispatchRate() {
        return dispatchRate;
    }

    /**
     * The Dispatchrate Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dispatchRate")
    public void setDispatchRate(Integer dispatchRate) {
        this.dispatchRate = dispatchRate;
    }

    /**
     * The Delayinsecond Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("delayInSecond")
    public Integer getDelayInSecond() {
        return delayInSecond;
    }

    /**
     * The Delayinsecond Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("delayInSecond")
    public void setDelayInSecond(Integer delayInSecond) {
        this.delayInSecond = delayInSecond;
    }

    /**
     * The Repeatcount Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("repeatCount")
    public Integer getRepeatCount() {
        return repeatCount;
    }

    /**
     * The Repeatcount Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("repeatCount")
    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * The Batchsize Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("batchSize")
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * The Batchsize Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("batchSize")
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * The Taskqueue Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("taskQueue")
    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    /**
     * The Taskqueue Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("taskQueue")
    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    /**
     * The Dispatcher Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dispatcher")
    public List<Dispatcher> getDispatcher() {
        return dispatcher;
    }

    /**
     * The Dispatcher Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("dispatcher")
    public void setDispatcher(List<Dispatcher> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * The Taskqueue Schema
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("scheduler")
    public String getScheduler() {
        return scheduler;
    }
    
    @JsonProperty("scheduler")
    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }
    
}
