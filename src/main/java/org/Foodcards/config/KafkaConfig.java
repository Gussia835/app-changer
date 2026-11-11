package org.Foodcards.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import lombok.Getter;

@ConfigMapping(prefix = "app.kafka")
public interface KafkaConfig {

    @WithDefault("kafka:9092")
    String bootstrapServers();

    @WithDefault("app-changer-group")
    String groupIdPrefix();

    @WithDefault("earliest")
    String autoOffsetReset();

    @WithDefault("100")
    int pollTimeoutMs();
}