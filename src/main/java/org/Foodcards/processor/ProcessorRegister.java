package org.Foodcards.processor;

import io.netty.handler.codec.MessageAggregator;
import jakarta.enterprise.inject.Instance;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ProcessorRegister {

    @Inject
    Instance<MessageProcessor> processors;

    private Map<String, MessageProcessor> registry = new HashMap<>();

    @PostConstruct
    void init() {
        processors.forEach(p -> registry.put(p.getSystemId(), p));
    }

    public MessageProcessor getProcessor(String systemId) {
        MessageProcessor processor = registry.get(systemId);
        if (processor == null) {
            throw new IllegalArgumentException("No processor for system: " + systemId);
        }
        return processor;
    }
}
