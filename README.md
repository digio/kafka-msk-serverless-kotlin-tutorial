# kafka-msk-serverless-kotlin-tutorial
This is a simple Kafka Producer Consumer example which connects to AWS MSK Serverless cluster. 

# How to build
`./gradlew clean build`

# How to locally run the application

AWS MSK Serverless is not open to public. Unless we set up a bastion host, we can't really connect to the cluster from our local machine. 

So for local development, we use single node kafka broker using docker.

To run the kafka server locally
`docker-compose up -d`

To stop the kafka server locally
`docker-compose down`

If you are using glue schema registry, then make sure setting the aws creds.

To run the app in development profile (connecting to local kafka)

java -jar build/libs/kafka-msk-serverless-kotlin-tutorial-0.0.1-SNAPSHOT.jar --spring.profiles.active=development

# How to run the application in an aws hosted environment
The service can be deployed in AWS AppRunner or EC2. Make sure you set the correct environment variables, VPC and IAM policies.

For more details, follow the tutorial.

# Application Properties
Either set the following environment variables
* BOOTSTRAP_URL
* AWS_REGION

* TOPIC_SIMPLE_NAME
* TOPIC_SIMPLE_GROUP

* TOPIC_BOOKCLUB_NAME
* TOPIC_BOOKCLUB_GROUP
* TOPIC_BOOKCLUB_REGISTRY
* TOPIC_BOOKCLUB_SCHEMA

OR update the properties in `resources/application.yml`

# To Run
`java -jar build/libs/kafka-msk-serverless-kotlin-tutorial-0.0.1-SNAPSHOT.jar -Xms256m .`

# REST Services 
(for local server the endpoint.url would be http://localhost:8080)
## To list the existing topics
`GET https://<endpoint.url>/admin/topic/all`

## To create a new topic
`POST https://<endpoint.url>/admin/topic`

with topic name in the body
`msk-serverless-tutorial-topic-simple`

`msk-serverless-tutorial-topic-bookclub`

## Once the topics are created, make sure to start the consumers
`POST https://<endpoint.url>/admin/consumers/start`

## To send a simple message
This will produce a simple text message to topic `msk-serverless-tutorial-topic-simple`.
Endpoint:
`POST https://<endpoint.url>/message/simple`

with a String message body
`This is a simple message`

## To send a bookclub record
This will produce a bookclub record serialised with avro schema TOPIC_BOOKCLUB_SCHEMA created in glue registry.
Endpoint:
`POST https://<endpoint.url>/message/bookclub`
With body 
`
{
    "name": "Runaway",
    "author": "Alice Munro",
    "genre": "Short Stories",
    "rating": "4"
}
`

