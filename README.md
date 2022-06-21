# kafka-msk-serverless-kotlin-tutorial
This is a simple Kafka Producer Consumer example which connects to AWS MSK Serverless cluster. 

# How to build
`./gradlew clean build`

# Application Properties
Either set the following environment variables

* BOOTSTRAP_URL
* AWS_REGION
* MESSAGE_TOPIC_NAME
* MESSAGE_GROUP_ID
* SCHEMA_NAME
* SCHEMA_REGISTRY_NAME

OR update the properties in `resources/application.yml`

# To Run
`java -jar build/libs/kafka-msk-serverless-kotlin-tutorial-0.0.1-SNAPSHOT.jar -Xms256m .`

# REST Services

## To list the existing topics

`GET https://<endpoint.url>/admin/topics`

## To create a new topic

`GET https://<endpoint.url>/admin/topics`
