package org.Foodcards.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IoLogDTO {
    private Long transId;
    private String protocolType;
    private String msgType;
    private String dir;
    private String msg;
    private String nodeId;
}