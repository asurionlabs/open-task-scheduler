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
 */package com.asurion.ava.scheduler.dispatcher.elasticsearch.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ElasticSearchRequest {
    
    public enum HTTP_METHOD {
        POST
        , GET
        , DELETE
    }
    
    private  String url;
    private  String uriParam;
    private  String jsonBody;
    private  HTTP_METHOD httpMethod = HTTP_METHOD.POST;
    
    private Map<String, String> queryParams = new HashMap<String, String>();
    
    public ElasticSearchRequest() {
        
    }
    public ElasticSearchRequest(String url, String uriParam, String jsonBody) {
        super();
        this.url = url;
        this.uriParam = uriParam;
        this.jsonBody = jsonBody;
    }
    
    
    
    @SuppressWarnings("deprecation")
    public String getUrl() throws UnsupportedEncodingException {
        return String.format("%s?uri=%s", url,  URLEncoder.encode(uriParam));
    }
    
    public String getJsonBody() {
        return jsonBody;
    }
    
    public String getPath() {
        return null;
    }
    
    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
        
    }
    
    public String getQueryParam(String key) {
        return queryParams.get(key);
    }
    
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
    
    public void setHttpMethod(HTTP_METHOD httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    public HTTP_METHOD getHttpMethod() {
        return httpMethod;
    }
    
}
