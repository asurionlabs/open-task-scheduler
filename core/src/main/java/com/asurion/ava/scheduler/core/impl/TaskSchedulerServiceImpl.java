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

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.core.RedisConnectionPool;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.TaskSchedulerManager;
import com.asurion.ava.scheduler.core.TaskSchedulerService;
import com.asurion.ava.scheduler.spring.BeanName;

/**
 * Manages Task Scheduler Service life cycle.
 * It interacts with TaskSchedulerManager to retrieve the scheduler by name.
 *    
 * @author scott.cheng
 *
 */
@Component(BeanName.TaskSchedulerService)
public class TaskSchedulerServiceImpl implements TaskSchedulerService {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(TaskSchedulerServiceImpl.class);


    private final AtomicReference<TaskSchedulerState> state = new AtomicReference<>(TaskSchedulerState.STOPPED);

    private final TaskSchedulerManager taskSchedulerManager;

    @Autowired
    public TaskSchedulerServiceImpl(TaskSchedulerManager taskSchedulerManager) {
        this.taskSchedulerManager = taskSchedulerManager;
    }
    
    @Override
    public TaskScheduler getTaskScheduler(Task task) {
        if(isActive()) {
            TaskScheduler taskScheduler = taskSchedulerManager.getTaskScheduler(task);
            if(taskScheduler!=null) {
                return taskScheduler;
            } else {
                throw new RuntimeException(String.format("No getTaskScheduler found for %s ", TaskScheduler.getTaskName(task)));
            }
        } else {
            throw new RuntimeException("TaskSchedulerService is NOT Active");
        }
    }

    @Override
    public boolean isActive() {
        return state.get() == TaskSchedulerState.STARTED;
    }

    @Override
    public void start() {
        // start connection  pool
        // FIXME
        RedisConnectionPool.getInstance().start();
        
        // load and start schedulers
        taskSchedulerManager.loadTaskSchedulers();
        state.set(TaskSchedulerState.STARTED);

    }

    @Override
    public void stop() {

        // stop all schedulers
        taskSchedulerManager.shutdown();
        state.set(TaskSchedulerState.STOPPED);
        // shutdown conneciton pool
        RedisConnectionPool.getInstance().shutdown();
        // shudown aws connection pool
        com.amazonaws.http.IdleConnectionReaper.shutdown();
        
        

    }




}
