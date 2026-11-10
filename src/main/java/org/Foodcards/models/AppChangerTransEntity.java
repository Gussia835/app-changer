package org.Foodcards.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "APP_CHANGER_TRANS", schema = "ACC_APP_CHANGER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerTransEntity extends PanacheEntity {

    @Column(name = "PROTOCOL_TYPE")
    public String protocolType;

    @Column(name = "SYSTEM_ID")
    private String systemId;

    @Column(name = "REQUEST_ID ")
    private String requestId;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "RESP_DATA", columnDefinition = "CLOB")
    private String respData;

    @Column(name = "REQ_DATA", columnDefinition = "CLOB", nullable = false)
    private String reqData;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "RESP_CODE")
    private String respCode;

    @Column(name = "RESP_DESC")
    private String respDesc;

    @CreationTimestamp
    @Column(name = "INS_TS")
    private LocalDateTime insTs;

    @UpdateTimestamp
    @Column(name = "UPD_TS")
    private LocalDateTime updTs;


}
