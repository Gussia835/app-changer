package org.Foodcards.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartEventDTO {
    private String entityValue;
    private String entityId;
    private DataSuccessDTO data;
    private String status;
    private DataErrorDTO error;
}
