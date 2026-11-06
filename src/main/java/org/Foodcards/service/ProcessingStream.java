package org.Foodcards.service;

import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.models.AppChangerConfig;

public interface ProcessingStream {
    String getStreamId();
    AppChangerConfig getConfig();
    void start();
    void stop();
    boolean isRunning();

    void process(GruIncomingDTO dto, String rawJson);
}
