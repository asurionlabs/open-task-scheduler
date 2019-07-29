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
package com.asurion.ava.scheduler.dispatcher.elasticsearch.client.aws;

import java.io.UnsupportedEncodingException;

import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.ElasticSearchRequest;

/**
 * AWS Elasticsearch Request.
 * 
 * @author scott.cheng
 *
 */
public class AwsElasticSearchRequest extends ElasticSearchRequest {
	// TODO set your ES URL
    public static final String ES_DEV_URL = "ES-URL";
    
    private  String url;
    private  String jsonBody;
    private String path;
    
    public AwsElasticSearchRequest(String url, String path, String jsonBody) {
        this.url = url;
        this.path = path;
        this.jsonBody = jsonBody;

    }
    
    public String getUrl() throws UnsupportedEncodingException {
        return url;
    }
    
    public String getJsonBody() {
        return jsonBody;
    }
    
    public String getPath() {
        return path;
    }
    
    
    
    
}
