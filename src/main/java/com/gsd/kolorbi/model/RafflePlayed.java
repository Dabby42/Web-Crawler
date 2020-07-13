package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "RafflePlayed")
@ToString
public class RafflePlayed {

    @Id
    @Getter
    @Setter
    String id;

    @Getter
    @Setter
    String raffleId;

    @Getter
    @Setter
    String phone;

    @Indexed(unique = true)
    @Getter
    @Setter
    String ticketNumber;

    @Getter
    @Setter
    String paymentReference;

    @Getter
    @Setter
    String paymentChannel;

    @Getter
    @Setter
    Date createDate;

    @Getter
    @Setter
    Date closeDate;

    @Getter
    @Setter
    String deviceId;

    @Getter
    @Setter
    boolean active;

    @Indexed(unique = true, sparse = true)
    @Getter
    @Setter
    String winCode;

    @Getter
    @Setter
    double winAmount;

    @Getter
    @Setter
    boolean winClaimed;

    @Getter
    @Setter
    String otp;

}
