package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Claim")
@ToString
public class Claim {

    @Id
    @Getter
    @Setter
    String id;

    @Getter
    @Setter
    Date timestamp;

    @Getter
    @Setter
    PaymentFeedback paymentFeedback;

    @Getter
    @Setter
    double amount;

    @Getter
    @Setter
    String type;

    @Getter
    @Setter
    String referenceId;

    @Getter
    @Setter
    String recipientAccount;
}
