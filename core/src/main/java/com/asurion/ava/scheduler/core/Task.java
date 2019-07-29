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
package com.asurion.ava.scheduler.core;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.asurion.ava.scheduler.enums.TaskStatus;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * Task entity object.
 * 
 * @author scott.cheng
 *
 */
public class Task {
    // sms
    private String type;
    // 12345
    private String taskId;
    
    // 0, 60, 3600
    private Long delayInSecond;
    // 0 - no repeat
    // 1 - repeat 1 time (initial one excluded)
    private Long repeatCount;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String id) {
        this.taskId = id;
    }
    
    public Long getDelayInSecond() {
        return delayInSecond;
    }
    public void setDelayInSecond(Long delayInSecond) {
        this.delayInSecond = delayInSecond;
    }
    public Long getRepeatCount() {
        return repeatCount;
    }
    public void setRepeatCount(Long repeatCount) {
        this.repeatCount = repeatCount;
    }
    
    //-------------------------------------------
    // Extra attributes
    //
    private TaskStatus status;
    
    private String scheduledTime;
    private String dispatchedTime;
    
    private String creationTime;
    
    private String timestamp = Instant.now().toString();

    public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    public String getScheduledTime() {
        return scheduledTime;
    }
    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    public String getDispatchedTime() {
        return dispatchedTime;
    }
    public void setDispatchedTime(String dispatchedTime) {
        this.dispatchedTime = dispatchedTime;
    }
    public String getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    //------------------------------------------------------
    public String runId=null;
    
    public String getRunId() {
        return runId;
    }
    
    public void setRunId(String runId) {
        this.runId = runId;
    }
    
    private Map<String, String> additional = new HashMap<>();
    
    public void setAdditional(Map<String, String> additional) {
        this.additional = additional;
    }
    
    public Map<String, String> getAdditional() {
        return additional;
    }
    
    
    //-------------------------------------------------------
    public static class NextTask {
        private Task task;
        
        public NextTask(Task task) {
            this.task = task;
        }
        
        public void setTask(Task task) {
            this.task = task;
        }
        
        public Task getTask() {
            return task;
        }
    }
    private NextTask nextTask;
    
    public void setNextTask(NextTask nextTask) {
        this.nextTask = nextTask;
    }
    public NextTask getNextTask() {
        return nextTask;
    }
    
    
    public static Task cloneTask(Task t1) {
        
        Task t2 = new Task();
        t2.taskId               = t1.taskId;
        t2.type                 = t1.type;
        t2.delayInSecond        = t1.delayInSecond;
        t2.repeatCount          = t1.repeatCount;
        t2.additional           = t1.additional;
        return t2;
        
    }
    
    //------------------------------------------------
    // 
    private String failReason=null;
    public void setFailStatus(TaskStatus status, String failReason) {
        this.status         = status;
        this.failReason     = failReason;
    }
    
    public String getFailReason() {
        return failReason;
    }
    
    //------------------------------------------------
    public static Task getTask(String json) {
        Task t = MiscUtil.toPojo(json, Task.class);
        t.timestamp = Instant.now().toString();
        
        return t;
    }
    
}
