package com.gsd.kolorbi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "DevicePreference")
@ToString
public class DevicePreference {

    @Id
    @Getter
    @Setter
    String id;

    @Indexed(unique = true)
    @Getter
    @Setter
    String deviceId;

    @Getter
    @Setter
    Map<String, Long> categoryViewCount;

    @Getter
    @Setter
    Map<String, Long> sourceViewCount;

    @Getter
    @Setter
    List<String> newsIds;

}
