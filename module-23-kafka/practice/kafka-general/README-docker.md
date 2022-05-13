##START via DOCKER

1. Navigate to the directory where the **kafka-single-node.yml** file is located

2. Execute the following command from this directory (-d is a flag for daemon process)

```docker-compose -f kafka-single-node.yml up -d```

3. Check if the containers are up and running

```docker ps```

4. To shut down and remove the setup, execute this command in the same directory

```docker-compose -f kafka-single-node.yml down```


##EXPLORING

#### Logging into the Kafka Container

        docker exec -it kafka-broker /bin/bash

#### Navigate to the Kafka Scripts directory

        cd /opt/bitnami/kafka/bin

#### Creating new Topics

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --create \
            --topic kafka.learning.tweets \
            --partitions 1 \
            --replication-factor 1

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --create \
            --topic kafka.learning.alerts \
            --partitions 1 \
            --replication-factor 1

#### Listing Topics

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --list

#### Getting details about a Topic

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --describe


#### Publishing Messages to Topics

        ./kafka-console-producer.sh \
            --bootstrap-server localhost:29092 \
            --topic kafka.learning.tweets

#### Consuming Messages from Topics

        ./kafka-console-consumer.sh \
            --bootstrap-server localhost:29092 \
            --topic kafka.learning.tweets \
            --from-beginning

#### Deleting Topics

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --delete \
            --topic kafka.learning.alerts


#### Create a Topic with multiple partitions

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --create \
            --topic kafka.learning.orders \
            --partitions 3 \
            --replication-factor 1


#### Check topic partitioning

        ./kafka-topics.sh \
            --bootstrap-server localhost:29092 \
            --topic kafka.learning.orders \
            --describe

#### Publishing Messages to Topics with keys

        ./kafka-console-producer.sh \
            --bootstrap-server localhost:29092 \
            --property "parse.key=true" \
            --property "key.separator=:" \
            --topic kafka.learning.orders

#### Consume messages using a consumer group

        ./kafka-console-consumer.sh \
            --bootstrap-server localhost:29092 \
            --topic kafka.learning.orders \
            --group test-consumer-group \
            --property print.key=true \
            --property key.separator=" = " \
            --from-beginning

#### Check current status of offsets

        ./kafka-consumer-groups.sh \
            --bootstrap-server localhost:29092 \
            --describe \
            --all-groups
