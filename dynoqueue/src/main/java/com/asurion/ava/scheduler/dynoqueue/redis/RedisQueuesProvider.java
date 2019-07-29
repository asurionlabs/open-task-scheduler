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
package com.asurion.ava.scheduler.dynoqueue.redis;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskConfig;
import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.dynoqueue.core.DynoQueueProvider;
import com.asurion.ava.scheduler.spring.BeanName;
import com.netflix.dyno.connectionpool.Host;
import com.netflix.dyno.queues.DynoQueue;
import com.netflix.dyno.queues.redis.RedisDynoQueue;
import com.netflix.dyno.queues.redis.v2.QueueBuilder;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Queue provider implementation class in providing Dyno Queue.
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.RedisQueuesProvider)
public class RedisQueuesProvider implements DynoQueueProvider {
	private final Logger logger = LoggerFactory.getLogger(RedisDynoQueue.class);
	
	private final TaskSchedulerConfig taskSchedulerConfig;
	
	public void initialize(TaskConfig taskConfig) {
	    
	}
	//private final RedisQueues queues;
	
	@Autowired
	public RedisQueuesProvider(TaskSchedulerConfig taskSchedulerConfig) {
		this.taskSchedulerConfig = taskSchedulerConfig;
		
		logger.debug(taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getHost());
		
		
		
	}
	
	public DynoQueue getQueue(String queueName) {
		
		List<Host> hosts = new LinkedList<>();
		
		hosts.add(new Host(taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getHost()
					, taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getPort()
					, taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getRack()));
		
			
			
		JedisPoolConfig config = new JedisPoolConfig();
		config.setTestOnBorrow(true);
		config.setTestOnCreate(true);
		config.setMaxTotal(10);
		config.setMaxIdle(5);
		config.setMaxWaitMillis(60_000);	
		// Custom RedisDynoQueueBuilder
		QueueBuilder qb = new RedisDynoQueueBuilder();
		
		DynoQueue queue = qb
				.setCurrentShard("a")			
				.setHostToShardMap((Host h) -> h.getRack().substring(h.getRack().length()-1))
				.setQueueName(queueName)
				.setRedisKeyPrefix(taskSchedulerConfig.getDynoRedisTaskQueueConfig().getQueueConfig().getPrefix())
				.setUnackTime(taskSchedulerConfig.getDynoRedisTaskQueueConfig().getQueueConfig().getAckTimeout())
				.useNonDynomiteRedis(config, hosts)
				.build();
		
		return queue;
		
	}
	
	
}
