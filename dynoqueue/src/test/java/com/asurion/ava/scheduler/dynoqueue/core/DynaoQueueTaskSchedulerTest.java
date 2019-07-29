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

import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;

import org.junit.Test;

import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.TaskSchedulerService;

public class DynaoQueueTaskSchedulerTest {

    public static void main(String[] args) {
        test();
    }
    
	//@Test
	public static void test() {
	    // Start task scheduler
	    TaskSchedulerService taskSchedulerService = TaskSchedulerService.getInstance();
        assertNotNull(taskSchedulerService);
        taskSchedulerService.start();
        
        /**
         * 
                "type": "test",
                "pollingInterval": 1,
                "dispatchRate": -1,
                "sqsDispatchURL":"https://sqs-url",
                "delayInSecond": 10,
                "repeatCount": 10
         */
        long taskCount=0;
        while(true) {
                for(int i=0; i<1000; ++i) {
                    Task task = new Task();
                    task.setType("test");
                    LinkedList<Task> tasks = new LinkedList<>();
                    tasks.add(task);
    
                    TaskScheduler taskScheduler = 
                            taskSchedulerService.getTaskScheduler(task);
                    assertNotNull(taskScheduler);
    
    
                    taskScheduler.schedule(tasks);
                    ++taskCount;
                }
            // Sleep 5 seconds!
            try {
                System.out.println("***** TaskCound: "+ taskCount);
                Thread.sleep(5 * 1000);
            } catch(Exception e) {
                
            }
        }
		
	}

}
