package com.microservice.model;

import com.microservice.repository.NotificationConditionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper
{
  @Mapping(source = "notificationConditionEntity.symbol", target = "symbol")
  @Mapping(source = "notificationConditionEntity.condition", target = "condition")
  @Mapping(source = "notificationConditionEntity.value", target = "conditionValue")
  Notification entityToModel(NotificationConditionEntity notificationConditionEntity);
}
