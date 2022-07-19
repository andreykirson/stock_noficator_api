package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.blockhound.BlockHound;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@EnableScheduling
public class StockPricesProcessingServiceApplication {

  @Bean
  public Scheduler publishEventScheduler() {
    return Schedulers.newBoundedElastic(10, 100, "publish-pool");
  }


  public static void main(String[] args) {
    SpringApplication.run(StockPricesProcessingServiceApplication.class, args);
  }
}
