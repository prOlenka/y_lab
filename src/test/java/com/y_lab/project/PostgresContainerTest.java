package com.y_lab.project;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresContainerTest {

    @Test
    public void testPostgresContainer() {
        try (PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.2")) {
            postgresContainer.start();

            String jdbcUrl = postgresContainer.getJdbcUrl();

            assertTrue(jdbcUrl.contains("jdbc:postgresql://"));
            System.out.println("Postgres URL: " + jdbcUrl);
        }
    }
}
