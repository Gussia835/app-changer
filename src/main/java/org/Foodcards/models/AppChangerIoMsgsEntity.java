package org.Foodcards.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "APP_CHANGER_IO_MSGS", schema = "ACC_APP_CHANGER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerIoMsgsEntity extends PanacheEntity {

    @Column(name = "TRANS_ID")
    private Long transId;

    @Column(name = "PROTOCOL_TYPE")
    private String protocolType;

    @Column(name = "MSG_TYPE")
    private String msgType;

    @Column(name = "DIR")
    private String dir;

    @Column(name = "MSG")
    private String msg;

    @Column(name = "INS_TS")
    private LocalDateTime insTs;

    @Column(name = "NODE_ID")
    private String nodeId;

    public Long getId() {
        return this.id;
    }
}
