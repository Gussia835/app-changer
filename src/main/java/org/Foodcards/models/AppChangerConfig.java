package org.Foodcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "APP_CHANGER_CONFIG", schema = "ACC_APP_CHANGER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerConfig {
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "config_seq")
    @SequenceGenerator(name = "config_seq",
                        sequenceName = "ACC_APP_CHANGER.CONFIG_SEQ",
                        allocationSize = 1)
    @Id
    private Long id;

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
    private String statusOut;

    @Column(name = "INS_TS")
    private LocalDateTime insTs;

    @Column(name = "UPD_TS")
    private LocalDateTime updTs;
}
