package org.Foodcards.service.in;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.Foodcards.ConfigStartUp;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.service.ProcessingStream;
import org.Foodcards.utils.JsonUtil;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
@RequiredArgsConstructor
public class GruKafkaListener {

    private final ConfigStartUp configStartUp;
    private final JsonUtil jsonUtil;


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
            GruIncomingDTO dto = jsonUtil.fromJson(rawJson, GruIncomingDTO.class);
            String streamId = dto.getSystemId() + "_" + dto.getEventType();

            ProcessingStream stream = configStartUp.getStream(dto.getSystemId(), dto.getEventType());

            if (stream == null || !stream.isRunning()) {
                Log.warnf("Поток %s не найден или остановлен (STATUS_IN=0)", streamId);
                return;
            }

            stream.process(dto, rawJson);

        } catch (Exception e) {
            Log.error("Critical error in Kafka listener, message will be re-queued", e);
            throw e;
        }
    }

}
