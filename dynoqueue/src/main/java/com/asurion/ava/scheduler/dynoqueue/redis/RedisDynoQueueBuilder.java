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

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asurion.ava.scheduler.core.RedisConnectionPool;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.AmazonInfo.MetaDataKey;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.dyno.connectionpool.Host;
import com.netflix.dyno.connectionpool.HostSupplier;
import com.netflix.dyno.jedis.DynoJedisClient;
import com.netflix.dyno.queues.DynoQueue;
import com.netflix.dyno.queues.redis.conn.DynoClientProxy;
import com.netflix.dyno.queues.redis.conn.JedisProxy;
import com.netflix.dyno.queues.redis.conn.RedisConnection;
import com.netflix.dyno.queues.redis.v2.MultiRedisQueue;
import com.netflix.dyno.queues.redis.v2.QueueBuilder;
import com.netflix.dyno.queues.redis.v2.RedisPipelineQueue;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Override Queue Builder (originally from Netflix Dynno Queue) to provide Redis 
 * connection pool to access Redis.
 * 
 * @author scott.cheng
 *
 */
public class RedisDynoQueueBuilder extends QueueBuilder {

    
    private Clock clock;

    private String queueName;

    private String redisKeyPrefix;

    private int unackTime;

    private String currentShard;

    private Function<Host, String> hostToShardMap;

    private HostSupplier hs;

    private Collection<Host> hosts;

    @SuppressWarnings("unused")
    private JedisPoolConfig redisPoolConfig;

    private DynoJedisClient dynoQuorumClient;

    private DynoJedisClient dynoNonQuorumClient;

    /**
     * @param clock the Clock instance to set
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder setClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    /**
     * @param queueName the queueName to set
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    /**
     * @param redisKeyPrefix Prefix used for all the keys in Redis
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
        return this;
    }

    /**
     * @param redisPoolConfig
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder useNonDynomiteRedis(JedisPoolConfig redisPoolConfig, List<Host> redisHosts) {
        this.redisPoolConfig = redisPoolConfig;
        this.hosts = redisHosts;
        return this;
    }

    /**
     *
     * @param dynoQuorumClient
     * @param dynoNonQuorumClient
     * @param hs
     * @return
     */
    @Override
    public QueueBuilder useDynomite(DynoJedisClient dynoQuorumClient, DynoJedisClient dynoNonQuorumClient, HostSupplier hs) {
        this.dynoQuorumClient = dynoQuorumClient;
        this.dynoNonQuorumClient = dynoNonQuorumClient;
        this.hs = hs;
        return this;
    }

    /**
     * @param hostToShardMap Mapping from a Host to a queue shard
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder setHostToShardMap(Function<Host, String> hostToShardMap) {
        this.hostToShardMap = hostToShardMap;
        return this;
    }

    /**
     * @param unackTime Time in millisecond, after which the uncked messages will be re-queued for the delivery
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder setUnackTime(int unackTime) {
        this.unackTime = unackTime;
        return this;
    }

    /**
     * @param currentShard Name of the current shard
     * @return instance of QueueBuilder
     */
    @Override
    public QueueBuilder setCurrentShard(String currentShard) {
        this.currentShard = currentShard;
        return this;
    }

    /**
     *
     * @return Build an instance of the queue with supplied parameters.
     * @see MultiRedisQueue
     * @see RedisPipelineQueue
     */
    @Override
    public DynoQueue build() {

        if (clock == null) {
            clock = Clock.systemDefaultZone();
        }

        boolean useDynomiteCluster = (dynoQuorumClient != null && hs != null);
        if(useDynomiteCluster) {
            this.hosts = hs.getHosts();
        }

        Map<String, Host> shardMap = new HashMap<>();
        for (Host host : hosts) {
            String shard = hostToShardMap.apply(host);
            shardMap.put(shard, host);
        }


        Map<String, RedisPipelineQueue> queues = new HashMap<>();

        for (String queueShard : shardMap.keySet()) {

            Host host = shardMap.get(queueShard);
            String hostAddress = host.getIpAddress();
            if (hostAddress == null || "".equals(hostAddress)) {
                hostAddress = host.getHostName();
            }
            RedisConnection redisConn = null;
            RedisConnection redisConnRead = null;

            if (useDynomiteCluster) {
                redisConn = new DynoClientProxy(dynoQuorumClient);
                redisConnRead = new DynoClientProxy(dynoNonQuorumClient);
            } else {
                //JedisPool pool = new JedisPool(redisPoolConfig, hostAddress, host.getPort(), 0);
                JedisPool pool = RedisConnectionPool.getInstance().getJedisPool();
                redisConn = new JedisProxy(pool);
                redisConnRead = new JedisProxy(pool);
            }

            RedisPipelineQueue q = new RedisPipelineQueue(clock, redisKeyPrefix, queueName, queueShard, unackTime, unackTime, redisConn);
            q.setNonQuorumPool(redisConnRead);

            queues.put(queueShard, q);
        }

        if (queues.size() == 1) {
            //This is a queue with a single shard
            return queues.values().iterator().next();
        }

        MultiRedisQueue queue = new MultiRedisQueue(queueName, currentShard, queues);
        return queue;
    }


    @SuppressWarnings("unused")
    private static List<Host> getHostsFromEureka(EurekaClient ec, String applicationName) {

        Application app = ec.getApplication(applicationName);
        List<Host> hosts = new ArrayList<Host>();

        if (app == null) {
            return hosts;
        }

        List<InstanceInfo> ins = app.getInstances();

        if (ins == null || ins.isEmpty()) {
            return hosts;
        }

        hosts = Lists.newArrayList(Collections2.transform(ins,

                new Function<InstanceInfo, Host>() {
                    @Override
                    public Host apply(InstanceInfo info) {

                        Host.Status status = info.getStatus() == InstanceStatus.UP ? Host.Status.Up : Host.Status.Down;
                        String rack = null;
                        if (info.getDataCenterInfo() instanceof AmazonInfo) {
                            AmazonInfo amazonInfo = (AmazonInfo) info.getDataCenterInfo();
                            rack = amazonInfo.get(MetaDataKey.availabilityZone);
                        }
                        Host host = new Host(info.getHostName(), info.getIPAddr(), rack, status);
                        return host;
                    }
                }));
        return hosts;
    }
    
    
    
    
}
