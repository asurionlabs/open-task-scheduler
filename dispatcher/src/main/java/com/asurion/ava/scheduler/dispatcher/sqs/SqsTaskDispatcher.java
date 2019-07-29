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
package com.asurion.ava.scheduler.dispatcher.sqs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.impl.AbstractTaskDispather;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.util.MiscUtil;
import com.google.common.collect.Lists;

/**
 * Sends tasks to AWS SQS
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.SqsTaskDispatcher)
@Scope("prototype")
public class SqsTaskDispatcher extends AbstractTaskDispather {
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(SqsTaskDispatcher.class);
    
    private final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
            
    @Override
    public List<Task> dispatch(List<Task> tasks) {
        //logger.info("dispatch {} tasks to {}", tasks.size(), taskConfig.getDispatchDestination());
        doSendInBatchPartition(tasks);
        return tasks;
        
    }
    
    public void doSend(List<Task> tasks) {
        
        tasks.stream().forEach(t -> {
            SendMessageRequest sqsMsg = new SendMessageRequest()
                    .withQueueUrl("SQS-URL-TBD")
                    .withMessageBody(MiscUtil.toJson(t))
                    .withDelaySeconds(0);
            
            sqs.sendMessage(sqsMsg);
        }
        );
        
    }
    
    public void doSendInBatch(List<Task> tasks) {
        
        List <SendMessageBatchRequestEntry> messageEntries = new LinkedList<>();
        Map<String, Task> taskMap = new HashedMap<>();
        tasks.stream().forEach(t -> {
            messageEntries.add(new SendMessageBatchRequestEntry()
                    .withId(t.getTaskId())
                    .withMessageBody(MiscUtil.toJson(t))
                    .withDelaySeconds(0)
                    ); 
            taskMap.put(t.getTaskId(), t);
        }
        );
        /*
        try {
            SendMessageBatchRequest sendMessageBatchRequest = 
                new SendMessageBatchRequest(taskConfig.getDispatchDestination(), messageEntries);
            SendMessageBatchResult result = sqs.sendMessageBatch(sendMessageBatchRequest);
            result.getFailed().forEach(f -> taskMap.get(f.getId()).setFailStatus(
                    TaskStatus.FAIL, f.getMessage())); 
        } catch(Throwable e) {
            //  only have the type of the exception and the error message
            tasks.stream().forEach(t -> t.setFailStatus(TaskStatus.FAIL, "Error was caught: "+e));
            logger.error("Fail send SQS", e);
            taskManagement.pollingError(e);
        }
        */
    }
    
    public void doSendInBatchPartition(List<Task> tasks) {
        // max 10 tasks per batch limitation
        List<List<Task>> partitionTasks = Lists.partition(tasks, 10);
        partitionTasks.stream().forEach(subset -> doSendInBatch(subset));
        
        
    }
    
}
