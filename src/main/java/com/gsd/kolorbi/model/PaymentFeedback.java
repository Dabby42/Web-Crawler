package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
public class PaymentFeedback {

    @Getter
    @Setter
    String paymentReference;

    @Getter
    @Setter
    String paymentChannel;

    @Getter
    @Setter
    String requestLog;

    @Getter
    @Setter
    String responseLog;

    @Getter
    @Setter
    String message;

    @Getter
    @Setter
    boolean status;

    @Getter
    @Setter
    Date date;
}
