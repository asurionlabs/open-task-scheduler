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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.asurion.ava.scheduler.config.TaskConfig;
import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskIdGenerator;
import com.asurion.ava.scheduler.core.TaskPoller;
import com.asurion.ava.scheduler.core.TaskQueue;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.TaskSchedulerLifecycleListener;
import com.asurion.ava.scheduler.core.jmx.mbeans.TaskManagement;
import com.asurion.ava.scheduler.dispatcher.TaskDispatherChain;
import com.asurion.ava.scheduler.enums.TaskStatus;
import com.asurion.ava.scheduler.spring.SpringContext;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * Provides implementation in scheduling tasks, polling tasks and dispatching tasks. 
 * 
 * @author scott.cheng
 *
 */
public abstract class AbstractTaskScheduler implements TaskScheduler, TaskSchedulerLifecycleListener {
    
    private final Logger logger = LoggerFactory.getLogger(AbstractTaskScheduler.class);
    
    protected TaskSchedulerConfig taskSchedulerConfig;
    
    private final TaskIdGenerator taskIdGenerator = TaskIdGenerator.getInstance();
  
    // The task that the scheduler is associated to
    private String name;
    private TaskConfig taskConfig;
    
    protected TaskPoller taskPoller;
    
    private TaskDispatherChain taskDispatherChain;
    
    private TaskManagement taskManagement;
    
    @Override
    public String getName() {
        return name;
    }
   
    public TaskConfig getTaskConfig() {
        return taskConfig;
    }
    
    @Autowired
    public void setTaskPoller(TaskPoller taskPoller) {
        this.taskPoller = taskPoller;
    }
    
    @Autowired
    public void setTaskDispatherChain(TaskDispatherChain taskDispatherChain) {
        this.taskDispatherChain = taskDispatherChain;
    }
    
    
    
    @Autowired
    public void setTaskManagement(TaskManagement taskManagement) {
        this.taskManagement = taskManagement;
    }
    
    public void initialize(TaskConfig taskConfig) {
        // 
        // 1. set taskConfig
        this.name = TaskScheduler.getTaskName(taskConfig);
        this.taskConfig = taskConfig;
        
        // 2. initialize task queue
        initializeTaskQueue();
        
        // 3. setup task dispatcher(s)
        taskDispatherChain.initialize(taskConfig, name);
        
        taskConfig.getDispatcher().forEach( (dispatcher) -> {
            AbstractTaskDispather taskDisparcher = 
                    SpringContext.getBean(dispatcher.getType(), AbstractTaskDispather.class);
                   
            taskDisparcher.initialize(taskConfig, name);
            taskDispatherChain.addTaskDispatcher(taskDisparcher);
        });
        
        // 4. setup poller 
        taskPoller.initilize(this, getTaskQueue(), taskDispatherChain, taskConfig);
        
        
        
    }

    
    @Override
    public List<Task> schedule(List<Task> tasks) {
        tasks.stream().forEach(task -> {
            
            // reuse taskId if taskId was already assigned
            if(task.getTaskId()==null) {
                task.setTaskId(taskIdGenerator.getNextId());
            }
            
            
            if(task.getDelayInSecond() == null) {
                Long delayInSecond = taskConfig.getDelayInSecond()!=null? taskConfig.getDelayInSecond():0L;
                task.setDelayInSecond(delayInSecond);
            }
            if(task.getRepeatCount() == null) {
                Long repeatCount = taskConfig.getRepeatCount()!=null? taskConfig.getRepeatCount():0L;
                task.setRepeatCount(repeatCount);
            }
            task.setStatus(TaskStatus.SCHEDULED);
            task.setScheduledTime(MiscUtil.getTimeInString(task.getDelayInSecond()));
            task.setCreationTime(MiscUtil.getTimeInString());
            
            // minus repeat count by 1
            if(task.getRepeatCount()>0) {
                task.setRepeatCount(task.getRepeatCount()-1);
            }
            
        });
        getTaskQueue().push(tasks);
        
        taskManagement.incrementTasksScheduled(tasks.size());
        //kinesisLoggerTaskDispatcher.dispatch(tasks);
        
        
        return tasks;
    }

    @Override
    public void poll() {
        taskPoller.poll();
    }

    @Override
    public boolean cancel(Task task0) {
       
        String taskId = task0.getTaskId();
        
        if(taskId==null) {
            throw new IllegalArgumentException("taskId is NULL");
        }
       
        Task task = getTaskQueue().getTask(taskId);
        
        boolean deleted = false;
        
        if(task != null) {
            deleted = getTaskQueue().remove(taskId);
            if(deleted) {
                task.setStatus(TaskStatus.CANCELED);
            }
        }
        
        return deleted;
    }

    @Override
    public boolean reschedule(String taskId, long timeout) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public TaskSchedulerLifecycleListener getTaskSchedulerLifecycleListener() {
        return this;
    }

    //---------------------------------------------------
    // Lifecycle events
    //
    @Override
    public void start() {
        //getTaskQueue()
        taskPoller.start();
        logger.info("start {}", this);
        
    }

    @Override
    public void stop() {
        taskPoller.stop();
        getTaskQueue().stop();
        logger.info("stop {}", this);
    }

    @Override
    public void pause() {
        // TODO 
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String toString() {
        return String.format("TaskScheduler: [%s][hash:%s]", name, hashCode());
    }
    

    //------------------------------------------
    // Abstract Methods
    protected abstract void initializeTaskQueue();
    protected abstract TaskQueue getTaskQueue();
    
}
