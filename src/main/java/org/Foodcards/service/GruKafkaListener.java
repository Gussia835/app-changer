package org.Foodcards.service;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor
public class GruKafkaListener {

    private final IngestionService ingestionService;

    @Incoming("gru-balance-in")
    @Blocking
    public void consumeBalance(String rawJson) {
        processMessage(rawJson);
    }

    @Incoming("gru-food-in")
    @Blocking
    public void consumeFood(String rawJson) {
        processMessage(rawJson);
    }

    private void processMessage(String rawJson) {
        try {
            ingestionService.process(rawJson);
        } catch (Exception e) {
            Log.error("Critical error in Kafka listener, message will be re-queued", e);

            throw e;
        }
    }

}
