package com.poc.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Producer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public void send(String topic, String data) {
    LOGGER.info("sending data='{}' to topic='{}'", data, topic);
    kafkaTemplate.send(topic, data);
  }
}
