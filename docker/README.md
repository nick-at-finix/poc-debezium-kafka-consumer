Taken from https://github.com/debezium/debezium-examples/tree/main/tutorial

```bash
# Start the topology
export DEBEZIUM_VERSION=2.1
docker-compose -f docker-compose-postgres.yaml up --force-recreate

# Start Postgres connector
curl -i -X POST -H "Accept:application/json" \
  -H "Content-Type:application/json" \
  http://localhost:8083/connectors/ \
  -d @register-postgres.json

# Consume messages from a Debezium topic, can use '--max-messages 1' to limit output
docker-compose -f docker-compose-postgres.yaml exec \
  kafka /kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server kafka:9092 \
  --from-beginning \
  --property print.key=true \
  --topic 'dbserver1.inventory.customers' | jq .

# kill running console consumer
ps | grep kafka-console-consumer | grep -v grep | awk '{print $1}' | xargs kill -9

# Modify records in the database via Postgres client
docker-compose -f docker-compose-postgres.yaml exec \
  postgres env PGOPTIONS="--search_path=inventory" \
  bash -c 'psql -U $POSTGRES_USER postgres'
  
# SELECT * FROM customers;
# UPDATE customers SET first_name='Brrr' WHERE id=1004;

# Shut down the cluster
docker-compose -f docker-compose-postgres.yaml down -v --rmi 'all'

# figure out version of kafka
docker-compose -f docker-compose-postgres.yaml exec kafka /kafka/bin/kafka-topics.sh --version
```