package org.Foodcards.factory.dto;

import jakarta.enterprise.context.ApplicationScoped;
import org.Foodcards.dto.entity.IoLogDTO;
import org.Foodcards.models.AppChangerIoMsgsEntity;
import org.Foodcards.models.AppChangerTransEntity;

import java.time.LocalDateTime;

import static org.Foodcards.utils.Constants.*;

@ApplicationScoped
public class IoMsgsDtoFactory {
    public IoLogDTO buildIoLog(AppChangerTransEntity trans, String json, String nodeId) {
        return IoLogDTO.builder()
                .transId(trans.getId())
                .protocolType(trans.getProtocolType())
                .msgType(trans.getSystemId())
                .dir(DIR_IN)
                .msg(json)
                .nodeId(nodeId)
                .build();
    }

    public AppChangerIoMsgsEntity buildOutLog(AppChangerTransEntity trans, String nodeId) {
        return AppChangerIoMsgsEntity.builder()
                .transId(trans.getId())
                .protocolType(trans.getProtocolType())
                .msgType(SYSTEM_SMART)
                .dir(DIR_OUT)
                .msg(trans.getRespData())
                .insTs(LocalDateTime.now())
                .nodeId(nodeId)
                .build();
    }
}

