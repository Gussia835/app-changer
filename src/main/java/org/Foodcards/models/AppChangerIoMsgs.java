package org.Foodcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "APP_CHANGER_IO_MSGS", schema = "ACC_APP_CHANGER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChangerIoMsgs {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "ioMsgs_seq")
    @SequenceGenerator(name = "ioMsgs_seq",
                        sequenceName = "ACC_APP_CHANGER.IO_MSGS_SEQ",
                        allocationSize = 1)
    private Long id;

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
}
