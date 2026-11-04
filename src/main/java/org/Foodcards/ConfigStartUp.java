package org.Foodcards;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.Foodcards.models.AppChangerConfig;
import org.Foodcards.repository.ConfigRepository;
import io.quarkus.logging.Log;

import java.util.List;

@ApplicationScoped
public class ConfigStartUp {
    @Inject
    private ConfigRepository configRepository;

    void init(@Observes StartupEvent e) {
        List<AppChangerConfig> configs = configRepository.findActiveConfigs();

        Log.info("Loaded " + configs.size() + " configurations");

        for (AppChangerConfig config : configs) {
            Log.info("Config: " + config.getSystemId() +
                    " | IN: " + config.getProtocolIn() +
                    " | OUT: " + config.getProtocolOut());
        }
    }
}
