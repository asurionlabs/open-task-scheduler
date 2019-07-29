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

package com.asurion.ava.scheduler.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import com.asurion.ava.scheduler.proxy.ProxyServlet;
import com.asurion.ava.scheduler.servlet.PingServlet;
import com.asurion.ava.scheduler.servlet.TaskCancelApiServlet;
import com.asurion.ava.scheduler.servlet.TaskScheduleApiServlet;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * Embeds Jetty server to allow Task Scheduler running as a standalone service.   
 * 
 * @author scott.cheng
 *
 */
public class TaskSchedulerJettyStandalone {


    private Server server;

    private TaskSchedulerJettyStandalone() {

        server = new Server(8080);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        
        handler.addServletWithMapping(PingServlet.class, "/open-scheduler/ping");
        handler.addServletWithMapping(TaskScheduleApiServlet.class, "/open-scheduler/v1/task/schedule");
        handler.addServletWithMapping(TaskCancelApiServlet.class, "/open-scheduler/v1/task/cancel");
        handler.addServletWithMapping(ProxyServlet.class, "/open-scheduler/jmx/*");

    }
    
    public void start() throws Exception {
        TaskSchedulerMain.start();
        server.start();
        server.join();
    }
    
    public void stop() throws Exception {
        TaskSchedulerMain.stop();
        server.stop();
    }
    
    public static void main(String[] args) throws Exception {
        TaskSchedulerJettyStandalone taskSchedulerJettyStandalone = new TaskSchedulerJettyStandalone();
        
        taskSchedulerJettyStandalone.start();
        
        
    }
    
    

}
