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
package com.asurion.ava.scheduler.memoryqueue.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.TaskQueue;
import com.asurion.ava.scheduler.core.impl.AbstractTaskQueue;
import com.asurion.ava.scheduler.core.impl.AbstractTaskScheduler;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;

/**
 *
 * @author scott.cheng
 *
 */
@Component(BeanName.MemoryQueueTaskScheduler)
@Scope("prototype")
public class MemoryQueueTaskScheduler extends AbstractTaskScheduler {

    private final TaskSchedulerConfig taskSchedulerConfig;
    private AbstractTaskQueue memoryTaskQueue;
    
    
    @Autowired
    public MemoryQueueTaskScheduler(TaskSchedulerConfig taskSchedulerConfig
            ) {
        this.taskSchedulerConfig = taskSchedulerConfig;
        
    }
    
    @Override
    protected TaskQueue getTaskQueue() {
        return memoryTaskQueue;
    }
    
    @Override
    protected void initializeTaskQueue() {
        
        this.memoryTaskQueue = SpringContext.getBean(
                getTaskConfig().getTaskQueue().getType(), AbstractTaskQueue.class); 
        
        memoryTaskQueue.initialize(getName(), getTaskConfig());
       
    }
    
}
