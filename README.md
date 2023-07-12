# poc-debezium-kafka-consumer

## Overview
This repo houses a proof of concept for using Debezium to stream changes from a Postgres database
to a Kafka topic. The Kafka topic is then consumed by a Kafka consumer in batches.

## Getting Started
Clone this repo and run the following commands from the project root:

```bash
# Start the topology
cd docker
export DEBEZIUM_VERSION=2.1
docker-compose -f docker-compose-postgres.yaml up --force-recreate

# Start postgres connector
curl -i -X POST -H "Accept:application/json" \
  -H "Content-Type:application/json" \
  http://localhost:8083/connectors/ \
  -d @register-postgres.json

# back to project root
cd ..

# Start kafka consumer
mvn spring-boot:run

# Start interactive postgres client session, modify some records and watch the consumer output
docker-compose -f docker-compose-postgres.yaml exec \
  postgres env PGOPTIONS="--search_path=inventory" \
  bash -c 'psql -U $POSTGRES_USER postgres'
```
Example sql commands:
```sql
-- See all records in customers table
SELECT * FROM customers;

-- Update a record
UPDATE customers SET first_name='Brrr' WHERE id=1004;
```

Helpful commands:
```bash

# Consume messages from a Debezium topic via command line, can use '--max-messages 1' to 
# limit output. Run form 'docker' directory.
docker-compose -f docker-compose-postgres.yaml exec \
  kafka /kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --from-beginning \
  --property print.key=true \
  --topic 'dbserver1.inventory.customers' | jq .
  
# kill running console consumer
ps | grep kafka-console-consumer | grep -v grep | awk '{print $1}' | xargs kill -9

# Shut down the cluster
docker-compose -f docker-compose-postgres.yaml down -v --rmi 'all'

# figure out version of kafka
docker-compose -f docker-compose-postgres.yaml exec \
  kafka /kafka/bin/kafka-topics.sh \
  --version

```