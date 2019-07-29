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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskConfig;
import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskDispatcher;
import com.asurion.ava.scheduler.core.TaskIdGenerator;
import com.asurion.ava.scheduler.core.TaskPoller;
import com.asurion.ava.scheduler.core.TaskQueue;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.jmx.mbeans.TaskManagement;
import com.asurion.ava.scheduler.enums.TaskStatus;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * Maintains a  poller daemon thread that continually checking 
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.TaskPoller)
@Scope("prototype")
public class TaskPollerImpl implements TaskPoller {
    
    private final Logger logger = LoggerFactory.getLogger(TaskPollerImpl.class);
    
    public enum State {
        STOPPED
        , STARTED
    }
    
    @SuppressWarnings("unused")
    private final TaskSchedulerConfig taskSchedulerConfig;
    
    private TaskScheduler taskScheduler;
    private TaskQueue taskQueue;
    private TaskDispatcher taskDispatcher;
    private TaskConfig taskConfig;
    
    private TaskManagement taskManagement;
    
    private long lastLogTime = 0L;
    
    private final ScheduledExecutorService pollingProcessing = Executors.newScheduledThreadPool(1, r -> {
        Thread t = new Thread(r);
        t.setName("p_"+taskQueue.getName());
        t.setDaemon(true);
        return t;
    });
    
    private final AtomicReference<State> state = new AtomicReference<TaskPollerImpl.State>(State.STOPPED);
    
    @Autowired
    public TaskPollerImpl(TaskSchedulerConfig taskSchedulerConfig, TaskManagement taskManagement) {
        this.taskSchedulerConfig = taskSchedulerConfig;
        this.taskManagement = taskManagement;
    }
    
    @Override
    public void initilize(TaskScheduler taskScheduler, TaskQueue taskQueue, TaskDispatcher taskDispatcher, TaskConfig taskConfig) {
        this.taskScheduler  = taskScheduler;
        this.taskQueue      = taskQueue;
        this.taskDispatcher = taskDispatcher;
        this.taskConfig     = taskConfig;
        
        logger.info("initialize {}", this);
    }
    
    @Override
    public void start() {
        long pollingInterval = taskConfig.getPollingInterval();
        pollingProcessing.scheduleAtFixedRate(() -> poll(), pollingInterval, pollingInterval, TimeUnit.SECONDS);
        state.set(State.STARTED);
        logger.info("start {} in {} second(s)", this, pollingInterval);
        
    }
    
    @Override
    public void stop() {
        // gracefully shutdown polling thread 
        //
        pollingProcessing.shutdown();
        int ct=10;
        //All done, ready to exit
        while (!pollingProcessing.isTerminated()) {
            try {
                pollingProcessing.awaitTermination(500,TimeUnit.MILLISECONDS);
            } catch(Exception e) {
                
            }
            // Total wait 5 seconds and then shutdown no matter!
            if(--ct>0) {
                pollingProcessing.shutdownNow();
                break;
            }
        }
        
        state.set(State.STOPPED);
        logger.info("stop {}", this);
    }
    
    @Override
    public void poll() {
        // TODO error handling!
        try {
          if(state.get() == State.STARTED) {
              int taskCount = taskConfig.getDispatchRate();
              if(taskCount==-1) {
                  taskCount = Integer.MAX_VALUE;
              }
              if(isLogging()) {
                  logger.info("run (maxTasks={}) ", taskCount);
              }
              
              long qSize = taskQueue.size();
              // TODO maybe only poll if qSize>0 to reduce overhead!
              LinkedList<Task> repeatTasks = new LinkedList<>();
              List<Task> tasks = taskQueue.pop(taskCount, 0, TimeUnit.SECONDS);
              if(logger.isDebugEnabled()) {
                  logger.debug(String.format("recvTask=%d", tasks.size()));
              }
              
              taskManagement.pollingStatus(taskScheduler.getName(), String.format("[expired Task=%d], [qSize=%d]", tasks!=null? tasks.size():0, qSize));
              
              if(tasks!=null && !tasks.isEmpty()) {
                  
                  if(logger.isDebugEnabled()) {
                      tasks.stream().forEach(task -> logger.debug("dispatch task ===> {}", MiscUtil.toJson(task)));
                  }
                  
                  // log to jmx number of tasks polled
                  taskManagement.incrementTotalNumTasksPolled(tasks.size());
                  
                  tasks.stream().forEach(task -> {
                      // 1. Set tasks Dispatched
                      task.setStatus(TaskStatus.DISPATCHED);
                      task.setDispatchedTime(task.getTimestamp()); 
                      // generate unique run Id
                      task.setRunId(TaskIdGenerator.getInstance().getNextId());
                      if(task.getRepeatCount()>0) {
                          //task.setNextTask(new Task.NextTask(Task.newTask(task)));
                          //repeatTasks.add(task.getNextTask().getTask());
                          Task nextTask = Task.cloneTask(task);
                          repeatTasks.add(nextTask);
                          
                      }
                      if(logger.isDebugEnabled()) {
                          logger.debug("dispatch task ===> {}", MiscUtil.toJson(task));
                      }

                  });
                  
                  // dispatch tasks
                  try {
                      taskDispatcher.dispatch(tasks);
                  } catch(Exception e) {
                      logger.error("Fail on poll - dispatch tasks", e);
                      taskManagement.pollingError(e);
                  }
                  
                  // 2. schedule repeat tasks
                  try {
                      if(!repeatTasks.isEmpty()) {
                          taskScheduler.schedule(repeatTasks);
                      }
                  }catch(Exception e) {
                      logger.error("Fail on poll - schedule repeat tasks", e);
                      taskManagement.pollingError(e);
                  }
                  
                  
                  // 4. log to jmx
                  //taskManagement.incrementTasksDispatched(tasks.size());
                  tasks.stream().filter(t->t.getStatus()==TaskStatus.DISPATCHED).forEach(t->taskManagement.incrementTasksDispatched());
              }
              
              
          }
        } catch(Throwable e) {
            logger.error("Fail on poll", e);
            taskManagement.pollingError(e);
        }
    }
    
    @Override
    public String toString() {
        return String.format("TaskPoller [%s] [hash:%d]", taskQueue.getName(), hashCode());
    }
    
    private static final long logInterval = 10*60 *1000;
    private boolean isLogging() {
        
        if(System.currentTimeMillis()-lastLogTime > logInterval) {
            lastLogTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    

}
