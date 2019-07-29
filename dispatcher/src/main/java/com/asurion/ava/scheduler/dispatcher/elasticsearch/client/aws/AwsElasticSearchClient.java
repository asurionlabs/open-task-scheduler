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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.cloudformation.model.Output;
import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.ElasticSearchClient;
import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.ElasticSearchRequest;
import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.ElasticSearchRequest.HTTP_METHOD;
import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.ElasticSearchResponse;

/**
 *  Access AWS ElasticSearch service by AWS SDK with AWS credential. 
 *  
 * @author scott.cheng
 *
 */
public class AwsElasticSearchClient implements ElasticSearchClient {
    
    private static final Logger logger = Logger.getLogger(AwsElasticSearchClient.class);
    
    private static final AwsElasticSearchClient instance = new AwsElasticSearchClient();
    
    private AwsElasticSearchClient() {
        //System.getProperties().setProperty("com.amazonaws.sdk.disableCertChecking", "true");
    }
    
    @Override
    public ElasticSearchResponse send(ElasticSearchRequest esRequest) throws Exception {
        
        if(esRequest.getHttpMethod() == HTTP_METHOD.GET) {
            return sendByGet(esRequest);
        }
        if(esRequest.getHttpMethod() == HTTP_METHOD.DELETE) {
            return doSend(esRequest, HttpMethodName.DELETE);
        }
        
        
        
        
        final String fullEndpoint = esRequest.getUrl();
        //Instantiate the request
        Request<Void> request = new DefaultRequest<>("es"); //?!?
        request.setHttpMethod(HttpMethodName.POST);
        
        request.setEndpoint(URI.create(fullEndpoint));
        // *NOTE* - path cannot be part of endpoint otherwise it will be encoded again.
        request.setResourcePath(esRequest.getPath());
        request.addHeader("Content-Type", "application/json");
        
        InputStream stream = new ByteArrayInputStream(esRequest.getJsonBody().getBytes(StandardCharsets.UTF_8));
        request.setContent(stream);

        Map<String, List<String>> params = new HashMap<>();
        //params.put("from", Arrays.asList(String.valueOf(from)));
        //params.put("size", Arrays.asList(String.valueOf(0)));
        //params.put("source", Arrays.asList(source));
        for(String key: esRequest.getQueryParams().keySet()) {
            params.put(key, Arrays.asList(esRequest.getQueryParam(key)));
        }
        
        request.setParameters(params);

        //Sign it...
        AWS4Signer signer = new AWS4Signer(); //?!?
        //TODO remove hardcode regionName
        
        
        //signer.setRegionName(Configuration.getInstance().getAwsRegion());
        signer.setRegionName("us-east-1");
        signer.setServiceName(request.getServiceName());    
        signer.sign(request, new DefaultAWSCredentialsProviderChain().getCredentials());

        final ElasticSearchResponse esResponse = new ElasticSearchResponse(esRequest);
        
        //Execute it and get the response...?!?
        @SuppressWarnings("unused")
        Response<Output> rsp = new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true)) //?!?
                .request(request)
                .errorResponseHandler(new MyErrorHandler(esResponse))
                .execute(new HttpResponseHandler<Output>() {
                    @Override
                    public Output handle(HttpResponse response) throws Exception {
                        int status =  response.getStatusCode();
                        String body = getStringFromInputStream(response.getContent());
                        logger.debug(String.format("HTTP %d,  %s", status, body));
                                                
                        esResponse.setStatus(status);
                        esResponse.setJsonBody(body);
                        
                        return null;
                    }

                    @Override
                    public boolean needsConnectionLeftOpen() {
                        return false;
                    }
                });
        
        
        return esResponse;
    }
    
    //@Override
    public ElasticSearchResponse sendByGet(ElasticSearchRequest esRequest) throws Exception {
        
        final String fullEndpoint = esRequest.getUrl();
        //Instantiate the request
        Request<Void> request = new DefaultRequest<>("es"); //?!?
        request.setHttpMethod(HttpMethodName.GET);
        
        request.setEndpoint(URI.create(fullEndpoint));
        // *NOTE* - path cannot be part of endpoint otherwise it will be encoded again.
        request.setResourcePath(esRequest.getPath());
        request.addHeader("Content-Type", "application/json");
        
        Map<String, List<String>> params = new HashMap<>();
        //params.put("from", Arrays.asList(String.valueOf(from)));
        //params.put("size", Arrays.asList(String.valueOf(0)));
        //params.put("source", Arrays.asList(source));
        for(String key: esRequest.getQueryParams().keySet()) {
            params.put(key, Arrays.asList(esRequest.getQueryParam(key)));
        }
        params.put("source", Arrays.asList(esRequest.getJsonBody()));
        
        request.setParameters(params);

        //Sign it...
        AWS4Signer signer = new AWS4Signer(); //?!?
        //TODO remove hardcode regionName
        
        
        //signer.setRegionName(Configuration.getInstance().getAwsRegion());
        signer.setRegionName("us-east-1");
        signer.setServiceName(request.getServiceName());    
        signer.sign(request, new DefaultAWSCredentialsProviderChain().getCredentials());

        final ElasticSearchResponse esResponse = new ElasticSearchResponse(esRequest);
        
        //Execute it and get the response...?!?
        @SuppressWarnings("unused")
        Response<Output> rsp = new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true)) //?!?
                .request(request)
                .errorResponseHandler(new MyErrorHandler(esResponse))
                .execute(new HttpResponseHandler<Output>() {
                    @Override
                    public Output handle(HttpResponse response) throws Exception {
                        int status =  response.getStatusCode();
                        String body = getStringFromInputStream(response.getContent());
                        logger.debug(String.format("HTTP %d,  %s", status, body));
                                                
                        esResponse.setStatus(status);
                        esResponse.setJsonBody(body);
                        
                        return null;
                    }

                    @Override
                    public boolean needsConnectionLeftOpen() {
                        return false;
                    }
                });
        
        
        return esResponse;
    }
    
  //@Override
    public ElasticSearchResponse doSend(ElasticSearchRequest esRequest, HttpMethodName method) throws Exception {
        
        final String fullEndpoint = esRequest.getUrl();
        //Instantiate the request
        Request<Void> request = new DefaultRequest<>("es"); //?!?
        request.setHttpMethod(method);
        
        request.setEndpoint(URI.create(fullEndpoint));
        // *NOTE* - path cannot be part of endpoint otherwise it will be encoded again.
        request.setResourcePath(esRequest.getPath());
        //request.addHeader("Content-Type", "application/json");
        
        Map<String, List<String>> params = new HashMap<>();
        //params.put("from", Arrays.asList(String.valueOf(from)));
        //params.put("size", Arrays.asList(String.valueOf(0)));
        //params.put("source", Arrays.asList(source));
        for(String key: esRequest.getQueryParams().keySet()) {
            params.put(key, Arrays.asList(esRequest.getQueryParam(key)));
        }
        //params.put("source", Arrays.asList(esRequest.getJsonBody()));
        
        request.setParameters(params);

        //Sign it...
        AWS4Signer signer = new AWS4Signer(); //?!?
        //TODO remove hardcode regionName
        
        
        //signer.setRegionName(Configuration.getInstance().getAwsRegion());
        signer.setRegionName("us-east-1");
        signer.setServiceName(request.getServiceName());    
        signer.sign(request, new DefaultAWSCredentialsProviderChain().getCredentials());

        final ElasticSearchResponse esResponse = new ElasticSearchResponse(esRequest);
        
        //Execute it and get the response...?!?
        @SuppressWarnings("unused")
        Response<Output> rsp = new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true)) //?!?
                .request(request)
                .errorResponseHandler(new MyErrorHandler(esResponse))
                .execute(new HttpResponseHandler<Output>() {
                    @Override
                    public Output handle(HttpResponse response) throws Exception {
                        int status =  response.getStatusCode();
                        String body = getStringFromInputStream(response.getContent());
                        logger.debug(String.format("HTTP %d,  %s", status, body));
                                                
                        esResponse.setStatus(status);
                        esResponse.setJsonBody(body);
                        
                        return null;
                    }

                    @Override
                    public boolean needsConnectionLeftOpen() {
                        return false;
                    }
                });
        
        
        return esResponse;
    }
    
    
    private static class MyErrorHandler implements HttpResponseHandler<AmazonServiceException> {
        ElasticSearchResponse esResponse;
        
        @SuppressWarnings("unused")
        MyErrorHandler() {
            esResponse = null;
        }
        MyErrorHandler(ElasticSearchResponse esResponse) {
            this.esResponse = esResponse;
        }
        
        @Override
        public AmazonServiceException handle(
                com.amazonaws.http.HttpResponse response) throws Exception {
            int status =  response.getStatusCode();
            String body = getStringFromInputStream(response.getContent());
            System.out.println(String.format("HTTP %d,  %s", status, body));
            
            esResponse.setStatus(status);
            esResponse.setJsonBody(body);
            
            /*
            InputStream responseStream = response.getContent();
            String responseString = convertStreamToString(responseStream);

            AmazonServiceException ase
            = new AmazonServiceException(responseString);
            ase.setStatusCode(response.getStatusCode());
            ase.setErrorCode(response.getStatusText());
            return ase;
            */
            
            return null;
            
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
    
    public static AwsElasticSearchClient getInstance() {
        return instance;
    }
    
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
       
        String encodedId = URLEncoder.encode("messaging%2Fmms%2Fcannot+send-receive%2FAndroid_1");
        encodedId = URLEncoder.encode(encodedId);
        
        AwsElasticSearchRequest req = new AwsElasticSearchRequest(AwsElasticSearchRequest.ES_DEV_URL
                //, "/scott_test/flow/"+ "messaging%252Fmms%252Fcannot%2Bsend-receive%252FAndroid_1"
                ,"/scott_test/flow/messaging%2Fmms%2Fcannot+send-receive%2FAndroid_1"
                //,"/scott_test/flow/"+ encodedId
                //, "/scott_test/flow/test"
                , "{\"test\": \"foo\"}");
        
        AwsElasticSearchClient client = new AwsElasticSearchClient();
        
        client.send(req);
        
    }
}
