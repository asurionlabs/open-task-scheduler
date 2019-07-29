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
package com.asurion.ava.scheduler.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.util.MiscUtil;

/**
 * Test Status:
 *      RepeatCount     NumContTasks    TPS      Total
 * ----------------------------------------------------
 *      1000            10              10       10K
 *      100             100             100      10K
 */


public class TaskClient {

    public static void main(String[] args) throws Exception {
        
        String json = "{\"type\":\"claim_status\",\"partner\":\"\",\"context\":\"claims\",\"delayInSecond\":1,\"repeatCount\":1000,\"sessionId\":\"abc\",\"mdn\":\"\",\"claimId\":\"\"}";
        
        int numConcurrentTasks = 100;
        
        long repeatCount=100;
        
        String testFor= "10K - 100TPS";
        
        String testTitle = "test-"+ testFor;
        
        for(int i =0; i<numConcurrentTasks; ++i) {

            Task task = MiscUtil.toPojo(json, Task.class);
            
            task.setRepeatCount(repeatCount);
            String testId = String.format("%s-%d", testTitle, i);
            //task.setClaimId(testId);
            //task.setSessionId(testId);
            //task.setMdn(testId);
            //System.out.println(MiscUtil.toJson(task));
            
            
            CloseableHttpClient client = HttpClients.createDefault();
            // TODO add your url here
            String url = "scheduler-url";
            
            //url = "http://localhost:8080/AvaTaskScheduler-1.0/v1/task/schedule";
            
            
            HttpPost httpPost = new HttpPost(url);


            StringEntity entity = new StringEntity(MiscUtil.toJson(task));
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            CloseableHttpResponse response = client.execute(httpPost);

            System.out.println("HTTP Status: "+ response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            //System.out.println(String.format("HTTP Status:%d \n\n"+ response.getStatusLine().getStatusCode()));
            System.out.println(result);

            client.close();
            

        }
    }
    
    
}
