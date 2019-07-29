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

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskConfig;
import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskQueue;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.impl.AbstractTaskQueue;
import com.asurion.ava.scheduler.spring.BeanName;

/**
 * Simple priority queue implementation that keeps tasks with priority in 'schedule time'.
 * The priority queue head contains the task with earliest timeout.
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.MemoryTaskQueue)
@Scope("prototype")
public class MemoryTaskQueue extends AbstractTaskQueue implements TaskQueue {
   
    private final Logger logger = LoggerFactory.getLogger(MemoryTaskQueue.class);
    
    private final TaskSchedulerConfig taskSchedulerConfig;
    private String queueName;
    private TaskConfig taskConfig;
    
    private static class MemTask {
        private final long schTime;
        private final Task task;
        
        private MemTask(long schTime, Task task) {
            
            this.schTime = schTime;
                    //System.currentTimeMillis() + (task.getDelayInSecond() * 1000);
            this.task = task;
        }
    }
    
    private final PriorityQueue<MemTask> priorityQueue = new PriorityQueue<>(
            (t1, t2) -> Long.compare(t1.schTime, t2.schTime)
            );
    
    @Autowired
    public MemoryTaskQueue(
            TaskSchedulerConfig taskSchedulerConfig) {
        
        this.taskSchedulerConfig = taskSchedulerConfig;
        
    }
    
    @Override
    public String getName() {
        return queueName;
    }
    
    
    @Override
    public synchronized List<Task> push(List<Task> tasks) {
        long now = now(); 
        
        tasks.forEach( (task) -> {
            long timeout = task.getDelayInSecond()!=null && task.getDelayInSecond()>0?
                 now+ TimeUnit.MILLISECONDS.convert(task.getDelayInSecond(), TimeUnit.SECONDS) : now;
            logger.info("Scheduled task: {}, {}", task.getTaskId(), timeout);
            
            priorityQueue.add( new MemTask(timeout, task) );
            
        
        });
        
        return tasks;
    }

    @Override
    public synchronized List<Task> pop(int taskCount, int wait, TimeUnit unit) {
        
        List<Task> tasks = new LinkedList<Task>();
        long now = now();      
        
        while(!priorityQueue.isEmpty()) {
            
            if(priorityQueue.peek().schTime>now) {
                break;
            }
            Task task = priorityQueue.poll().task;
            logger.info("======> Dispatch task: {}", task.getTaskId());
            tasks.add(task);
            
        }
        
        
        
       return tasks;
    }
    
    public Task getTask(String taskId) {
        final Task[] result = new Task[1];
        
        priorityQueue.forEach( (memTask) -> {
           if(memTask.task.getTaskId().equals(taskId)) {
               //final Task task0 = memTask.task;
               result[0] = memTask.task;
           }
        });
        
       return result[0];
        
    }

    @Override
    public boolean remove(String taskId) {
        final MemTask[] result = new MemTask[1];
        
        priorityQueue.forEach( (memTask) -> {
           if(memTask.task.getTaskId().equals(taskId)) {
               //final Task task0 = memTask.task;
               result[0] = memTask;
           }
        });
        if(result[0]!=null) 
            priorityQueue.remove(result[0]);
        
        return false;
    }
    
    @Override
    public long size() {
        
        return priorityQueue.size();
    }

    @Override
    public boolean setTimeout(String taskId, long timeout) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initialize(String name, TaskConfig taskConfig) {
        this.queueName = TaskScheduler.getTaskName(taskConfig);
        this.taskConfig = taskConfig;
        
        
    }
    
    @Override
    public String toString() {
        
        return String.format("DyanoTaskQueue [%s] [hash:%d]", queueName, hashCode());
    }
    
    @Override
    public void stop() {
        logger.info("stop {}", this);
        
        
    }
    
    private static long now() {
        return System.currentTimeMillis();
    }
    
}
