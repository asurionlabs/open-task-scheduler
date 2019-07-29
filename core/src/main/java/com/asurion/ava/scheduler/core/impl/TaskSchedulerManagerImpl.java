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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.TaskSchedulerManager;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;

/**
 * Maintains schedulers in a map. Each scheduler has a name associated and the name is used to
 * retrieve the scheduler.   
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.TaskSchedulerManager) 
public class TaskSchedulerManagerImpl implements TaskSchedulerManager {
    private static final Logger logger = Logger.getLogger(TaskSchedulerManagerImpl.class);
    //private final Logger logger = LoggerFactory.getLogger(TaskSchedulerManagerImpl.class);
    
    private final Map<String, TaskScheduler> schedulers= new HashMap<>();
    
    private final TaskSchedulerConfig taskSchedulerConfig;
    //private final RedisQueues queues;

    @Autowired
    public TaskSchedulerManagerImpl(TaskSchedulerConfig taskSchedulerConfig) {
        this.taskSchedulerConfig = taskSchedulerConfig;
    }
    
    @Override
    public TaskScheduler getTaskScheduler(Task task) {
    	TaskScheduler taskScheduler = schedulers.get(TaskScheduler.getTaskName(task));
    	if(taskScheduler == null)
    		taskScheduler = schedulers.get(TaskScheduler.getTaskNameOnly(task));
        return taskScheduler;
    }

    @Override
    public void loadTaskSchedulers() {
        logger.info("*********** load TaskSchedulers ***********");
        schedulers.clear();
        taskSchedulerConfig.getTaskConfigs().forEach(taskConfig ->{
            //logger.info( String.format("build task scheduler - [%s][%s]", taskConfig.getName(), taskConfig.getScheduler()) );
            
            // 1. get scheduler builder
            AbstractTaskSchedulerBuilder taskSchedulerBuilder = SpringContext.getBean(String.format("%sBuilder", taskConfig.getScheduler()), AbstractTaskSchedulerBuilder.class);
            TaskScheduler taskScheduler = taskSchedulerBuilder.build(taskConfig);
            
            String taskName = taskScheduler.getName();
            logger.info( String.format("Build %s", taskScheduler));
            // 
            schedulers.put(taskName, taskScheduler);
            // start task scheduler
            taskScheduler.getTaskSchedulerLifecycleListener().start();
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        
        }
        );
        
        
        /*
        TaskSchedulerBuilder taskSchedulerBuilder = TaskSchedulerBuilder.getInstance();
        
        logger.info("*********** load TaskSchedulers ***********");
        schedulers.clear();
        
        TaskSchedulerConfig configs = TaskSchedulerConfigsFactory.getInstance().getTaskSchedulerConfigs();
        configs.getTaskConfigs().forEach(taskConfig ->{
        
        
        
        }
        );
        */
        /*
        taskSchedulerConfig.getTaskConfigCollection().stream().forEach(taskConfig-> {
            // build task scheduler
            TaskScheduler taskScheduler = taskSchedulerBuilder.build(taskConfig);
            String taskName = taskScheduler.getName();
            logger.info("Build {}", taskScheduler);
            // 
            schedulers.put(taskName, taskScheduler);
            // start task scheduler
            taskScheduler.getTaskSchedulerLifecycleListener().start();
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        });
        */
    }

    @Override
    public void shutdown() {
        logger.info("*********** shutdown TaskSchedulers ***********");
        schedulers.values().stream().forEach(taskScheduler -> {
            // stop task scheduler
            taskScheduler.getTaskSchedulerLifecycleListener().stop();
            logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        });
        schedulers.clear();
        
        
    }

    

    


}
