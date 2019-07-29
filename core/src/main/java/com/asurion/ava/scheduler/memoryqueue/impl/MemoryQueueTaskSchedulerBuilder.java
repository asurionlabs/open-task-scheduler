package com.asurion.ava.scheduler.memoryqueue.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.core.impl.AbstractTaskScheduler;
import com.asurion.ava.scheduler.core.impl.AbstractTaskSchedulerBuilder;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;

@Component(BeanName.MemoryQueueTaskSchedulerBuilder)
@Scope("prototype")
public class MemoryQueueTaskSchedulerBuilder extends AbstractTaskSchedulerBuilder {
    
    
    @Override
    public AbstractTaskScheduler doCreateTaskScheduler() {
        
        return SpringContext.getBean(MemoryQueueTaskScheduler.class);
    }
    
    
}
