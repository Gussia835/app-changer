package org.Foodcards.dto.entity;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateDTO {
    private Long transId;
    private String respData;
    private String respCode;
    private String respDesc;
    private String status;
}
