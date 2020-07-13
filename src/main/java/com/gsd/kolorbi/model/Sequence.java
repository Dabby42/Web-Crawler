package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence")
@ToString
public class Sequence {

    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private long seq;
}