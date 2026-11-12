package org.Foodcards.service;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
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
public class StreamManager {

    @Inject
    ConfigRepository configRepository;

    @Inject
    IngestionService ingestionService;

    @Inject
    ManagedExecutor executor;

    @Inject
    KafkaConfig kafkaConfig;

    private final ConcurrentHashMap<String, Future<?>> activeTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, KafkaConsumer<String, String>> consumers = new ConcurrentHashMap<>();

    public void init(@Observes StartupEvent e) {
        Log.info("Инициализация DynamicStreamManager");
        syncStreams();
    }

    @Scheduled(every = "60s")
    public void syncStreams() {
        List<AppChangerConfigEntity> configs = configRepository.findActiveConfigs();

        activeTasks.keySet().forEach(taskId -> {
            String streamId = extractStreamId(taskId);

            boolean isActive = configs.stream()
                            .anyMatch(c -> c.getStreamId().equals(streamId) && c.getStatusIn() == 1);

            if (!isActive) {
                stopTask(taskId);
            }
        });

        for (AppChangerConfigEntity config : configs) {
            if (config.getStatusIn() == 1) {
                String streamId = config.getStreamId();

                String configKey = streamId.toLowerCase().replace("_", "-");

                int requiredThreads = kafkaConfig.concurrency().getOrDefault(configKey,
                                                kafkaConfig.concurrency().getOrDefault("default", 1));

                long currThreads = activeTasks.keySet().stream()
                        .filter(id -> id.startsWith(streamId + "_thread_")).count();

                for (int i = (int) currThreads; i < requiredThreads; ++i) {
                    startConsumerThread(config, i);
                }
            }
        }
    }

    private void startConsumerThread(AppChangerConfigEntity config, int threadIndex) {
        String streamId = config.getStreamId();
        String taskId = streamId + "_thread_" + threadIndex;

        Log.infof("Запуск Consumer #%d для потока: %s (Топик: %s)",
                threadIndex + 1, streamId, config.getProtocolIn());

        KafkaConsumer<String, String> consumer = createConsumer(config);
        consumers.put(taskId, consumer);

        Future<?> task = executor.submit(() -> runConsumerLoop(taskId, consumer));
        activeTasks.put(taskId, task);
    }

    private KafkaConsumer<String, String> createConsumer(AppChangerConfigEntity config) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrapServers());

        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.groupIdPrefix() + "-" + config.getSystemId() + "-" + config.getEventType());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.autoOffsetReset());

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(config.getProtocolIn()));
        return consumer;
    }

    private void runConsumerLoop(String streamId, KafkaConsumer<String, String> consumer) {
        Log.infof("Поток %s начал опрос Kafka", streamId);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(kafkaConfig.pollTimeoutMs()));
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

    private void stopTask(String taskId) {
        Log.infof("Остановка задачи: %s", taskId);

        Future<?> task = activeTasks.remove(taskId);
        if (task != null) task.cancel(true);

        KafkaConsumer<String, String> consumer = consumers.remove(taskId);
        if (consumer != null) {
            consumer.wakeup();
            consumer.close();
        }
    }

    private String extractStreamId(String taskId) {
        int lastUnderscore = taskId.lastIndexOf("_thread_");
        return lastUnderscore > 0 ? taskId.substring(0, lastUnderscore) : taskId;
    }

    public void shutdown(@Observes ShutdownEvent e) {
        Log.info("остановка всех динамических потоков...");
        activeTasks.keySet().forEach(this::stopTask);
    }
}