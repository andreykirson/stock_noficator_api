package com.microservice.repository;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notification_conditions")
public class NotificationConditionEntity
{
  public static final String PROCESSING_STATE_Active   = "active";
  public static final String PROCESSING_STATE_Resolved = "resolved";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long    id;
  @Column(name = "user_id")
  private long    userId;
  private String  symbol;
  @Column(name = "condition_expression")
  private String  condition;
  @Column(name = "condition_value")
  private double  value;
  @Column(name = "create_time")
  private Instant createTime;
  @Column(name = "state")
  private String  state;
  @Column(name = "start_processing_time")
  private Instant startProcessingTime;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
  private UserEntity userEntity;

  public NotificationConditionEntity()
  {
  }

  public NotificationConditionEntity(
      long userId,
      String symbol,
      String condition,
      double value,
      Instant createTime,
      String state
  )
  {
    this.userId     = userId;
    this.symbol     = symbol;
    this.condition  = condition;
    this.value      = value;
    this.createTime = createTime;
    this.state      = state;
  }

  public NotificationConditionEntity(
      long id,
      String symbol,
      String condition,
      double value,
      Instant createTime,
      long userId,
      String state,
      Instant startProcessingTime
  )
  {
    this.id                  = id;
    this.userId              = userId;
    this.symbol              = symbol;
    this.condition           = condition;
    this.value               = value;
    this.createTime          = createTime;
    this.state               = state;
    this.startProcessingTime = startProcessingTime;
  }

  public UserEntity getUserEntity()
  {
    return userEntity;
  }

  public long getId()
  {
    return id;
  }


  public void setId(long id)
  {
    this.id = id;
  }

  public long getUserId()
  {
    return userId;
  }

  public void setUserId(long userId)
  {
    this.userId = userId;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public void setSymbol(String symbol)
  {
    this.symbol = symbol;
  }

  public String getCondition()
  {
    return condition;
  }

  public void setCondition(String condition)
  {
    this.condition = condition;
  }

  public double getValue()
  {
    return value;
  }

  public void setValue(double value)
  {
    this.value = value;
  }

  public Instant getCreateTime()
  {
    return createTime;
  }

  public void setCreateTime(Instant createTime)
  {
    this.createTime = createTime;
  }

  public void setUserEntity(UserEntity userEntity)
  {
    this.userEntity = userEntity;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String processingState)
  {
    this.state = processingState;
  }

  public Instant getStartProcessingTime()
  {
    return startProcessingTime;
  }

  public void setStartProcessingTime(Instant startProcessingTime)
  {
    this.startProcessingTime = startProcessingTime;
  }

  @Override
  public String toString()
  {
    return "NotificationConditionEntity{" +
        "id=" + id +
        ", userId=" + userId +
        ", symbol='" + symbol + '\'' +
        ", condition='" + condition + '\'' +
        ", value=" + value +
        ", createTime=" + createTime +
        ", processingState='" + state + '\'' +
        ", startProcessingTime=" + startProcessingTime +
        ", userEntity=" + userEntity +
        '}';
  }
}
