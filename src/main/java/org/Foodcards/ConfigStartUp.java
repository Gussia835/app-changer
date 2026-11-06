package org.Foodcards;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.Foodcards.models.AppChangerConfig;
import org.Foodcards.processor.MessageProcessor;
import org.Foodcards.processor.ProcessorRegister;
import org.Foodcards.repository.ConfigRepository;
import io.quarkus.logging.Log;
import org.Foodcards.service.KafkaProcessingStream;
import org.Foodcards.service.ProcessingStream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConfigStartUp {
    @Inject
    ConfigRepository configRepository;

    @Inject
    ProcessorRegister processorRegister;

    private final Map<String, ProcessingStream> activeStreams = new ConcurrentHashMap<>();

    void init(@Observes StartupEvent e) {

        List<AppChangerConfig> configs = configRepository.findActiveConfigs();

        for (AppChangerConfig config : configs) {
            String streamId = config.getSystemId() + "_" + config.getEventType();

            if (config.getStatusIn() != null && config.getStatusIn() == 1) {
                MessageProcessor processor = processorRegister.getProcessor(config.getSystemId());

                if (processor != null) {
                    ProcessingStream processingStream = new KafkaProcessingStream(config);
                    activeStreams.put(streamId, processingStream);
                    processingStream.start();
                } else {
                    Log.errorf("Не найден процессор для SystemId: %s. Поток не создан.", config.getSystemId());
                }
            } else {
                Log.infof("Поток %s пропущен (STATUS_IN = 0)", streamId);
            }
        }
    }

    public ProcessingStream getStream(String systemId, String eventType) {
        return activeStreams.get(systemId + "_" + eventType);
    }

    public int getActiveStreamsCount() {
        return (int) activeStreams.values().stream().filter(ProcessingStream::isRunning).count();
    }


    public boolean isStreamRunning(String systemId, String eventType) {
        ProcessingStream stream = activeStreams.get(systemId + "_" + eventType);
        return stream != null && stream.isRunning();
    }

    public boolean isOutEnabled(String systemId, String eventType) {
        ProcessingStream stream = activeStreams.get(systemId + "_" + eventType);
        if (stream != null) {

            return "1".equals(stream.getConfig().getStatusOut());
        }
        return false;
    }

}
