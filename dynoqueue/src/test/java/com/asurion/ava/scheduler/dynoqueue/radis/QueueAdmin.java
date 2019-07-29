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
package com.asurion.ava.scheduler.dynoqueue.radis;

import com.asurion.ava.scheduler.core.RedisConnectionPool;
import com.asurion.ava.scheduler.dynoqueue.core.DynoQueueProvider;
import com.netflix.dyno.queues.DynoQueue;

public class QueueAdmin {

    public static void main(String[] args) throws Exception {
        // clear queue
       
        RedisConnectionPool.getInstance().start();
        
        DynoQueueProvider provider = DynoQueueProvider.getRedisQueuesProvider();
        // TODO config queue name below
        String queueName = "task_queue_name";
        
        DynoQueue queue = provider.getQueue(queueName);
    
        System.out.println(String.format("%s has %d tasks", queueName, queue.size()));
        // Clear the queue!!!
        queue.clear();
        
        System.out.println(String.format("After clear queue - %s has %d tasks", queueName, queue.size()));
        
        
    }
    
    
}
