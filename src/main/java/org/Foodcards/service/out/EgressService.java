package org.Foodcards.service.out;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.Foodcards.dao.AppChangerDao;
import org.Foodcards.models.AppChangerTransEntity;
import org.Foodcards.repository.TransRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class EgressService {

    private final TransRepository transRepo;
    private final AppChangerDao dao;

    @Channel("smart-output")
    Emitter<String> kafkaEmitter;

    @ConfigProperty(name = "app.egress.batch-size", defaultValue = "50")
    int batchSize;

    @Scheduled(every = "${app.egress.interval:2s}")
    @Transactional
    public void sendResponses() {
        List<AppChangerTransEntity> done = transRepo.findDoneTransactions(batchSize);

        for (AppChangerTransEntity trans : done) {
            try {
                kafkaEmitter.send(Message.of(trans.getRespData()));
                dao.markAsSent(trans.id);

                Log.info("Sent transaction: " + trans.id);
            } catch (Exception e) {
                Log.error("Failed to send transaction: " + trans.id, e);
                dao.markTransAsError(trans, e);
            }

        }
    }
}
