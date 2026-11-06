package org.Foodcards.service;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.models.AppChangerConfig;
import org.Foodcards.service.in.IngestionService;

@RequiredArgsConstructor
public class KafkaProcessingStream implements ProcessingStream {

    private final AppChangerConfig config;

    @Inject
    private IngestionService ingestionService;

    private boolean running = false;

    @Override
    public String getStreamId() {
        return config.getSystemId() + "_" + config.getEventType();
    }

    @Override
    public AppChangerConfig getConfig() {
        return config;
    }

    @Override
    public void start() {
        if (running) {
            Log.warnf("Поток %s уже запущен", getStreamId());
            return;
        }
        Log.infof("Запуск потока: System=%s, EventType=%s, IN=%s, OUT=%s",
                config.getSystemId(), config.getEventType(),
                config.getProtocolIn(), config.getProtocolOut());
        this.running = true;
    }

    @Override
    public void stop() {
        Log.infof("Остановка потока: %s", getStreamId());
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }


    @Override
    public void process(GruIncomingDTO dto, String rawJson) {
        if (!running) {
            Log.warnf("Поток %s остановлен, сообщение отклонено", getStreamId());
            return;
        }

        try {
            ingestionService.process(dto, rawJson);
        } catch (Exception e) {
            Log.errorf("Ошибка в потоке %s при обработке сообщения", getStreamId(), e);
            throw e;
        }
    }
}