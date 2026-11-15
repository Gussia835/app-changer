package org.Foodcards.dto.kafka;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class SmartEventDTO {
    private String entityValue;
    private String entityId;
    private DataSuccessDTO data;
    private String status;
    private DataErrorDTO error;
}
