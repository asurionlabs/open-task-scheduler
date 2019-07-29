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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.asurion.ava.scheduler.config.TaskConfig;
import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.TaskDispatcher;
import com.asurion.ava.scheduler.core.jmx.mbeans.TaskManagement;

/**
 * Provides default methods to manage dispatcher.
 * 'dispatch()' method should be implemented by the actual dispatcher class.
 * 
 * @author scott.cheng
 *
 */
public abstract class AbstractTaskDispather implements TaskDispatcher {
    
    private final Logger logger = LoggerFactory.getLogger(AbstractTaskDispather.class);
    
    protected TaskSchedulerConfig taskSchedulerConfig;
    
    protected TaskConfig taskConfig;
    protected String name;
    protected TaskManagement taskManagement;
    
    
    @Autowired
    public void setTaskSchedulerConfig(TaskSchedulerConfig taskSchedulerConfig, TaskManagement taskManagement) {
        this.taskSchedulerConfig = taskSchedulerConfig;
        this.taskManagement = taskManagement;
    }
    
    public void initialize(TaskConfig taskConfig, String name) {
        this.taskConfig = taskConfig;
        this.name = name;
        logger.info("initialize {}", this);
    }
    
    @Override
    public String toString() {
        return String.format("%s: [%s][hash:%s]", this.getClass().getSimpleName(), name, hashCode());
    }
    
}
