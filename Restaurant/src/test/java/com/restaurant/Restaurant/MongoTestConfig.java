package com.restaurant.Restaurant;

import com.mongodb.client.MongoClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class MongoTestConfig {

    private static final MongoDBContainer mongoDBContainer;

    static {
        // Start MongoDB container before running tests
        mongoDBContainer = new MongoDBContainer("mongo:4.4.3");
        mongoDBContainer.start();

        // Set the Spring Data MongoDB URI to the TestContainers MongoDB instance
        System.setProperty("spring.data.mongodb.uri=mongodb+srv://shamshard94:0771181518@clusterrestaurant.xssj8d2.mongodb.net/?retryWrites=true&w=majority&appName=ClusterRestaurant", mongoDBContainer.getReplicaSetUrl());
    }

    @Bean
    public MongoClient mongoClient() {
        return com.mongodb.client.MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "Restaurant");
    }
}
