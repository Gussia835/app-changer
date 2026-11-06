package org.Foodcards.service.processing;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import oracle.jdbc.OracleTypes;
import org.Foodcards.dto.internal.BalanceChange;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.internal.ProcedureResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcedureService {

    @Inject
    EntityManager entityManager;

    public ProcedureResult call(ProcedureParamsDTO params) {
        try (Connection connection = entityManager.unwrap(Connection.class)) {

            String sql = "{call " + params.getDbConnection() + ".SP_PROCESS_BALANCE(?, ?, ?, ?)}";

            try (CallableStatement statement = connection.prepareCall(sql)){

                setInCursor(statement, connection, params);

                statement.registerOutParameter(2, OracleTypes.CURSOR);
                statement.registerOutParameter(3, Types.INTEGER);
                statement.registerOutParameter(4, Types.VARCHAR);

                statement.execute();

                int code = statement.getInt(3);
                String message = statement.getString(4);

                List<BalanceChange> changes = new ArrayList<>();

                if (code == 0) {
                    try (ResultSet rs = (ResultSet) statement.getObject(2)) {
                        while (rs.next()) {
                            BalanceChange change = BalanceChange.builder()
                                    .account(rs.getString("ACCOUNT"))
                                    .oldTbal(rs.getString("OLD_TBAL"))
                                    .newTbal(rs.getString("NEW_TBAL"))
                                    .operation(rs.getString("OPERATION"))
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

    private void setInCursor(CallableStatement stmt, Connection connection, ProcedureParamsDTO params) throws Exception {

            Struct balanceRecord = connection.createStruct(
                    "GRU.BALANCE_RECORD",
                    new Object[] {
                            params.getSystemAccount(),
                            params.getOperation(),
                            new java.math.BigDecimal(params.getAmount()),
                            params.getCurrency()
                    }
            );

            Array balanceTable = connection.createArrayOf(
                    "GRU.T_BALANCE_TABLE",
                    new Object[] { balanceRecord }
            );

            stmt.setArray(1, balanceTable);
    }
}
