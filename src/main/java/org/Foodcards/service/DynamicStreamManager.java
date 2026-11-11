package org.Foodcards.service;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.Foodcards.config.KafkaConfig;
import org.Foodcards.models.AppChangerConfigEntity;
import org.Foodcards.repository.ConfigRepository;
import org.Foodcards.service.in.IngestionService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@ApplicationScoped
public class DynamicStreamManager {

    @Inject
    ConfigRepository configRepository;

    @Inject
    IngestionService ingestionService;

    @Inject
    ManagedExecutor executor;

    @Inject
    KafkaConfig appConfig;

    private final ConcurrentHashMap<String, Future<?>> activeTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, KafkaConsumer<String, String>> consumers = new ConcurrentHashMap<>();

    public void init(@Observes io.quarkus.runtime.StartupEvent e) {
        Log.info("Инициализация DynamicStreamManager");
        syncStreams();
    }

    @Scheduled(every = "60s")
    public void syncStreams() {
        List<AppChangerConfigEntity> configs = configRepository.findActiveConfigs();

        activeTasks.keySet().forEach(streamId -> {
            boolean isActive = configs.stream()
                    .anyMatch(c -> c.getStreamId().equals(streamId) && c.getStatusIn() == 1);
            if (!isActive) {
                stopStream(streamId);
            }
        });

        configs.stream()
                .filter(c -> c.getStatusIn() == 1)
                .filter(c -> !activeTasks.containsKey(c.getStreamId()))
                .forEach(this::startStream);
    }

    private void startStream(AppChangerConfigEntity config) {
        String streamId = config.getStreamId();
        Log.infof("Запуск Consumer для потока: %s (Топик: %s)", streamId, config.getProtocolIn());

        KafkaConsumer<String, String> consumer = createConsumer(config);
        consumers.put(streamId, consumer);

        Future<?> task = executor.submit(() -> runConsumerLoop(streamId, consumer));
        activeTasks.put(streamId, task);
    }

    private KafkaConsumer<String, String> createConsumer(AppChangerConfigEntity config) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, appConfig.groupIdPrefix() + "-" + config.getSystemId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, appConfig.autoOffsetReset());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(config.getProtocolIn()));
        return consumer;
    }

    private void runConsumerLoop(String streamId, KafkaConsumer<String, String> consumer) {
        Log.infof("Поток %s начал опрос Kafka", streamId);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(appConfig.pollTimeoutMs()));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        ingestionService.processFromKafka(record.value());
                    } catch (Exception e) {
                        Log.errorf("Ошибка обработки сообщения в потоке %s", streamId, e);
                    }
                }
            } catch (Exception e) {
                Log.errorf("Сбой poll() в потоке %s", streamId, e);
                try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
    }

    private void stopStream(String streamId) {
        Log.infof("Остановка потока: %s", streamId);

        Future<?> task = activeTasks.remove(streamId);
        if (task != null) task.cancel(true);

        KafkaConsumer<String, String> consumer = consumers.remove(streamId);
        if (consumer != null) {
            consumer.wakeup();
            consumer.close();
        }
    }

    @PreDestroy
    public void shutdown(@Observes io.quarkus.runtime.ShutdownEvent e) {
        Log.info("Грейсфул остановка всех динамических потоков...");
        activeTasks.keySet().forEach(this::stopStream);
    }
}