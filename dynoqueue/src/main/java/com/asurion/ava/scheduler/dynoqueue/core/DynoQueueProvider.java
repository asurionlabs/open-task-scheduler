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
package com.asurion.ava.scheduler.dynoqueue.core;

import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;
import com.netflix.dyno.queues.DynoQueue;

/**
 * Provides facgtory method to build dyno queue object by queue name.
 * 'getQueue()' is called when the scheduler starts.
 * 
 * @author scott.cheng
 */
public interface DynoQueueProvider {
	
	public DynoQueue getQueue(String queueName);
	
	public static DynoQueueProvider get() {
		return getRedisQueuesProvider();
	}
	
	public static DynoQueueProvider getRedisQueuesProvider() {
		return SpringContext.getBean(BeanName.RedisQueuesProvider, DynoQueueProvider.class);
	}
	
	
}
