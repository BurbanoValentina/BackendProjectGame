package com.example.gamebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoAuditingConfig {
    // Enables @CreatedDate / @LastModifiedDate annotations for Mongo documents.
}
