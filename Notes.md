
Tasks:
3. Create db in MongoDB before start app
4. Investigate onBackPressure
5. Investigate retryBackOff
6. Investigate avoid duplication in MongoDb in TimeSeries Collection
7. Investigate working with KStream with reactive WebFlux
8. Testing


5. MongoDB
5.1. To avoid duplicates we need create indexes with unique = true
5.2. How gracefully handle duplication exception?
5.2. Why does not throw Not Found Exception

6. Global Exception
6.1. Hide message of exception
6.2. Should we handle with "java.util.concurrent.CancellationException" when turn off generator server

8. Create Socket connection and yet another method to emulate update data in real time with 1 sec delay
7.1. For SSE connection on back-end side can use method which return Flux

8. RSocket Request
8.1. Tune retry, backoff and backpressure - ok

9. Kafka cluster
9.1. Tune local storage for real-time query for admin
9.1. Tune avro scheme and registry
9.2. Tune partition

Investigate difference between http 1.x, 1.1, 2 and so on

---- CONFIGURATION ----
app on localhost and kafka in docker
services:
kafka:
image: wurstmeister/kafka:2.12-2.5.0
mem_limit: 512m
ports:
- "9092:9092"
  environment:
  KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
  KAFKA_ADVERTISED_HOST_NAME: localhost
  KAFKA_ADVERTISED_PORT: 9092
  KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
  depends_on:
  - zookeeper
  
zookeeper:
image: wurstmeister/zookeeper:3.4.6
mem_limit: 512m
ports:
- "2181:2181"
  environment:
  - KAFKA_ADVERTISED_HOST_NAME=zookeeper

---- Command ----
to view message in kafka by docker cli run command: 
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic stocks --from-beginning or
kafka-console-consumer --bootstrap-server localhost:9092 --topic stocks --from-beginning
