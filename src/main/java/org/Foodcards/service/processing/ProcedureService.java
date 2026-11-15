package org.Foodcards.service.processing;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.Foodcards.dto.internal.BalanceChange;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.internal.ProcedureResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcedureService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public ProcedureResult call(ProcedureParamsDTO params) {
        ProcedureResult result = new ProcedureResult();
        result.setChanges(new ArrayList<>());

        try {
            String amountStr = params.getAmount();
            if (amountStr == null || amountStr.trim().isEmpty()) {
                amountStr = "0";
            }

            String sql = "SELECT * FROM acc_app_changer.sp_process_balance(:p_system_account, :p_operation, CAST(:p_amount AS NUMERIC), :p_currency)";

            List<Object[]> rows = entityManager.createNativeQuery(sql)
                    .setParameter("p_system_account", params.getSystemAccount())
                    .setParameter("p_operation", params.getOperation())
                    .setParameter("p_amount", new BigDecimal(amountStr))
                    .setParameter("p_currency", params.getCurrency())
                    .getResultList();

            if (rows != null && !rows.isEmpty()) {
                Object[] row = rows.get(0);

                Integer code = row[4] != null ? ((Number) row[4]).intValue() : -1;
                String message = row[5] != null ? row[5].toString() : null;

                result.setCode(code);
                result.setMessage(message);

                if (code == 0) {
                    BalanceChange change = new BalanceChange();
                    change.setAccount(row[0] != null ? row[0].toString() : null);
                    change.setOldTbal(row[1] != null ? row[1].toString() : null);
                    change.setNewTbal(row[2] != null ? row[2].toString() : null);
                    change.setOperation(row[3] != null ? row[3].toString() : null);
                    result.getChanges().add(change);
                }
            } else {
                result.setCode(-1);
                result.setMessage("Функция не вернула результатов");
            }

        } catch (Exception e) {
            Log.errorf(e, "Критическая ошибка вызова процедуры для account %s", params.getSystemAccount());
            result.setCode(-1);
            result.setMessage("Database execution error: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }

        return result;
    }
}