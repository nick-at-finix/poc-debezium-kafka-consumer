package com.poc.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

  private final ObjectMapper objectMapper;

  @Autowired
  public Consumer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @KafkaListener(id = "batch", topics = "dbserver1.inventory.customers")
  public void receive(
      List<String> data,
      @Header(name = KafkaHeaders.PARTITION, required = false) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) throws JsonProcessingException {

    LOGGER.info("start of batch receive");
    for (int i = 0; i < data.size(); i++) {
      final String prettyString = objectMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(objectMapper.readTree(data.get(i)));
      if (partitions == null) {
        LOGGER.info("received message='{}'", prettyString);
      } else {
        LOGGER.info("received message='{}' with partition-offset='{}'",
            prettyString,
            partitions.get(i) + "-" + offsets.get(i));
      }
    }
    LOGGER.info("end of batch receive");
  }
}
