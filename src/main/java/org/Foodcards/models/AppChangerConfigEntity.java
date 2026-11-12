package org.Foodcards.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_changer_config", schema = "acc_app_changer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerConfigEntity extends PanacheEntity {

    @Column(name = "SYSTEMID")
    private String systemId;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "ENTITY_TYPE")
    private String entityType;

    @Column(name = "PROTOCOL_TYPE")
    private String protocolType;

    @Column(name = "PROTOCOL_IN")
    private String protocolIn;

    @Column(name = "STATUS_IN")
    private Integer statusIn;

    @Column(name = "PROTOCOL_OUT")
    private String protocolOut;

    @Column(name = "STATUS_OUT")
    private Integer statusOut;

    @Column(name = "INS_TS")
    private LocalDateTime insTs;

    @Column(name = "UPD_TS")
    private LocalDateTime updTs;

    public Long getId() {
        return this.id;
    }

    public String getStreamId() {
        return this.systemId + "_" + this.eventType;
    }
}
