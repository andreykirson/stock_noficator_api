package com.microservice.repository;

import java.util.Date;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockRepository extends ReactiveCrudRepository<StockEntity, String> {

  @Query("{ symbol: ?0, 'date' : { $gte: ?1, $lte: ?2 } }")
  Flux<StockEntity> find(String symbol, Date start, Date end);
}
