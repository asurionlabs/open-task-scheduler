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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.asurion.ava.scheduler.dynoqueue.core.DynoQueueProvider;
import com.netflix.dyno.queues.DynoQueue;
import com.netflix.dyno.queues.Message;

public class Producer {
	
	static AtomicLong ct = new AtomicLong(0L);
	
	public static void main(String[] args) throws Exception {
		
		
		//DelayQueue delayQueue = DelayQueue.getInstance();
		DynoQueueProvider provider = DynoQueueProvider.getRedisQueuesProvider();
		
		
		
		DynoQueue q = provider.getQueue("TEST");
		while(true) {
			/*
			long ts = System.currentTimeMillis();
			SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
			String current_time_str = time_formatter.format(System.currentTimeMillis());
			
			Message msg = new Message(String.valueOf(ts), "message -" + current_time_str);
			//msg.setTimeout(60, TimeUnit.SECONDS);
			msg.setTimeout(10, TimeUnit.SECONDS);
			*/
			List<Message> msgs = new ArrayList<>();
			// 0 second delay
			msgs.add(buildTask(0));
			
			// 60 second delay
			msgs.add(buildTask(60));
			
			// 20 min delay - 60* 20
			//msgs.add(buildTask(60*20));
			
			q.push(msgs);
			
			// sleep 10 seconds
			Thread.sleep(10 * 1000);
		}
		
	}
	
	private static Message buildTask(long delay) {
		long ts = System.currentTimeMillis();
		SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		
		String current_time_str = time_formatter.format(System.currentTimeMillis());
		
		String payload =  "message -" + current_time_str + " [dely "+  delay + " second(s)]";
		
		System.out.println(payload);
		
		Message msg = new Message(String.valueOf(ts)+"_"+ct.incrementAndGet(), payload);
		
		if(delay >0) {
			msg.setTimeout(delay, TimeUnit.SECONDS);
		}
		
		return msg;
	}
		
}
