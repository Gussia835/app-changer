package org.Foodcards.service.processing;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.Foodcards.dto.internal.BalanceChange;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.internal.ProcedureResult;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcedureService {

    @Inject
    EntityManager entityManager;

    public ProcedureResult call(ProcedureParamsDTO params) {

        String sql = "{? = call ACC_APP_CHANGER.sp_process_balance(?, ?, ?, ?)}";

        try (Connection connection = entityManager.unwrap(Connection.class)) {

            try (CallableStatement statement = connection.prepareCall(sql)){

                statement.registerOutParameter(1, Types.OTHER);

                statement.setString(2, params.getSystemAccount());
                statement.setString(3, params.getOperation());
                statement.setBigDecimal(4, new BigDecimal(params.getAmount()));
                statement.setString(5, params.getCurrency());

                statement.execute();

                List<BalanceChange> changes = new ArrayList<>();
                int code = 0;
                String message = null;

                try (ResultSet rs = (ResultSet) statement.getObject(1)) {
                    if (rs.next()) {
                        code = rs.getInt("out_code");
                        message = rs.getString("out_message");

                        if (code == 0) {
                            BalanceChange change = BalanceChange.builder()
                                                    .account(rs.getString("out_account"))
                                                    .oldTbal(rs.getString("out_old_tbal"))
                                                    .newTbal(rs.getString("out_new_tbal"))
                                                    .operation(rs.getString("out_operation"))
                                                    .build();

                            changes.add(change);
                        }
                    }
                }


                return ProcedureResult.builder()
                        .code(code)
                        .message(message)
                        .changes(changes)
                        .build();

            }


        } catch (Exception e) {
            Log.error("Failed to call database procedure for account: " + params.getSystemAccount(), e);
            return ProcedureResult.builder()
                    .code(-1)
                    .message("Database error: " + e.getMessage())
                    .changes(new ArrayList<>())
                    .build();
        }


    }
}
