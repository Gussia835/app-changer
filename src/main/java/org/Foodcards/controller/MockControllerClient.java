package org.Foodcards.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import org.Foodcards.dto.internal.RoutingInfo;
import org.Foodcards.client.ControllerClient;

@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.APPLICATION + 100)
public class MockControllerClient implements ControllerClient {

    @Override
    public RoutingInfo getRouting(String accountId) {
        return RoutingInfo.builder()
                .dbConnection("jdbc:postgresql://pom_db:5432/pom_db")
                .schemaName("acc_app_changer")
                .procedureName("sp_process_balance")
                .build();
    }
}