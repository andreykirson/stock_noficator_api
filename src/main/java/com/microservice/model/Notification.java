package com.microservice.model;

public class Notification
{
  private String symbol;
  private String condition;
  private double conditionValue;

  public Notification(String symbol, String condition, double conditionValue)
  {
    this.symbol         = symbol;
    this.condition      = condition;
    this.conditionValue = conditionValue;
  }

  public void setSymbol(String symbol)
  {
    this.symbol = symbol;
  }

  public void setCondition(String condition)
  {
    this.condition = condition;
  }

  public void setConditionValue(double conditionValue)
  {
    this.conditionValue = conditionValue;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public String getCondition()
  {
    return condition;
  }

  public double getConditionValue()
  {
    return conditionValue;
  }
}
