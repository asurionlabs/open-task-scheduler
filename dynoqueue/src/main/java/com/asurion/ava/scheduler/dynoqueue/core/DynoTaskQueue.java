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

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import com.asurion.ava.scheduler.dynoqueue.redis.RedisQueuesProvider;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.util.MiscUtil;
import com.netflix.dyno.queues.DynoQueue;
import com.netflix.dyno.queues.Message;

/**
 * Task queue implementation which provides:
 * push() - Enqueue task to 'Delay' dynao task queue. The delay queue head contains 
 *          tasks with earliest expiration timestamp
 * pop() - Dequeue task from dynao task queue. Remove tasks from queue head for those 
 *         tasks with expiration time less than or equal to current time.
 *             
 * @author scott.cheng
 *
 */
@Component(BeanName.DyanoTaskQueue)
@Scope("prototype")
public class DynoTaskQueue extends AbstractTaskQueue implements TaskQueue {
   
    private final Logger logger = LoggerFactory.getLogger(DynoTaskQueue.class);
    
    @SuppressWarnings("unused")
    private final TaskSchedulerConfig taskSchedulerConfig;
    private final RedisQueuesProvider redisQueuesProvider;
    // Queue name
    private String queueName;
    @SuppressWarnings("unused")
    private TaskConfig taskConfig;
    
    private DynoQueue dynoQueue;
    
    @Autowired
    public DynoTaskQueue(
            TaskSchedulerConfig taskSchedulerConfig,
            RedisQueuesProvider redisQueuesProvider) {
        
        this.taskSchedulerConfig = taskSchedulerConfig;
        this.redisQueuesProvider = redisQueuesProvider;
    }
    
    @Override
    public String getName() {
        return queueName;
    }
    
    
    @Override
    public List<Task> push(List<Task> tasks) {
        List<Message> messages = new LinkedList<>();
        
        tasks.stream().forEach(task -> {
            Message m = new Message(task.getTaskId(), MiscUtil.toJson(task));
            
            if(task.getDelayInSecond()!=null && task.getDelayInSecond()>0) {
                m.setTimeout(task.getDelayInSecond(), TimeUnit.SECONDS);
            }
            messages.add(m);
        });
        
        dynoQueue.push(messages);
        
        return tasks;
    }

    @Override
    public List<Task> pop(int taskCount, int wait, TimeUnit unit) {
        List<Message> messages = dynoQueue.pop(taskCount, wait, unit);
        
        if(messages!=null && !messages.isEmpty()) {
            // ack on pop
            dynoQueue.ack(messages);
            List<Task> tasks = new LinkedList<>();
            messages.stream().forEach(m -> {
                tasks.add(Task.getTask(m.getPayload()));
            });
            
            return tasks;
        }
        
        return Collections.emptyList();
    }
    
    public Task getTask(String taskId) {
        Message m = dynoQueue.get(taskId);
        return m != null ?
                Task.getTask(m.getPayload()): null;
        
    }

    @Override
    public boolean remove(String taskId) {
        
        return dynoQueue.remove(taskId);
    }
    
    @Override
    public long size() {
        
        return dynoQueue.size(); 
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
        
        dynoQueue = redisQueuesProvider.getQueue(queueName);
        
        logger.info("initialize {}", this);
    }
    
    @Override
    public String toString() {
        
        return String.format("DyanoTaskQueue [%s] [hash:%d]", queueName, hashCode());
    }
    
    @Override
    public void stop() {
        logger.info("stop {}", this);
        try {
            dynoQueue.close();
        } catch(IOException e) {
            throw new RuntimeException("Error in closing dynoQueue", e);
        }
        
    }
    
}
