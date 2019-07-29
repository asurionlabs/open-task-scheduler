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
package com.asurion.ava.scheduler.dispatcher.elasticsearch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskDispatcher;
import com.asurion.ava.scheduler.spring.BeanName;

/**
 * Sends tasks to ElasticSearch.
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.ElasticSearchTaskDispatcher)
@Scope("prototype")
public class ElasticSearchTaskDispatcher implements TaskDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchTaskDispatcher.class);

    @SuppressWarnings("unused")
    private TaskSchedulerConfig taskSchedulerConfig;
    private ElasticSearchLogger esLogger;
    
    @Autowired
    public void setTaskSchedulerConfig(TaskSchedulerConfig taskSchedulerConfig) {
        this.taskSchedulerConfig = taskSchedulerConfig;
    }
    
    @Autowired
    public ElasticSearchTaskDispatcher(ElasticSearchLogger esLogger) {
        this.esLogger = esLogger;
    }
    
    @Override
    public List<Task> dispatch(List<Task> tasks) {
        //logger.info("dispatch tasks {}", taskSchedulerConfig.getElasticSearch().getUrl());
        try {
            esLogger.log(tasks);
        } catch(Exception e) {
            logger.error("ElasticSearchTaskDispatcher error", e);
        }
        return tasks;
    }
    
}
