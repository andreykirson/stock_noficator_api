package com.microservice.repository;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stocks")
@CompoundIndexes({
    @CompoundIndex(name = "symbol_date_index", def = "{\"symbol\" : 1, \"date\": 1}", unique = true)
})
public class StockEntity {

  public String symbol;
  public long date;
  public double open;
  public double high;
  public double low;
  public double close;
  public double volume;

  public StockEntity(com.microservice.Stock stock) {
    this.symbol = stock.getSymbol();
    this.date = stock.getDate();
    this.open = stock.getOpen();
    this.high = stock.getHigh();
    this.low = stock.getLow();
    this.close = stock.getClose();
    this.volume = stock.getVolume();
  }
}
