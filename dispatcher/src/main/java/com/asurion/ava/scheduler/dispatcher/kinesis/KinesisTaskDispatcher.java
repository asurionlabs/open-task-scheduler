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
package com.asurion.ava.scheduler.dispatcher.kinesis;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.impl.AbstractTaskDispather;
import com.asurion.ava.scheduler.enums.TaskStatus;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * Sends tasks to AWS Kinesis stream.
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.KinesisTaskDispatcher)
@Scope("prototype")
public class KinesisTaskDispatcher extends AbstractTaskDispather {

    private static final Logger logger = LoggerFactory.getLogger(KinesisTaskDispatcher.class);

    private final AmazonKinesis kinesis;

    public KinesisTaskDispatcher() {
        kinesis = new AmazonKinesisClient().withRegion(Regions.US_EAST_1);
    }

    @Override
    public List<Task> dispatch(List<Task> tasks) {
        
        //logger.info("dispatch {} tasks to Kinesis stream {}", tasks.size(), taskConfig.getDispatchDestination());
        
        doSend(tasks);
        return tasks;

    }

    public void doSend(List<Task> tasks) {
        
        // batch all tasks
        try {
            PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
            String streamName=  "TBD";
            putRecordsRequest.setStreamName(streamName);   
            
            List <PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>(); 
            Map<String, Task> taskMap = new HashedMap<>();
            
            tasks.stream().forEach(t -> {

                PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
                putRecordsRequestEntry.setData(ByteBuffer.wrap(MiscUtil.toJson(t).getBytes()));
                // partitionKey taskId
                putRecordsRequestEntry.setPartitionKey(t.getTaskId());
                putRecordsRequestEntryList.add(putRecordsRequestEntry); 

                taskMap.put(t.getTaskId(), t);

            });
            
            putRecordsRequest.setRecords(putRecordsRequestEntryList);

            PutRecordsResult putRecordsResult = kinesis.putRecords(putRecordsRequest);

            final List<PutRecordsResultEntry> putRecordsResultEntryList = putRecordsResult.getRecords();
            for (int i = 0; i < putRecordsResultEntryList.size(); i++) {
                final PutRecordsRequestEntry putRecordRequestEntry = putRecordsRequestEntryList.get(i);
                final PutRecordsResultEntry putRecordsResultEntry = putRecordsResultEntryList.get(i);
                if (putRecordsResultEntry.getErrorCode() != null) {
                    taskMap.get(putRecordRequestEntry.getPartitionKey()).setFailStatus(
                            TaskStatus.FAIL, String.format("Fail send Kinesis - %s,%s", 
                                    putRecordsResultEntry.getErrorMessage(), putRecordsResultEntry.getErrorCode()));
                }
            }
        } catch(Exception e) {
            tasks.stream().forEach(t -> t.setFailStatus(TaskStatus.FAIL, "Error was caught: "+e));
            logger.error("Fail send Kinesis", e);
            taskManagement.pollingError(e);
        }
        

    }


}
