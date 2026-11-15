package org.Foodcards.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class GruEventDTO {
    private String id;
    private String systemAccount;
    private String currency;

    @JsonProperty("XALFA")
    private String xalfa;

    private String operation;
}
