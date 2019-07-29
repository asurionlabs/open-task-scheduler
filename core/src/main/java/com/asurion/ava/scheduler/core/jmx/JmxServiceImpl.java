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
package com.asurion.ava.scheduler.core.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.core.jmx.mbeans.TaskManagement;
import com.asurion.ava.scheduler.spring.BeanName;
import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * Starts/Stops JMX HtmlAdaptorServer so that JMX can be accessed through HTML/HTTP.
 * 
 * @author scott.cheng
 *
 */
@Component(BeanName.JmxService)
public class JmxServiceImpl implements JmxService {

    private HtmlAdaptorServer htmlAdaptor;
    
    private TaskManagement taskManagement;
    @Autowired
    public JmxServiceImpl(TaskManagement taskManagement) {
       this.taskManagement = taskManagement;
    }
    
    @Override
    public void start() throws Exception {
        startHtmlAdaptor();
        
        MBeanServer mbeanServer = 
                ManagementFactory.getPlatformMBeanServer();
        
        ObjectName name = new ObjectName("AvaTaskScheduler:name=TaskManagement");
        mbeanServer.registerMBean(taskManagement, name);
        
    }
    
    @Override
    public void stop() throws Exception {
        stopHtmlAdaptor();
        
        MBeanServer mbeanServer = 
                ManagementFactory.getPlatformMBeanServer();
        
        //mbeanServer.unregisterMBean(new ObjectName("Server:name=HtmlAdaptorServer"));
        mbeanServer.unregisterMBean(new ObjectName("AvaTaskScheduler:name=TaskManagement"));
    }
    

    private void startHtmlAdaptor() throws Exception {

        MBeanServer mbeanServer = 
                ManagementFactory.getPlatformMBeanServer();

        htmlAdaptor = new HtmlAdaptorServer(8989);
        ObjectName name = new ObjectName("Server:name=HtmlAdaptorServer");
        mbeanServer.registerMBean(htmlAdaptor, name);
        htmlAdaptor.start();
        
    }

    public void stopHtmlAdaptor() throws Exception {
        
        htmlAdaptor.stop();
        
        int ct =0;
        while(htmlAdaptor.isActive()) {
            Thread.sleep(100);
            if(ct++<5) {
                break;
            }
        }
    }





}
