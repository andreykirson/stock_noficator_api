package com.microservice.controller;

import com.microservice.model.Notification;
import com.microservice.model.Stock;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Controller
{
  @MessageMapping("stock-last-data")
  Flux<Stock> getLastStocksData();

  @PostMapping(
      path = "/notification",
      consumes = "application/json",
      produces = "application/json"
  )
  Mono<Notification> createNotificationCondition(@RequestBody Notification body);

  @GetMapping(
      value = "/notification",
      produces = "application/json"
  )
  Flux<Notification> getActiveNotificationConditionsByUserId(@RequestParam("userId") long userId);



}
