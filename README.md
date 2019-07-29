Open Task Scheduler
===================

This README provides a quick start. [Please see these slides explaining more details](DOCS/Task-Scheduler.pptx)

What does task scheduler do
--------------------------------------
* It maintains time based delay queue in Redis through Netflix Dyno Queue. 
* Each scheduled task has a time stamp and the task won't be polled out until that time.
* Internally there's a polling thread (for each queue) running every second to check and see
  if there's any task that is expired and will be polled out and dispatched to task 'Consumer'. 

 Features
--------------------------------------------------
* Schedule Task - REST API to schedule task
* Dispatch Task - Ready-to-run task will be delivered to task 'Consumer' through  SQS, Kinesis, or any other messaging channel such as Kafka     
* Flexible Scheduling -  Task could be scheduled in second, minute, hour, or day
* JMX Management - provides JMX MBeans which collects the scheduler metrics

How it works
---------------------------------------------------
#### Schedule Task
	* HTTP POST 'v1/task/schedule' API is provided
	* Example:
	{
	   "type": "demo_dyno_redis_task",
	   "delayInSecond": 10,
	   "additional": {
	    "att_1": "foo",
	    "att_2": "bar",
	    ...
    	  }
    }
    'type' - the type of scheduler the task should be scheduled.
    'delayInSecond' - the task is delay/scheduled in X seconds
    The example task "demo_dyno_redis_task" is scheduled to run in 10 seconds

### Dispatch Task
	* Each type of task scheduler has a polling thread to check if there's any expired task
	* The expired task will be polled out and dispatched to configured messaging/event channel such as SQS, Kinesis (AWS), or Kafka

### Dispatcher classes:
    * 'ava-task-scheduler-dispatcher' module contains Java classes that could configured to dispatch task to different message channel.
    * By default, it provides implementation classes for 'SQS', 'Kinesis', or "Elastic Search'. 

### Build the project
    % ./build.sh

### Run the scheduler
    % ./run.sh

Demo
-------------------------------------------------
A a quick memory queue task scheduler implementation is included. All tasks are kept in the memory using priority queue.
Schedule demo task:

    Example:
	    HTTP POST http://localhost:8080/open-scheduler/v1/task/schedule
	    {
		   "type": "demo_task",
		   "delayInSecond": 0,
		   "repeatCount": 1,
		   "additional": {
		    "test_data":"false"
		   }
	   }
	   The task is scheduled and run immediately 
    Example:
       {
		   "type": "demo_task",
		   "delayInSecond": 10,
		   "repeatCount": 1,
		   "additional": {
		    "test_data":"false"
		   }
	   }
       The task is scheduled and run in 10 seconds 	     
 

Redis Dyno Task
-------------------------------------------------
To enable the 'real' task scheduler with Redis & Dyno, simply add JSON configuration to 'task-scheduler-config-opensource.json' file

     {
			"name": "demo_dyno_redis_task",
			"scheduler": "DynoQueueTaskScheduler",
			"pollingInterval": 1,
			"dispatchRate": -1,
			"delayInSecond": 0,
			"repeatCount": 0,
			"batchSize": 0,
			"taskQueue": {
				"type": "DynoTaskQueue"
			},
			"dispatcher": [
				{
					"type": "StdOutTaskDispatcher"
				}
			]
			
		}
		
Config Redis host and port

       "dynoRedisTaskQueueConfig": {
		"dataCenter": {
			"region": "dc",
			"localDC": "rack1",
			"rack": "rack1",
			"host": "localhost",
		    "port": 6379
		},
		"queueConfig": {
			"prefix": "open_ts",
			"ackTimeout": 60000,
			"ackOnPoll": true
		}
	}
	Change "host" & "port" to the Redis host and port

Dispatch Task to SQS
-------------------------------------------------
JSON configuration to include 'SqsTaskDispatcher' 
	
	{
			"name": "demo_dyno_redis_task",
			"scheduler": "DynoQueueTaskScheduler",
			"pollingInterval": 1,
			"dispatchRate": -1,
			"delayInSecond": 0,
			"repeatCount": 0,
			"batchSize": 0,
			"taskQueue": {
				"type": "DynoTaskQueue"
			},
			"dispatcher": [
				{
					"type": "SqsTaskDispatcher"
				}
			]
			
	}

SQS URL is configured at "com.asurion.ava.scheduler.dispatcher.sqs.SqsTaskDispatcher" class.

Also, "com.asurion.ava.scheduler.dynoqueue.test.e2e.SQSConsumer" test SQS consumer is provided to quickly verify if the task was dispatched to SQS.  	
	
  			

JMX Management Console
------------------------------
	http://localhost:8080/open-scheduler/jmx
	credential: jmx/jmx


Version(s):
---------------------------------------
1.0 initial delivery for open source


