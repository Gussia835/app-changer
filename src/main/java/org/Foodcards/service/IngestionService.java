package org.Foodcards.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.Foodcards.factory.dto.TransDtoFactory;
import org.Foodcards.dao.AppChangerDao;
import org.Foodcards.dto.internal.IncomingMessageCommand;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.utils.JsonUtil;

@ApplicationScoped
@RequiredArgsConstructor
public class IngestionService {
    private final AppChangerDao dao;
    private final JsonUtil jsonUtil;
    private final TransDtoFactory transDtoFactory;

    public void process(String rawJson) {
        try {
            GruIncomingDTO incomingDto = jsonUtil.fromJson(rawJson, GruIncomingDTO.class);
            IncomingMessageCommand command = transDtoFactory.buildIncomingMessage(rawJson, incomingDto);

            dao.saveIncomning(command);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
