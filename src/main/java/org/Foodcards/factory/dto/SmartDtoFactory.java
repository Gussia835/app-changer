package org.Foodcards.factory.dto;

import jakarta.enterprise.context.ApplicationScoped;
import org.Foodcards.dto.internal.ProcedureResult;
import org.Foodcards.dto.kafka.*;

import java.util.List;

import static org.Foodcards.utils.Constants.*;


@ApplicationScoped
public class SmartDtoFactory {
    public SmartOutDTO buildSmartOutDTO(GruIncomingDTO incoming, List<SmartEventDTO> responseEvents) {
        return SmartOutDTO.builder()
                .actualTimestamp(String.valueOf(System.currentTimeMillis()))
                .systemId(SYSTEM_SMART)
                .requestId(incoming.getRequestId())
                .eventType(incoming.getEventType())
                .entityType(incoming.getEntityType())
                .status(STATUS_SUCCESS)
                .events(responseEvents)
                .build();
    }

    public SmartEventDTO buildSmartEvent(GruEventDTO event, ProcedureResult result) {
        if (result.getCode() == 0 && !result.getChanges().isEmpty()) {
            ProcedureResult.BalanceChange change = result.getChanges().get(0);
            return SmartEventDTO.builder()
                    .entityValue(event.getSystemAccount())
                    .entityId(event.getId())
                    .data(DataSuccessDTO.builder()
                            .oldTbal(change.getOldTbal())
                            .newTbal(change.getNewTbal())
                            .operation(change.getOperation())
                            .build())
                    .status(STATUS_SUCCESS)
                    .build();
        } else {
            return SmartEventDTO.builder()
                    .entityValue(event.getSystemAccount())
                    .entityId(event.getId())
                    .status(STATUS_ERROR)
                    .error(DataErrorDTO.builder()
                            .code(String.valueOf(result.getCode()))
                            .message(result.getMessage())
                            .build())
                    .build();
        }
    }
}
