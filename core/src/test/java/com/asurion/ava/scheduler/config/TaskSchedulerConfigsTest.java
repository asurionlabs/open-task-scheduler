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

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;


public class TaskSchedulerConfigsTest {
    
    
    static Logger logger = Logger.getLogger(TaskSchedulerConfigsTest.class);

    @Test
    public void test() {
        TaskSchedulerConfig configs = 
                SpringContext.getBean(BeanName.TaskSchedulerConfig, TaskSchedulerConfig.class);
                //TaskSchedulerConfigsFactory.getInstance().getTaskSchedulerConfigs();
        
        configs.getTaskConfigs().forEach(taskConfig ->{
            
            logger.info(String.format("%s, , %s, %s, %s", 
                    taskConfig.getName()
                    , taskConfig.getScheduler()
                    , taskConfig.getTaskQueue().getType()
                    , taskConfig.getDispatcher().get(0).getType())
                    );
            
        }
        );
        
    }

}
