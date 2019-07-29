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

package com.asurion.ava.scheduler.servlet;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.core.TaskScheduler;
import com.asurion.ava.scheduler.core.TaskSchedulerService;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * HTTP Servlet handles 'schedule task' API call.
 * 
 * @author scott.cheng
 *
 */
@WebServlet(
        urlPatterns = "/v1/task/schedule"
        )
public class TaskScheduleApiServlet extends AbstractApiServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduleApiServlet.class);
    
    private TaskSchedulerService taskSchedulerService = TaskSchedulerService.getInstance();
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String postData = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        
        try {
            Task task = Task.getTask(postData);
            if(task.getTaskId()!=null) {
                writeResponse("taskId should be set\n"+ postData, HttpServletResponse.SC_BAD_REQUEST, response);
            }
            TaskScheduler taskScheduler = taskSchedulerService.getTaskScheduler(task);
            if(taskScheduler!=null) {
                task = taskSchedulerService.getTaskScheduler(task).schedule(task);
                writeResponse(MiscUtil.toJson(task), HttpServletResponse.SC_CREATED, response);
            } else {
                writeResponse("No scheduler found for\n"+ postData, HttpServletResponse.SC_BAD_REQUEST, response);
            }
        }  catch(Exception e) {
            handleError("Fail to schedule task for:\n "+ postData, e, response);
        }
    }
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
        

}
