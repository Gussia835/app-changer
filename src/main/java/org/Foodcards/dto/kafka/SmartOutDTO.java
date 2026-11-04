package org.Foodcards.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartOutDTO {
    private String actualTimestamp;
    private String systemId;
    private String requestId;
    private String eventType;
    private String entityType;
    private String status;
    private List<SmartEventDTO> events;
    private DataErrorDTO error;
}
