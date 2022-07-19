package com.microservice.broker;

import com.microservice.model.Stock;
import com.microservice.repository.NotificationConditionEntity;
import com.microservice.repository.NotificationRepository;
import com.microservice.repository.StockEntity;
import com.microservice.repository.StockRepository;
import com.microservice.util.exceptions.InvalidInput;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Scheduler;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public class ConditionNotificatorHandler
{
  private static final Logger Log = LoggerFactory.getLogger(ConditionNotificatorHandler.class);

  private static final String TOPIC_NAME = "handledStocks";

  private static final String BOOTSTRAP_SERVERS             = "localhost:19092";
  private static final String APPLICATION_SERVER_CONFIG_URL = "http://localhost:8082";
  private static final String APPLICATION_ID                = "StockAppId";
  private static final int    REPLICATION_FACTOR_VALUE      = 1;
  private static final String SCHEMA_REGISTRY_URL           = "http://localhost:8081";
  private static final String SCHEMA_REGISTRY_URL_CONFIG    = "schema.registry.url";

  private final Properties props;

  private final Many<Stock>            sinks;
  private final Flux<Stock>            fluxSink;
  private final StockRepository        stockRepository;
  private final NotificationRepository notificationRepository;
  private final Scheduler              publishEventScheduler;
  private final NotificationProducer   notificationProducer;

  @Autowired
  public ConditionNotificatorHandler(
      StockRepository stockRepository,
      NotificationRepository notificationRepository,
      Scheduler publishEventScheduler,
      NotificationProducer notificationProducer
  )
  {
    this.publishEventScheduler = publishEventScheduler;
    this.notificationProducer  = notificationProducer;

    sinks    = Sinks.many().multicast().onBackpressureBuffer();
    fluxSink = sinks.asFlux();

    props = new Properties();

    props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, REPLICATION_FACTOR_VALUE);
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    props.put(
        StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
        WallclockTimestampExtractor.class
    );
    props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
    props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, APPLICATION_SERVER_CONFIG_URL);
    this.stockRepository        = stockRepository;
    this.notificationRepository = notificationRepository;
  }

  public Flux<Stock> getStocksSinkVersion()
  {
    return Flux.<Stock>create(stockFluxSink -> fluxSink.subscribe(stockFluxSink::next)).share();
  }

  //    @Scheduled(initialDelay = 1000 * 60, fixedDelay=Long.MAX_VALUE)
  @PostConstruct
  private void run()
  {
    Serde<String>                        stringSerde             = Serdes.String();
    Serde<com.microservice.Stock>        stockSpecificAvroSerde  = new SpecificAvroSerde<>();
    Serde<com.microservice.Notification> signalSpecificAvroSerde = new SpecificAvroSerde<>();

    Map<String, String> serdeConfig = Collections.singletonMap(SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);

    stringSerde.configure(serdeConfig, true);
    stockSpecificAvroSerde.configure(serdeConfig, false);

    StreamsBuilder streamsBuilder = new StreamsBuilder();

    KStream<String, com.microservice.Stock> stockKStream = streamsBuilder.stream(
        StockProducer.TOPIC_NAME_Stocks,
        Consumed.with(stringSerde, stockSpecificAvroSerde)
    );

    ForeachAction<String, com.microservice.Stock> saveStockAction = (key, stock) -> stockRepository
        .save(new StockEntity(stock))
        .subscribe();

    ForeachAction<String, com.microservice.Stock> handleNotificationConditionAction = (key, stock) ->
        produceNotification(handleNotificationConditions(stock));

    ForeachAction<String, com.microservice.Stock> emitStockAction = (key, stock) -> sinks.tryEmitNext(new Stock(stock));

    stockKStream.foreach(saveStockAction);
    stockKStream.foreach(emitStockAction);
    stockKStream.foreach(handleNotificationConditionAction);

    stockKStream.to(TOPIC_NAME);

    KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), props);

    kafkaStreams.start();
  }

  // TODO: 15.06.2022 use pagination for query
  // TODO: 16.06.2022 investigate using flux and mono in non blocking way in kafka stream
  // TODO: 16.06.2022 move deletion process to another microservice
  // TODO: Add Error handling
  @Transactional
  public Flux<com.microservice.Notification> handleNotificationConditions(com.microservice.Stock stock)
  {
    return Flux.fromIterable(
        notificationRepository.findAllBySymbolAndCurrentValue(
                stock.getSymbol(),
                stock.getOpen(),
                NotificationConditionEntity.PROCESSING_STATE_Active
            )
            .stream()
            .map(entity -> {
//              el.setProcessingState(PROCESSING_STATE_Resolved); // TODO: 19.06.2022 Investigate what use to guarantee transaction

              notificationRepository.save(
                  new NotificationConditionEntity(
                      entity.getId(),
                      entity.getSymbol(),
                      entity.getCondition(),
                      entity.getValue(),
                      entity.getCreateTime(),
                      entity.getUserId(),
                      NotificationConditionEntity.PROCESSING_STATE_Resolved,
                      Instant.now())
              );

              return new com.microservice.Notification(
                  entity.getId(),
                  entity.getUserEntity().firstName,
                  entity.getUserEntity().lastName,
                  entity.getUserEntity().email,
                  entity.getSymbol(),
                  entity.getCondition(),
                  entity.getValue(),
                  NotificationConditionEntity.PROCESSING_STATE_Resolved
              );
            }).collect(Collectors.toList())
    ).publishOn(publishEventScheduler);
  }

  private void produceNotification(Flux<com.microservice.Notification> notificationFlux)
  {
    notificationProducer
        .sender
        .send(notificationFlux
            .map(
                notification ->
                    SenderRecord.create(
                        new ProducerRecord<>(
                            NotificationProducer.TOPIC_NAME,
                            notification.getSymbol(),
                            notification),
                        notification)
            )
        ).onErrorMap(Exception.class, this::handleException)
        .subscribe();
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
