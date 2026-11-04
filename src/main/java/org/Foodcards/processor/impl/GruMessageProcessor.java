package org.Foodcards.processor.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.Foodcards.dao.AppChangerDao;
import org.Foodcards.dto.entity.TransactionUpdateDTO;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.internal.ProcedureResult;
import org.Foodcards.dto.internal.RoutingInfo;
import org.Foodcards.dto.kafka.GruEventDTO;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.dto.kafka.SmartEventDTO;
import org.Foodcards.factory.dto.ProcedureDtoFactory;
import org.Foodcards.factory.dto.SmartDtoFactory;
import org.Foodcards.processor.MessageProcessor;
import org.Foodcards.service.ProcedureService;
import org.Foodcards.client.ControllerClient;
import org.Foodcards.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GruMessageProcessor implements MessageProcessor {
    @Inject
    JsonUtil jsonUtil;

    @Inject
    ControllerClient appController;

    @Inject
    ProcedureService procedureService;

    @Inject
    ProcedureDtoFactory procedureDtoFactory;

    @Inject
    SmartDtoFactory smartDtoFactory;

    @Inject
    AppChangerDao dao;

    @Override
    public String getSystemId() {
        return "GRU";
    }

    @Override
    public TransactionUpdateDTO process(String json, Long transId) {
        GruIncomingDTO dto = jsonUtil.fromJson(json, GruIncomingDTO.class);

        List<SmartEventDTO> events = processEvents(dto);

        TransactionUpdateDTO transactionUpdateDTO = procedureDtoFactory.buildTransUpdateForResponse(transId, dto, events);

        return transactionUpdateDTO;
    }

    private List<SmartEventDTO> processEvents(GruIncomingDTO incoming) {

        List<SmartEventDTO> events = new ArrayList<>();

        for (GruEventDTO event : incoming.getEvents()) {
            RoutingInfo routing = appController.getRouting(event.getSystemAccount());
            ProcedureParamsDTO params = procedureDtoFactory.buildProcedureParams(event, routing);

            ProcedureResult result = procedureService.call(params);

            events.add(smartDtoFactory.buildSmartEvent(event, result));

        }

        return events;

    }
}
