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
package com.asurion.ava.scheduler.dispatcher.elasticsearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asurion.ava.scheduler.config.TaskSchedulerConfig;
import com.asurion.ava.scheduler.core.Task;
import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.aws.AwsElasticSearchClient;
import com.asurion.ava.scheduler.dispatcher.elasticsearch.client.aws.AwsElasticSearchRequest;
import com.asurion.ava.scheduler.spring.BeanName;
import com.asurion.ava.scheduler.util.MiscUtil;

@Component(BeanName.ElasticSearchLogger)
public class ElasticSearchLogger {

    @SuppressWarnings("unused")
    private final TaskSchedulerConfig taskSchedulerConfig;
    private AwsElasticSearchClient es = AwsElasticSearchClient.getInstance();

    private final String esUrl;

    @Autowired
    public ElasticSearchLogger(TaskSchedulerConfig taskSchedulerConfig) {
        // file me
        this.taskSchedulerConfig = taskSchedulerConfig;
        // FIXME
        this.esUrl = null;
        //this.esUrl = taskSchedulerConfig.getElasticSearch().getUrl();
    }

    public void log(Task t)  {

        AwsElasticSearchRequest esRequest = new AwsElasticSearchRequest(esUrl, null, MiscUtil.toJson(t));
        try {
            es.send(esRequest);
        } catch(Exception e) {
            throw new RuntimeException("Fail log to ElasticSearch", e);
        }
    }

    public void log(List<Task> tasks) throws Exception {
        tasks.stream().forEach(t->log(t));
    }

}
