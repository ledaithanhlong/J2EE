package com.nhom3.Jurni_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // Enables @CreatedDate and @LastModifiedDate on documents
    
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
}
