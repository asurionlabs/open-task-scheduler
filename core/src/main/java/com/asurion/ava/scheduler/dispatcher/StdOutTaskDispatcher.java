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
package com.asurion.ava.scheduler.dispatcher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.impl.AbstractTaskDispather;
import com.asurion.ava.scheduler.spring.BeanName;

/**
 * A simple task dispatcher example that simply prints out tasks to 'standard out'.
 *  
 * @author scott.cheng
 *
 */
@Component(BeanName.StdOutTaskDispatcher)
@Scope("prototype")
public class StdOutTaskDispatcher extends AbstractTaskDispather {

    private final Logger logger = LoggerFactory.getLogger(StdOutTaskDispatcher.class);
    
    @Override
    public List<Task> dispatch(List<Task> tasks) {
        
        
        tasks.forEach( (task) -> {
           logger.info("Dispatch task: {}", task.getTaskId()); 
        });
        
        
        
        return tasks;
        
    }

    
    
    
}
