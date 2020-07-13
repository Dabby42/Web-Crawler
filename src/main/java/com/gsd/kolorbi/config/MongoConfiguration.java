package com.gsd.kolorbi.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${mongodb.host}")
    private String host;

    @Value("${mongodb.database.name}")
    private String databaseName;


    @Override
    public MongoClient mongoClient() {
        return new MongoClient(host);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
}
