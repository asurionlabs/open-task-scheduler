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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.RedisConnectionPool;
import com.asurion.ava.scheduler.spring.BeanName;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis connection pool wrapper.
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.RedisConnectionPool)
public class RedisConnectionPoolImpl implements RedisConnectionPool {
    
    private final Logger logger = LoggerFactory.getLogger(RedisConnectionPoolImpl.class);
    private final TaskSchedulerConfig taskSchedulerConfig;
    
    private JedisPool pool;
    
    
    @Autowired
    public RedisConnectionPoolImpl(TaskSchedulerConfig taskSchedulerConfig) {
        this.taskSchedulerConfig = taskSchedulerConfig;
        
        
    }
    
    @Override
    public void start() {
        logger.info("start RedisConnectionPool");
     // TODO connection config
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(30_000);
        
        String host = taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getHost();
        int port = taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getPort();
        
        this.pool = new JedisPool(config, host, port, 0);
        
        
    }
    
    public JedisPool getJedisPool() {
        return pool;
    }
    
    @Override
    public void shutdown() {
        logger.info("shutdown RedisConnectionPool");
        pool.close();
        pool.destroy();
        
        
    }
    
    
    

}
