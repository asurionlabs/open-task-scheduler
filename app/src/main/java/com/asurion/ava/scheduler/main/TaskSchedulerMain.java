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

package com.asurion.ava.scheduler.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asurion.ava.scheduler.core.TaskSchedulerService;
import com.asurion.ava.scheduler.core.jmx.JmxService;

/**
 * Task Scheduler main class. It provide 'start()' & 'stop()' methods to 
 * control lifecycle of Task Scheduler.
 * 
 * @author scott.cheng
 *
 */
public class TaskSchedulerMain {

    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerMain.class);
    
    
    public static void start() {
        // Start task scheduler
        try {
            TaskSchedulerService taskSchedulerService = TaskSchedulerService.getInstance();
            taskSchedulerService.start();
        
            JmxService.getInstance().start();
        } catch (Throwable e) {
            logger.error("Error in starting TaskSchedulerService", e);
        }
    }
    
    public static void stop() {
        // Stop task scheduler
        try {
            TaskSchedulerService taskSchedulerService = TaskSchedulerService.getInstance();
            taskSchedulerService.stop();
            JmxService.getInstance().stop();
        } catch (Throwable e) {
            logger.error("Error in stopping TaskSchedulerService", e);
        }
        
    }
    
    
}
