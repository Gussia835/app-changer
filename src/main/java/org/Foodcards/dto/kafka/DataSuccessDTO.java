package org.Foodcards.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSuccessDTO {
    private String oldTbal;
    private String newTbal;
    private String operation;
}
