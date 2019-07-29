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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Provides simple methods to push, poll, or remove tasks to the queue.
 *  
 * @author scott.cheng
 *
 */
public interface TaskQueue {
    /**
     * 
     * @return name of the queue
     */
    public String getName();
    

    /**
     * Schedules a task
     * 
     * @param tasks to be added to queue
     * @return tasks that are pushed to queue
     */
    public List<Task> push(List<Task> tasks);
    
    /**
     * Polls tasks
     * 
     * @param taskCount number of tasks to be popped out
     * @param wait longest wait time if there're no tasks
     * @param unit Time unit
     * @return
     */
    public List<Task> pop(int taskCount, int wait, TimeUnit unit);
    
    /**
     * Retrieves task by id
     * 
     * @param taskId
     * @return
     */
    public Task getTask(String taskId);
    
    /**
     * Cancels the task
     * 
     * @param taskId Remove the task from the queue
     * @return true if the task is found and removed, otherwise false.
     */
    public boolean remove(String taskId);
    
    /**
     * Update delay
     * 
     * @param taskId
     * @param timeout
     * @return true if the task is found and updated with new timeout, otherwise false.
     */
    public boolean setTimeout(String taskId, long timeout);
    
    /**
     * Get current task queue size in Redis
     * 
     * @return
     */
    public long size();
    
    /**
     * Stops the queue
     */
    public void stop();
    
}
