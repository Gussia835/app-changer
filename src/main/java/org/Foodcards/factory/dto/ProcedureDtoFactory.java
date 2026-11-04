package org.Foodcards.factory.dto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.Foodcards.dto.entity.TransactionUpdateDTO;
import org.Foodcards.dto.internal.IncomingMessageCommand;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.internal.RoutingInfo;
import org.Foodcards.dto.kafka.GruEventDTO;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.dto.kafka.SmartEventDTO;
import org.Foodcards.dto.kafka.SmartOutDTO;
import org.Foodcards.utils.JsonUtil;

import java.util.List;

import static org.Foodcards.utils.Constants.KAFKA_PROTOCOL;

@ApplicationScoped
@RequiredArgsConstructor
public class ProcedureDtoFactory {

    private final JsonUtil jsonUtil;
    private final SmartDtoFactory smartDtoFactory;
    private final TransDtoFactory transDtoFactory;

    public TransactionUpdateDTO buildTransUpdateForResponse(Long transId,
                                                            GruIncomingDTO incomingDTO,
                                                            List<SmartEventDTO> responseEvents) {
        SmartOutDTO response = smartDtoFactory.buildSmartOutDTO(incomingDTO, responseEvents);
        String responseData = jsonUtil.toJson(response);
        TransactionUpdateDTO updateDto = transDtoFactory.buildTransactionUpdate(transId, responseData);

        return updateDto;

    }

    public ProcedureParamsDTO buildProcedureParams(GruEventDTO event, RoutingInfo routing) {
        return ProcedureParamsDTO.builder()
                .systemAccount(event.getSystemAccount())
                .operation(event.getOperation())
                .xalpha(event.getXalfa())
                .currency(event.getCurrency())
                .dbConnection(routing.getDbConnection())
                .build();
    }


}
