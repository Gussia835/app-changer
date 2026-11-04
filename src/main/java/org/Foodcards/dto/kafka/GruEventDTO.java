package org.Foodcards.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GruEventDTO {
    private String id;
    private String systemAccount;
    private String currency;

    @JsonProperty("XALFA")
    private String xalfa;

    private String operation;
}
