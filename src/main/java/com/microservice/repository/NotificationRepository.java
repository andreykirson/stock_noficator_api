package com.microservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends CrudRepository<NotificationConditionEntity, Long>
{
  List<NotificationConditionEntity> findByUserIdAndState(long userId, String state);

  @Query("SELECT n FROM NotificationConditionEntity n WHERE n.symbol = :symbol AND " +
      "n.state = :processingState AND " +
      "((n.condition = 'lessThan' AND n.value <= :currentValue) OR (n.condition = 'moreThan' AND n.value >= :currentValue))")
  List<NotificationConditionEntity> findAllBySymbolAndCurrentValue(String symbol, double currentValue, String processingState);

  @Modifying
  @Transactional
  @Query(value = "UPDATE notification_conditions " +
      "SET state = :processingState, start_processing_time =:startProcessingTime " +
      "WHERE notification_conditions.id = :id", nativeQuery = true)
  Integer update(long id, String processingState, Instant startProcessingTime);
}
