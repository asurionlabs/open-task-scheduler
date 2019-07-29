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
package com.asurion.ava.scheduler.core.jmx.mbeans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Task scheduler MBean implementation class.
 * 
 * @author scott.cheng
 *
 */
@Component
public class TaskManagement implements TaskManagementMBean {

    private final Logger logger = LoggerFactory.getLogger(TaskManagement.class);
    
    private AtomicLong totalNumTasksPolled      = new AtomicLong(0);
    private AtomicLong totalNumTasksDispatched  = new AtomicLong(0);
    private AtomicLong totalNumTasksScheduled   = new AtomicLong(0);
    
    public long getTotalNumTasksPolled() {
        return totalNumTasksPolled.get();
    }
    
    public void incrementTotalNumTasksPolled(int n) {
        totalNumTasksPolled.addAndGet(n);
        
    }
    
    @Override
    public long getTotalNumTasksDispatched() {   
       return totalNumTasksDispatched.get();
        
    }
    
    public void incrementTasksDispatched() {
        totalNumTasksDispatched.addAndGet(1);
       
    }
    
    public void incrementTasksDispatched(int n) {
        totalNumTasksDispatched.addAndGet(n);
       
    }
    
    @Override
    public long getTotalNumTasksScheduled() {
        return totalNumTasksScheduled.get();
    }
    
    public void incrementTasksScheduled(int n) {
        totalNumTasksScheduled.addAndGet(n);
        
    }
    public void incrementTasksScheduled() {
        totalNumTasksScheduled.incrementAndGet();
        
    }
    
    //------------------------------------------------------------------------------
    //
    private static class LruLinkedHashMap<K,V> extends LinkedHashMap {
        
        LruLinkedHashMap() {
            super(10);
        }
        
        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
            return super.size()>=11;
        }
    }
    
    
    private LinkedHashMap<Date, Throwable> last10ErrorPolling = new LruLinkedHashMap<>();
    
    private LinkedHashMap<Date, Throwable> last10ErrorScheduling = new LruLinkedHashMap<>();
    
    public void pollingError(Throwable e) {
        last10ErrorPolling.put(new Date(), e);
    }
    
    public void SchedulingError(Throwable e) {
        last10ErrorScheduling.put(new Date(), e);
    }
    
    @Override
    public LinkedHashMap<Date, Throwable> last10ErrorPolling() {
        return last10ErrorPolling;
    }
    
    @Override
    public LinkedHashMap<Date, Throwable> last10ErrorScheduling() {
        return last10ErrorScheduling;
    }
    
    //---------------------------------------------------------------------------------
    
    private LinkedHashMap<String, String> pollingStatus = new LruLinkedHashMap<>();
    
    public void pollingStatus(String scheduler, String status) {
            
        SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        
        String currentTime = time_formatter.format(System.currentTimeMillis());
        
        
        pollingStatus.put(scheduler, String.format("[%s] %s", currentTime, status));
        
    }
    
    @Override
    public String getPollingAndQueueStatus() {
        //return pollingStatus;
        StringBuilder sb = new StringBuilder();
        pollingStatus.forEach((k,v)->sb.append(String.format("[%s: %s]\n", k, v)));
        
        return sb.toString();
    }
}
