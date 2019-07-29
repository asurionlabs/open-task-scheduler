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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listens to the Servlet Context event 'init' & 'destroy' events to start & stop Task Scheduler.
 *  
 * @author scott.cheng
 *
 */
@WebListener
public class TaskSchedulerServletContextListener implements ServletContextListener {
 
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("****** Starting TaskScheduler ******");
        TaskSchedulerMain.start();
        System.out.println("****** TaskScheduler has been started ******");
    }
     
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Stopping TaskScheduler");
        TaskSchedulerMain.stop();
        System.out.println("****** TaskScheduler has been stopped ******");
    }
}
