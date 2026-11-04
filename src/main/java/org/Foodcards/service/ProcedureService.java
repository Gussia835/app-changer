package org.Foodcards.service;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypeMetaData;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.StructDescriptor;
import org.Foodcards.dto.internal.ProcedureParamsDTO;
import org.Foodcards.dto.internal.ProcedureResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

                List<ProcedureResult.BalanceChange> changes = new ArrayList<>();

                if (code == 0) {
                    try (ResultSet rs = (ResultSet) statement.getObject(2)) {
                        while (rs.next()) {
                            ProcedureResult.BalanceChange change = ProcedureResult.BalanceChange.builder()
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

        oracle.jdbc.OracleConnection oracleConnection =
                connection.unwrap(oracle.jdbc.OracleConnection.class);

        Object[] record = new Object[] {
                params.getSystemAccount(),
                params.getOperation(),
                new java.math.BigDecimal(params.getAmount()),
                params.getCurrency()
        };

        java.sql.Array array = oracleConnection.createOracleArray(
                "T_BALANCE_TABLE",
                new Object[] { record }
        );

        stmt.setArray(1, array);
    }
}
