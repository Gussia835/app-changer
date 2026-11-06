package org.Foodcards.service.processing;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.Foodcards.models.AppChangerTrans;
import org.Foodcards.repository.TransRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.Foodcards.utils.Constants.STATUS_NEW;

@ApplicationScoped
public class StuckTransactionService {

    @Inject
    TransRepository transRepo;

    @Scheduled(every = "${app.stuck-check.interval:5m}")
    @Transactional
    public void resetStuckTransactions() {
        List<AppChangerTrans> stuck = transRepo.findStuckTransactions(10);

        if (stuck.isEmpty()) {
            return;
        }

        Log.warn("Found " + stuck.size() + " stuck transactions, resetting to NEW");

        for (AppChangerTrans trans : stuck) {
            trans.setStatus(STATUS_NEW);
            trans.setUpdTs(LocalDateTime.now());
        }
    }
}