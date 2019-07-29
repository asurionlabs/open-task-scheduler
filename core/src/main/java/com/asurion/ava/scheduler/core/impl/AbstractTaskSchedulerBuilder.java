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
package com.asurion.ava.scheduler.core.impl;

import com.asurion.ava.scheduler.config.TaskConfig;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.TaskSchedulerBuilder;

/**
 * Builds the task scheduler per 'TaskConfig' defined in JSON.
 * E.g.
 * {
            "name": "demo_task",
            "scheduler": "MemoryQueueTaskScheduler",
            "pollingInterval": 1,
            "dispatchRate": -1,
            "delayInSecond": 0,
            "repeatCount": 0,
            "batchSize": 0,
            "taskQueue": {
                "type": "MemoryTaskQueue"
            },
            "dispatcher": [
                {
                    "type": "StdOutTaskDispatcher"
                }
            ]
            
   }
   This configuration defines a task scheduler for 'demo_task'. 
   The scheduler is based on the 'MemoryQueueTaskScheduler' class. 
   The task is saved to 'MemoryTaskQueue'.
   The task is is dispatched to 'StdOutTaskDispatcher' when the task is expired and ready to go.
 * 
 * @author scott.cheng
 *
 */
public abstract class AbstractTaskSchedulerBuilder implements TaskSchedulerBuilder {

    @Override
    public final TaskScheduler build(TaskConfig taskConfig) {
        
        AbstractTaskScheduler abstractTaskScheduler = doCreateTaskScheduler();
        abstractTaskScheduler.initialize(taskConfig);
        
        return abstractTaskScheduler;
    }
    
    protected abstract AbstractTaskScheduler doCreateTaskScheduler();
    
}
