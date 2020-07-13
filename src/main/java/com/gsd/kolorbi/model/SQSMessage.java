package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SQSMessage {

    public static final String RAFFLE_COUNTER_UPDATE = "RAFFLE_COUNTER_UPDATE";

    @Getter
    @Setter
    String action;

    @Getter
    @Setter
    Object payload;
}
