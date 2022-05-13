##INSTALL
Update brew: ```brew update```

Install Kafka: ```brew install kafka```

See folders with kafka and properties: 
```brew info kafka```

##RUN
####Start zookeeper:
```zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties```

####Start Kafka (in separate terminal window):
```kafka-server-start /usr/local/etc/kafka/server.properties```

##TOPICS
####CREATE TOPIC:
```kafka-topics --bootstrap-server 127.0.0.1:9092 --topic second_topic --create --partitions 3 --replication-factor 1```
####LIST ALL TOPICS:
```kafka-topics --bootstrap-server localhost:9092 --list```
####INFO ABOUT TOPIC:
```kafka-topics --bootstrap-server localhost:9092 --topic second_topic --describe```
####DELETE TOPIC:
```kafka-topics --bootstrap-server localhost:9092 --topic second_topic --delete```

##PRODUCER
```kafka-console-producer --bootstrap-server localhost:9092 --topic second_topic```

```kafka-console-producer --bootstrap-server localhost:9092 --topic second_topic --producer-property acks=all```

##CONSUMER:
```kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic```

```kafka-console-consumer --bootstrap-server localhost:9092 --topic second_topic --from-beginning```

```kafka-console-consumer --bootstrap-server localhost:9092 --topic second_topic --group my-first-app```

##CONSUMER-GROUP:
```kafka-consumer-groups --bootstrap-server localhost:9092 --list```

```kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-first-app```

```kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-app --reset-offsets --to-earliest --execute --topic second_topic```

```kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-app --reset-offsets --shift-by -2 --execute --topic second_topic```

##CONFIGS
####FOR TOPICS
**ADD config:**
```kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name first_topic --add-config min.insync.replicas=2 --alter```

**INFO ABOUT config:**
```kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name first_topic --describe```

**DELETE config:**
```kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name first_topic --delete-config min.insync.replicas --alter```
