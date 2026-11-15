package org.Foodcards.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_changer_trans", schema = "acc_app_changer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerTransEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "protocol_type")
    public String protocolType;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "resp_data", columnDefinition = "TEXT")
    private String respData;

    @Column(name = "req_data", columnDefinition = "TEXT", nullable = false)
    private String reqData;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "resp_code")
    private String respCode;

    @Column(name = "resp_desc")
    private String respDesc;

    @CreationTimestamp
    @Column(name = "ins_ts")
    private LocalDateTime insTs;

    @UpdateTimestamp
    @Column(name = "upd_ts")
    private LocalDateTime updTs;

    public String getStreamId() {
        return this.systemId + "_" + this.eventType;
    }
}
