package com.microservice.model;

import lombok.Getter;

@Getter
public class Stock {

  public String symbol;
  public String name;
  public long date;
  public double open;
  public double high;
  public double low;
  public double close;
  public double volume;

  public Stock(com.microservice.Stock stock) {
    this.symbol = stock.getSymbol();
    this.name = stock.getName();
    this.date = stock.getDate();
    this.open = stock.getOpen();
    this.high = stock.getHigh();
    this.low = stock.getLow();
    this.close = stock.getClose();
    this.volume = stock.getVolume();
  }

  @Override
  public String toString() {
    return "Stock{" +
        "symbol='" + symbol + '\'' +
        ", name='" + name + '\'' +
        ", date='" + date + '\'' +
        ", open='" + open + '\'' +
        ", high='" + high + '\'' +
        ", low='" + low + '\'' +
        ", close='" + close + '\'' +
        ", volume='" + volume + '\'' +
        '}';
  }
}
