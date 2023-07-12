package com.poc;

import com.poc.producer.Producer;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SpringKafkaApplication implements CommandLineRunner {

  private final Producer producer;

  // activate test mode to send messages to test topic
  // you need to change 'kafka.topic.batch' in application.yml to 'test'
  @Value("${app.test-mode}")
  private boolean isInTestMode;

  public static void main(String[] args) {
    SpringApplication.run(SpringKafkaApplication.class, args);
  }

  @Autowired
  public SpringKafkaApplication(Producer producer) {
    this.producer = producer;
  }


  @Override
  public void run(String... strings) {
    if (isInTestMode) {
      CompletableFuture.runAsync(() -> {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        for (int i = 1; i < 11; i++){
          producer.send("test", String.format("{\"value\": %d}", i));
        }
      });
    }
  }

}
