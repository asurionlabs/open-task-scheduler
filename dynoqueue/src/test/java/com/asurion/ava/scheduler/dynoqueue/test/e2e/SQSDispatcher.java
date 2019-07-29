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
package com.asurion.ava.scheduler.dynoqueue.test.e2e;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.asurion.ava.scheduler.dynoqueue.core.DynoQueueProvider;
import com.netflix.dyno.queues.DynoQueue;
import com.netflix.dyno.queues.Message;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSDispatcher {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("********* Queue Consumer Running *********");
		
		DynoQueueProvider provider = DynoQueueProvider.getRedisQueuesProvider();
		
		
		
		DynoQueue q = provider.getQueue("TEST");
		/*
		while(true) {
			
			int count = 10;
			List<Message> polled = q.pop(count, 1, TimeUnit.SECONDS);	
			//System.out.println(polled);
			if(polled!=null && !polled.isEmpty()) {
				for(Message m: polled) {
					System.out.println( "Recv ==> "+ m.getPayload());
					q.ack(m.getId());
				}
			}
			
			
			// sleep 10 seconds
			Thread.sleep(10 * 1000);
		}
		*/
		// TODO configure SQS URL to disparch tasks to SQS
		String queueUrl = "https://sqs-url";
		
		
		final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion("US_EAST_1").build();
		
		while(true) {
			
			int count = 1;
			//List<Message> polled = q.pop(count, 60, TimeUnit.SECONDS);
			try {
				//System.out.println("Polling...");
				List<Message> polled = q.pop(count, 100, TimeUnit.MILLISECONDS);
				//System.out.println(polled);
				long ts = System.currentTimeMillis();

				SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
				String current_time_str = time_formatter.format(System.currentTimeMillis());

				
				

				if(polled!=null && !polled.isEmpty()) {
					for(Message m: polled) {
						//System.out.println( "[Recv at "+ current_time_str + "]: "+ m.getPayload());
						q.ack(m.getId());
						
						
						// Dispatch to SQS
						SendMessageRequest send_msg_request = new SendMessageRequest()
						        .withQueueUrl(queueUrl)
						        .withMessageBody(m.getPayload())
						        .withDelaySeconds(0);
						
						sqs.sendMessage(send_msg_request);
						
					}
				}
				
				
				
				
				
				
				
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	

}
