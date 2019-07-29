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
package com.asurion.ava.scheduler.dynoqueue.core.sync;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.TaskQueueSyncService;
import com.asurion.ava.scheduler.dynoqueue.redis.RedisQueuesProvider;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.spring.SpringContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/**
 * Utility services to Sync Redis queues
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.DyanoTaskQueueSync)
public class DynoTaskQueueSync implements TaskQueueSyncService {
    
    private final Logger logger = LoggerFactory.getLogger(DynoTaskQueueSync.class);
    
    private final TaskSchedulerConfig taskSchedulerConfig;
    @SuppressWarnings("unused")
    private final RedisQueuesProvider redisQueuesProvider;
    
    @SuppressWarnings("unused")
    private final int maxHashBuckets = 32;
    
    @Autowired
    public DynoTaskQueueSync(
            TaskSchedulerConfig taskSchedulerConfig,
            RedisQueuesProvider redisQueuesProvider) {
        
        this.taskSchedulerConfig = taskSchedulerConfig;
        this.redisQueuesProvider = redisQueuesProvider;
    }
    
    @Override
    public void sync(String src, String dest) {
        logger.info("Sync from {} to {}", src, dest); 
        
        Jedis jedis = null;
    
        try {
            // TBD
            String host =taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getHost();
            int port = taskSchedulerConfig.getDynoRedisTaskQueueConfig().getDataCenter().getPort();
            jedis = new Jedis(host, port);
            final Jedis jd = jedis;
            
            String srcKey = String.format("%s_*ava_task*", src);
            Set<String> keys = jedis.keys(srcKey);
            keys.stream().forEach(key -> {
                if(key.contains("QUEUE")) {
                    String srcTaskQueue = key;
                    String destTaskQueue = srcTaskQueue.replace(src, dest);
                    logger.info( srcTaskQueue + " ==> "+ destTaskQueue);
                    Set<Tuple> tasks = jd.zrangeWithScores(srcTaskQueue, 0, -1); 
                    tasks.stream().forEach(t -> {
                        String taskId = t.getElement();
                        double score = t.getScore();
                        logger.info(String.format("taskId:%s, score:%d", taskId, new Double(score).longValue()));
                        // Add task to destTaskQueue
                        jd.zadd(destTaskQueue, score, taskId);
                        // Remove task from srcTaskQueue
                        jd.zrem(srcTaskQueue, taskId);
                    });
                      
                } 
                else if(key.contains("MSG")) {
                    String srcHashBucket = key;
                    String destHashBucket = srcHashBucket.replace(src, dest);
                    logger.info( srcHashBucket + " ==> "+ destHashBucket);
                    Map<String, String> taskMsgs = jd.hgetAll(srcHashBucket);
                    taskMsgs.entrySet().stream().forEach(e -> { 
                        String taskId       = e.getKey();
                        String taskPayload  = e.getValue();
                        logger.info(String.format("taskId:%s, payload: %s", taskId, taskPayload));
                        // Add task payload to dest hashbucket
                        jd.hset(destHashBucket, taskId, taskPayload);
                        // Remove task payload form src hashbucket
                        jd.hdel(srcHashBucket, taskId);
                    });
                    
                    
                }
                
            });
            

        } finally {
            if(jedis!=null) {
                jedis.close();
            }
        }
        
    }
    
    public static void main(String[] args) {
        
        DynoTaskQueueSync dynoTaskQueueSync = SpringContext.getBean(BeanName.DyanoTaskQueueSync, DynoTaskQueueSync.class);
        
        
        //dynoTaskQueueSync.sync("dev", "dev-B");
        dynoTaskQueueSync.sync("dev-B", "dev");
        
    }
    
    
    
    

}
