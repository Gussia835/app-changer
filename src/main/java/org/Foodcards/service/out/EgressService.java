package org.Foodcards.service.out;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.Foodcards.dao.AppChangerDao;
import org.Foodcards.models.AppChangerConfigEntity;
import org.Foodcards.models.AppChangerTransEntity;
import org.Foodcards.repository.ConfigRepository;
import org.Foodcards.repository.TransRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@RequiredArgsConstructor
public class EgressService {

    private final TransRepository transRepo;
    private final AppChangerDao dao;

    @Inject
    private final ConfigRepository configRepository;

    @Inject
    KafkaProducer<String, String> kafkaProducer;

    @Inject
    ManagedExecutor executor;

    @ConfigProperty(name = "app.egress.batch-size", defaultValue = "50")
    int batchSize;

    @Scheduled(every = "${app.egress.interval:2s}")
    @Transactional
    public void sendResponses() {
        List<AppChangerTransEntity> done = transRepo.findDoneTransactions(batchSize);

        if (done.isEmpty()) {
            return;
        }

        Log.infof("Начало отправки %d записей...", done.size());

        List<CompletableFuture<Void>> futures = done.stream()
                .map(trans -> CompletableFuture.runAsync(() -> processTrans(trans), executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        Log.info("Пакет отправки завершен.");
    }

    private void processTrans(AppChangerTransEntity trans) {
        try {
            AppChangerConfigEntity config = configRepository.findBySystemIdAndEvent(trans.getSystemId(), trans.getEventType());

            if (config == null || config.getStatusOut() == 0) {
                Log.infof("Пропуск отправки для %s (STATUS_OUT=0)", trans.getStreamId());
                return;
            }

            ProducerRecord<String, String> proudcerRecord = new ProducerRecord<>(config.getProtocolOut(), trans.getRespData());

            kafkaProducer.send(proudcerRecord, (RecordMetadata recordMetaData, Exception ex) -> {
                if (ex != null) {
                    Log.errorf("Ошибка отправки TransID: %d", trans.getId(), ex);
                    dao.markTransAsError(trans, ex);
                } else {
                    Log.infof("Отправлено TransID: %d в топик: %s", trans.getId(), config.getProtocolOut());
                    dao.markAsSent(trans.getId());
                }
            });

        } catch (Exception e) {
            Log.errorf("Ошибка отправки TransID: %d", trans.getId(), e);
            dao.markTransAsError(trans, e);
        }
    }
}
