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
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;


public class SQSConsumer {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("********* SQS Consumer Running *********");
		
		// TODO configure SQS URL dispatched tasks are consumed by test "SQSConsumer"
		String queueUrl = "https://sqs-url";
		
		
		//final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion("US_EAST_1").build();
		
		final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		// Long polling 20 seconds
		SetQueueAttributesRequest set_attrs_request = new SetQueueAttributesRequest()
		        .withQueueUrl(queueUrl)
		        .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");
		sqs.setQueueAttributes(set_attrs_request);
		
		
		
		while(true) {
			
			
			try {
				
				List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
				
				for (Message m : messages) {
					SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
					String current_time_str = time_formatter.format(System.currentTimeMillis());
					
					
					System.out.println( "[Recv at "+ current_time_str + "]: "+ m.getBody());
				    sqs.deleteMessage(queueUrl, m.getReceiptHandle());
				}
				
				
				
				
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	

}
