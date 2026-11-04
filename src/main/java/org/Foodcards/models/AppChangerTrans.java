package org.Foodcards.models;

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
public class AppChangerTrans {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "trans_seq")
    @SequenceGenerator(name = "trans_seq",
            sequenceName = "ACC_APP_CHANGER.TRANS_SEQ",
            allocationSize = 1)
    private Long id;

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
