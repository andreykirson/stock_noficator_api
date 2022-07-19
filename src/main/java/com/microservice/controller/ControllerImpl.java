package com.microservice.controller;

import com.microservice.broker.ConditionNotificatorHandler;
import com.microservice.model.Notification;

import com.microservice.model.NotificationMapper;
import com.microservice.model.Stock;
import com.microservice.repository.NotificationConditionEntity;
import com.microservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class ControllerImpl implements Controller
{
  private final ConditionNotificatorHandler conditionNotificatorHandler;
  private final NotificationRepository      notificationRepository;
  private final NotificationMapper          notificationMapper;
  private final Scheduler                   publishEventScheduler;

  @Autowired
  public ControllerImpl(
      ConditionNotificatorHandler conditionNotificatorHandler,
      NotificationRepository notificationRepository,
      NotificationMapper notificationMapper,
      @Qualifier("publishEventScheduler") Scheduler publishEventScheduler
  )
  {
    this.conditionNotificatorHandler = conditionNotificatorHandler;
    this.notificationRepository      = notificationRepository;
    this.notificationMapper          = notificationMapper;
    this.publishEventScheduler       = publishEventScheduler;
  }

  @Override
  public Flux<Stock> getLastStocksData()
  {
    return conditionNotificatorHandler.getStocksSinkVersion().log();
  }

  @Override
  public Mono<Notification> createNotificationCondition(Notification body)
  {
    return Mono.fromCallable(() -> notificationMapper.entityToModel(
        notificationRepository.save(
            new NotificationConditionEntity(
                1L,
                body.getSymbol(),
                body.getCondition(),
                body.getConditionValue(),
                Instant.now(),
                NotificationConditionEntity.PROCESSING_STATE_Active
            )
        )
    )).publishOn(publishEventScheduler);
  }

  @Override
  public Flux<Notification> getActiveNotificationConditionsByUserId(long userId)
  {
    return Flux.fromIterable(
        notificationRepository.findByUserIdAndState(userId, NotificationConditionEntity.PROCESSING_STATE_Active)
            .stream()
            .map(notificationMapper::entityToModel)
            .collect(Collectors.toList())
    ).publishOn(publishEventScheduler);
  }
}
