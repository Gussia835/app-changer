package org.Foodcards.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureResult {
    private int code;
    private String message;
    private List<BalanceChange> changes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceChange {
        private String account;
        private String oldTbal;
        private String newTbal;
        private String operation;
    }
}