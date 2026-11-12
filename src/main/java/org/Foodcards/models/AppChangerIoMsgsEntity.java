package org.Foodcards.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_changer_io_msgs", schema = "acc_app_changer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerIoMsgsEntity extends PanacheEntity {

    @Column(name = "trans_id")
    private Long transId;

    @Column(name = "protocol_type")
    private String protocolType;

    @Column(name = "msg_type")
    private String msgType;

    @Column(name = "dir")
    private String dir;

    @Column(name = "msg")
    private String msg;

    @Column(name = "ins_ts")
    private LocalDateTime insTs;

    @Column(name = "node_id")
    private String nodeId;

    public Long getId() {
        return this.id;
    }
}
