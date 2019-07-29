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

import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;

/**
 * The top level Task Scheduler interface provide methods to:
 * 1) start scheduler(s) per JSON configuration
 * 2) stop  scheduler(s) per JSON configuration
 * 3) get the scheduler per task type
 * 
 * Called by TaskSchedulerMain to start & stop schedulers. 
 * When a schedule task API is called by REST, the "getTaskScheduler()" is 
 * called to retrieve the scheduler to schedule the task.
 * 
 * @author scott.cheng
 *
 */
public interface TaskSchedulerService {

    public enum TaskSchedulerState {
        STOPPED,
        STARTED,
    }
    
    /**
     * 
     * @param task gets the scheduler for the given task
     * @return TaskScheduler
     */
    public TaskScheduler getTaskScheduler(Task task);

    public boolean isActive();

    public void start();
    public void stop();
    


    public static TaskSchedulerService getInstance() {
        return SpringContext.getBean(BeanName.TaskSchedulerService, 
                TaskSchedulerService.class);
    }

    

}
