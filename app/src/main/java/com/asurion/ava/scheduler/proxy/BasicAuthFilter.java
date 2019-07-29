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

package com.asurion.ava.scheduler.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs HTTP Basic Authentication check for JMX HTML/HTTP access.
 * 
 * @author scott.cheng
 * 
 * Modify original code - 
 * Created by kemanson on 12/02/14.
 */

@WebFilter(filterName = "JmxBasicAuthFilter",
urlPatterns = {"/jmx/*"},
initParams = {
    @WebInitParam(name = "username", value = "jmx"),
    @WebInitParam(name = "password", value = "jmx")})
    
public class BasicAuthFilter implements Filter {

  /** Logger */
  private static final Logger LOG = LoggerFactory.getLogger(BasicAuthFilter.class);

  private String username = "";

  private String password = "";

  private String realm = "Protected";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    username = filterConfig.getInitParameter("username");
    password = filterConfig.getInitParameter("password");
    String paramRealm = filterConfig.getInitParameter("realm");
    realm = paramRealm;
    
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null) {
      StringTokenizer st = new StringTokenizer(authHeader);
      if (st.hasMoreTokens()) {
        String basic = st.nextToken();

        if (basic.equalsIgnoreCase("Basic")) {
          try {
            String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
            LOG.debug("Credentials: " + credentials);
            int p = credentials.indexOf(":");
            if (p != -1) {
              String _username = credentials.substring(0, p).trim();
              String _password = credentials.substring(p + 1).trim();

              if (!username.equals(_username) || !password.equals(_password)) {
                unauthorized(response, "Bad credentials");
              }

              filterChain.doFilter(servletRequest, servletResponse);
            } else {
              unauthorized(response, "Invalid authentication token");
            }
          } catch (UnsupportedEncodingException e) {
            throw new Error("Couldn't retrieve authentication", e);
          }
        }
      }
    } else {
      unauthorized(response);
    }
  }

  @Override
  public void destroy() {
  }

  private void unauthorized(HttpServletResponse response, String message) throws IOException {
    response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
    response.sendError(401, message);
  }

  private void unauthorized(HttpServletResponse response) throws IOException {
    unauthorized(response, "Unauthorized");
  }

}