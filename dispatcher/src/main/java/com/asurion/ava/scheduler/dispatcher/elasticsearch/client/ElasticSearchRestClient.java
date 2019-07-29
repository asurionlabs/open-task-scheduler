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

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.asurion.ava.adaptercore.security.SSLContextUtil;


/**
 * Access AWS ElasticSearch/Kibana via by ResteasyClient. 
 * 
 * @author scott.cheng
 *
 */
public class ElasticSearchRestClient implements ElasticSearchClient {

    private static final Logger logger = Logger.getLogger(ElasticSearchRestClient.class);

    public ElasticSearchResponse send(ElasticSearchRequest request)  throws Exception {
        ResteasyClient client = null;
        try {
            Response response = null;

            client = new ResteasyClientBuilder().socketTimeout(90, TimeUnit.SECONDS)
                    .sslContext(getSSLContext())
                    .hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY)
                    .build();

            WebTarget target = client.target(request.getUrl());                  
            response = setRequiredHeaders(target.request()).post(Entity.json(request.getJsonBody()));    
            response.bufferEntity();
            int status = response.getStatus();
            String body = response.readEntity(String.class);

            logger.debug("HTTP status: "+ status);

            logger.debug("HTTP body \n "+ body);

            return new ElasticSearchResponse(request, status, body);

        
        } finally {
            if(client!=null) {
                client.close();
            }
        }

    }

    private Invocation.Builder setRequiredHeaders(Invocation.Builder builder) {
        builder.header("Content-Type", "application/json");
        builder.header("kbn-version", "5.1.1");
        return builder;
    }


    private SSLContext getSSLContext() throws Exception {
        return SSLContextUtil.getInstance().getSSLContexByTrustAll();
    }



}
