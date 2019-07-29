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
package com.asurion.ava.scheduler.dynoqueue.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.TaskQueue;
import com.asurion.ava.scheduler.core.impl.AbstractTaskScheduler;
import com.asurion.ava.scheduler.spring.BeanName;

/**
 * Provides dyno task queue to AbstractTaskScheduler to save scheduled tasks & retrieve 
 * expired tasks.
 *  
 * @author scott.cheng
 *
 */
@Component(BeanName.DynoQueueTaskScheduler)
@Scope("prototype")
public class DynoQueueTaskScheduler extends AbstractTaskScheduler {

    @SuppressWarnings("unused")
    private final TaskSchedulerConfig taskSchedulerConfig;
    
    private final DynoTaskQueue dyanoTaskQueue;
    
    @Autowired
    public DynoQueueTaskScheduler(TaskSchedulerConfig taskSchedulerConfig
            , DynoTaskQueue dyanoTaskQueue) {
        this.taskSchedulerConfig = taskSchedulerConfig;
        
        this.dyanoTaskQueue = dyanoTaskQueue;
    }
    
    @Override
    protected TaskQueue getTaskQueue() {
        return dyanoTaskQueue;
    }
    
    @Override
    protected void initializeTaskQueue() {
        dyanoTaskQueue.initialize(getName(), getTaskConfig());
       
    }
    
}
