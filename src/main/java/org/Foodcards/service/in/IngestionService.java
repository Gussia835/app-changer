package org.Foodcards.service.in;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void process(GruIncomingDTO dto, String rawJson) {
        try {
            IncomingMessageCommand command = transDtoFactory.buildIncomingMessage(rawJson, dto);
            dao.saveIncomning(command);

        } catch (Exception e) {
            Log.errorf("Не удалось сохранить сообщение requestId=%s в БД", dto.getRequestId(), e);
            throw new RuntimeException("Ошибка сохранения в БД", e);
        }
    }

    @Transactional
    public void processFromKafka(String rawJson) {
        try {
            GruIncomingDTO dto = jsonUtil.fromJson(rawJson, GruIncomingDTO.class);
            IncomingMessageCommand command = transDtoFactory.buildIncomingMessage(rawJson, dto);
            dao.saveIncomning(command);
        } catch (Exception e) {
            Log.errorf("Не удалось сохранить сообщение из Kafka: %s", rawJson, e);
            throw new RuntimeException("Ошибка сохранения в БД", e);
        }
    }
}
