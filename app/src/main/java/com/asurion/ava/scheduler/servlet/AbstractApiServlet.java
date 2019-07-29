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
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * Abstract Servlet class provides common methods:
 * 'writeResponse' to return JSON HTTP response
 * 
 * @author scott.cheng
 *
 */
public abstract class AbstractApiServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    void handleError(String s, Exception e, HttpServletResponse response) {
        getLogger().error(s, e);
        writeResponse(e.toString(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
    }

    protected abstract Logger getLogger();

    protected static void writeResponse(String body, int httpcode, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("application/json");
            response.setStatus(httpcode);
            out = response.getWriter();
            out.print(body);
        } catch(IOException e) {

        } finally {
            if(out!=null) {
                out.close();
            }
        }
    }
}
