package org.Foodcards.factory.dto;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.Foodcards.dto.entity.TransactionUpdateDTO;
import org.Foodcards.dto.internal.IncomingMessageCommand;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.dto.kafka.SmartEventDTO;
import org.Foodcards.dto.kafka.SmartOutDTO;
import org.Foodcards.utils.JsonUtil;

import java.util.List;

import static org.Foodcards.utils.Constants.*;


@ApplicationScoped
public class TransDtoFactory {
    public TransactionUpdateDTO buildTransactionUpdate(Long transId, String responseData) {
        return TransactionUpdateDTO.builder()
                .transId(transId)
                .status(STATUS_DONE)
                .respData(responseData)
                .respCode(RESPONSE_CODE)
                .respDesc(null)
                .build();
    }

    public IncomingMessageCommand buildIncomingMessage(String rawJson, GruIncomingDTO incomingDto) {
        return IncomingMessageCommand.builder()
                .protocolType(KAFKA_PROTOCOL)
                .rawJson(rawJson)
                .payload(incomingDto)
                .build();
    }
}
