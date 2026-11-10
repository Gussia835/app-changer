package org.Foodcards.service;

import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.models.AppChangerConfigEntity;

public interface ProcessingStream {
    String getStreamId();
    AppChangerConfigEntity getConfig();
    void start();
    void stop();
    boolean isRunning();

    void process(GruIncomingDTO dto, String rawJson);
}
