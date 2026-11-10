package org.Foodcards.dao;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.Foodcards.dto.internal.IncomingMessageCommand;
import org.Foodcards.dto.entity.IoLogDTO;
import org.Foodcards.dto.entity.TransactionUpdateDTO;
import org.Foodcards.factory.dto.IoMsgsDtoFactory;
import org.Foodcards.factory.entity.IoMsgsFactory;
import org.Foodcards.factory.entity.TransEntityFactory;
import org.Foodcards.models.AppChangerIoMsgsEntity;
import org.Foodcards.models.AppChangerTransEntity;
import org.Foodcards.repository.IoMsgsRepository;
import org.Foodcards.repository.TransRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;

import static org.Foodcards.utils.Constants.*;

@ApplicationScoped
@RequiredArgsConstructor
public class AppChangerDao {

    private final IoMsgsRepository ioMsgsRepository;
    private final TransRepository transRepository;

    private final TransEntityFactory transFactory;
    private final IoMsgsFactory ioMsgsFactory;
    private final IoMsgsDtoFactory ioMsgsDtoFactory;

    @ConfigProperty(name = "app.node-id")
    String nodeId;

    /*
    Сохранение входящего
     */
    @Transactional
    public void saveIncomning(IncomingMessageCommand command) {
        AppChangerTransEntity trans = transFactory.buildTrans(command);
        transRepository.save(trans);

        IoLogDTO logDto = ioMsgsDtoFactory.buildIoLog(trans, command.getRawJson(), nodeId);
        AppChangerIoMsgsEntity ioMsg = ioMsgsFactory.buildIoMsg(logDto);
        ioMsgsRepository.save(ioMsg);
    }

    /*
    Частичное обновление
     */
    @Transactional
    public void updateAfterProcessing(TransactionUpdateDTO dto) {
        AppChangerTransEntity trans = transRepository.findById(dto.getTransId());

        if (trans == null) {
            throw new IllegalArgumentException("Transaction not found: " + dto.getTransId());
        }

        trans.setStatus(dto.getStatus());
        trans.setRespData(dto.getRespData());
        trans.setRespCode(dto.getRespCode());
        trans.setRespDesc(dto.getRespDesc());
    }

    /*
    Даем статус Progress записям
     */
    @Transactional
    public void markTransProcessing(AppChangerTransEntity trans) {
        trans.setStatus(STATUS_PROCESSING);
        transRepository.getEntityManager().merge(trans);
        transRepository.getEntityManager().flush();
    }


    @Transactional
    public void markAsSent(Long transId) {
        AppChangerTransEntity trans = transRepository.findById(transId);

        if (trans == null) {
            throw new IllegalArgumentException("Transaction not found: " + transId);
        }

        trans.setStatus(STATUS_SENT);
        trans.setUpdTs(LocalDateTime.now());

        AppChangerIoMsgsEntity ioMsg = AppChangerIoMsgsEntity.builder()
                .transId(trans.id)
                .protocolType(trans.getProtocolType())
                .msgType("SMART")
                .dir(DIR_OUT)
                .msg(trans.getRespData())
                .insTs(LocalDateTime.now())
                .nodeId(nodeId)
                .build();

        ioMsgsRepository.save(ioMsg);
    }

    /*
    Пометить ошибкой
     */
    @Transactional
    public void markTransAsError(AppChangerTransEntity trans, Exception e) {
        trans.setStatus(STATUS_ERROR);
        trans.setRespDesc(e.getMessage());
        transRepository.getEntityManager().merge(trans);
    }


}
