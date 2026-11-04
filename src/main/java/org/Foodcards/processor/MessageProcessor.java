package org.Foodcards.processor;

import org.Foodcards.dao.AppChangerDao;
import org.Foodcards.dto.entity.TransactionUpdateDTO;

public interface MessageProcessor {
    String getSystemId();

    TransactionUpdateDTO process(String rawJson, Long transId);
}
