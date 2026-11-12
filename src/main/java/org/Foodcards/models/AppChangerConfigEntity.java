package org.Foodcards.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_changer_config", schema = "acc_app_changer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerConfigEntity extends PanacheEntity {

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "protocol_type")
    private String protocolType;

    @Column(name = "protocol_in")
    private String protocolIn;

    @Column(name = "status_in")
    private Integer statusIn;

    @Column(name = "protocol_out")
    private String protocolOut;

    @Column(name = "status_out")
    private Integer statusOut;

    @Column(name = "ins_ts")
    private LocalDateTime insTs;

    @Column(name = "upd_ts")
    private LocalDateTime updTs;

    public Long getId() {
        return this.id;
    }

    public String getStreamId() {
        return this.systemId + "_" + this.eventType;
    }
}
