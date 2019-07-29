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

package com.asurion.ava.scheduler.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Servlet filter checks HTTP 'x-api-key' header.
 * By default, there's no API key checking. To enable API key check:
 * 1) Set checkAPIKey = true
 * 2) Implement validateAPIKey() method 
 *  
 * @author scott.cheng
 *
 */
@WebFilter(filterName = "TaskSchedulerApiSecurityFilter",
urlPatterns = {"/v1/task/schedule", "/v1/task/cancel"}
        )
public class SecurityFilter implements Filter {

    /** Logger */
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    private static final String API_KEY_PROPERTY = "x-api-key";
    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        if(checkAPIKey) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            String authCode = httpServletRequest.getHeader(API_KEY_PROPERTY);

            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            if(authCode==null || authCode.length()==0) {
                httpServletResponse.sendError(401, "Access denied for this resource");
                return;
            }

            if (!isValidateAPIKey(authCode)) {
                httpServletResponse.sendError(401, "Access denied for this resource");
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {


    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
    
    private boolean checkAPIKey = false;
    private boolean isValidateAPIKey(String key) {
        /**
         * Add your API Key validation here!
         */
        
        return false;
    }


}