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
package com.asurion.ava.scheduler.core;

import java.util.List;

/**
 * To be implemented by the actual dispatcher such as SQSDispatcher, StdoutDispatcher, and etc.
 * 
 * When the task scheduler is loaded, the dispatcher chain will be built.
 * 
 * @author scott.cheng
 *
 */
public interface TaskDispatcher {

    public List<Task> dispatch(List<Task> tasks);
    
}
