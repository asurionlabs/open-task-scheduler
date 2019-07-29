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

import java.util.LinkedList;
import java.util.List;

import com.asurion.ava.scheduler.config.TaskConfig;

/**
 * Task scheduler abstraction providing methods such as 'schedule()', 'poll()' and etc. to easily access
 * the scheduler.
 * 
 * @author scott.cheng
 *
 */
public interface TaskScheduler {
    
    public String getName();
    
    
    public default Task schedule(Task task) {
        LinkedList<Task> taskL = new LinkedList<>();
        taskL.add(task);
        schedule(taskL);
        return task;
    }
    
    /**
     * Schedule task
     * 
     * @param tasks to be added to queue
     * @return tasks that are pushed to queue
     */
    public List<Task> schedule(List<Task> tasks);
    
    /**
     * Trigger poll
     * @return
     */
    public void poll();
    
    /**
     * Cancel task
     * 
     * @param taskId Remove the task from the queue
     * @return true if the task is found and removed, otherwise false.
     */
    public boolean cancel(Task task);
    
    /**
     * Update delay
     * 
     * @param taskId
     * @param timeout
     * @return true if the task is found and updated with new timeout, otherwise false.
     */
    public boolean reschedule(String taskId, long timeout);
    
    /**
     * 
     * @return TaskSchedulerLifecycleListener to receive start, stop event(s)
     */
    public TaskSchedulerLifecycleListener getTaskSchedulerLifecycleListener();
    
    public static String getTaskName(TaskConfig c) {
        /*
        StringBuilder sb = new StringBuilder();
        sb.append(c.getType());
        if(c.getPartner()!=null) {
            sb.append('_');
            sb.append(c.getPartner());
        }
        if(c.getContext()!=null) {
            sb.append('_');
            sb.append(c.getContext());
        }
        
        return sb.toString();
        */
        
        return c.getName();
    }
    
    public static String getTaskName(Task c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getType());
        
        return sb.toString();
    }
    
    public static String getTaskNameOnly(Task c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getType());
        
        return sb.toString();
    }

}
