package org.Foodcards.service;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.Foodcards.factory.dto.ProcedureDtoFactory;
import org.Foodcards.dao.AppChangerDao;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.entity.TransactionUpdateDTO;
import org.Foodcards.dto.kafka.GruEventDTO;
import org.Foodcards.dto.kafka.GruIncomingDTO;
import org.Foodcards.dto.kafka.SmartEventDTO;
import org.Foodcards.factory.dto.SmartDtoFactory;
import org.Foodcards.models.AppChangerTrans;
import org.Foodcards.processor.MessageProcessor;
import org.Foodcards.processor.ProcessorRegister;
import org.Foodcards.repository.TransRepository;
import org.Foodcards.utils.JsonUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.Foodcards.client.ControllerClient;
import org.Foodcards.dto.internal.ProcedureResult;
import org.Foodcards.dto.internal.RoutingInfo;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcessingService {

    @Inject
    TransRepository transRepository;

    @Inject
    ProcessorRegister processorRegistry;

    @Inject
    AppChangerDao dao;

    @ConfigProperty(name = "app.batch-size", defaultValue = "50")
    private int limit;

    @Scheduled(every = "${app.process.interval:2s}")
    @Transactional
    public void processPending() {

        List<AppChangerTrans> records = transRepository.findNewTransactions(limit);

        for (AppChangerTrans trans : records) {
            try {
                dao.markTransProcessing(trans);

                String currSystemId = trans.getSystemId();
                MessageProcessor currProcessor = processorRegistry.getProcessor(currSystemId);

                TransactionUpdateDTO updateDTO = currProcessor.process(trans.getReqData(), trans.getId());

                dao.updateAfterProcessing(updateDTO);

            } catch (Exception e) {
                Log.error("Failed to process transaction: " + trans.getId(), e);
                dao.markTransAsError(trans, e);
            }

        }
    }

}
