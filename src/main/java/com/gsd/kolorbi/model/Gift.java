package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Gift")
@ToString
public class Gift {

    @Id
    @Getter
    @Setter
    String id;

    @Getter
    @Setter
    Date createDate;

    @Getter
    @Setter
    String deviceId;

    @Indexed(unique = true)
    @Getter
    @Setter
    String winCode;

    @Getter
    @Setter
    double winAmount;

    @Getter
    @Setter
    boolean winClaimed;
}
