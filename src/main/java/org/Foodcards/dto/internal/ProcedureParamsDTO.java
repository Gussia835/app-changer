package org.Foodcards.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureParamsDTO {
    String systemAccount;
    String operation;
    String amount;
    String currency;
    String dbConnection;
}
