package com.microservice.service;

import com.microservice.Stock;
import com.microservice.broker.StockProducer;
import com.microservice.util.exceptions.InvalidInput;
import io.rsocket.transport.netty.client.TcpClientTransport;

import java.time.Duration;
import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketRequester.Builder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;

@Component
public class StockDataProcessing
{
  private static final String TOPIC_NAME = "stocks";

  private static final Logger Log = LoggerFactory.getLogger(StockDataProcessing.class);

  private final RSocketRequester rSocketRequester;
  private final StockProducer    stockProducer;

  private static final TcpClientTransport tcpClientTransport = TcpClientTransport.create("localhost", 7000);

  @Autowired
  public StockDataProcessing(Builder rSocketRequesterBuilder, StockProducer stockProducer)
  {
    this.rSocketRequester = rSocketRequesterBuilder
        .rsocketConnector(connector -> connector.reconnect(Retry.backoff(10, Duration.ofMillis(500))))
        .transport(tcpClientTransport);

    this.stockProducer = stockProducer;
  }

  @PostConstruct
  public void sendStock()
  {
    this.stockProducer
        .sender
        .send(
            getStocks()
                .map(stock -> SenderRecord.create(new ProducerRecord<>(TOPIC_NAME, stock.getSymbol(), stock), stock)))
        .onErrorMap(Exception.class, this::handleException)
        .subscribe();
  }

  private Flux<com.microservice.Stock> getStocks()
  {
    return this.rSocketRequester.route("stocks")
        .retrieveFlux(Stock.class)
        .log()
        .onErrorMap(Exception.class, this::handleException);
  }

  private Throwable handleException(Throwable exception)
  {
    if (!(exception instanceof org.springframework.core.codec.DecodingException)) {
      Log.warn("Got a unexpected error: {}, will rethrow it", exception.toString());
      return exception;
    } else {
      Log.warn("Invalid stock data");
      return new InvalidInput("Invalid stock data");
    }
  }
}
