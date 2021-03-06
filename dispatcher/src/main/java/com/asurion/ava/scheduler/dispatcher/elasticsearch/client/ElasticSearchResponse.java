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
package com.asurion.ava.scheduler.dispatcher.elasticsearch.client;

public class ElasticSearchResponse {

    private  final ElasticSearchRequest request;
    private  int status;
    private  String jsonBody;
    
    public ElasticSearchResponse(ElasticSearchRequest request, int status, String jsonBody) {
        super();
        this.request = request;
        this.status = status;
        this.jsonBody = jsonBody;
    }
    
    public ElasticSearchResponse(ElasticSearchRequest request) {
        super();
        this.request = request;
    }

    public ElasticSearchRequest getRequest() {
        return request;
    }

    public int getStatus() {
        return status;
    }

    public String getJsonBody() {
        return jsonBody;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @Override
    public String toString() {
        return String.format("[httpStatus=%d], %s", status, jsonBody);
    }
    
}
