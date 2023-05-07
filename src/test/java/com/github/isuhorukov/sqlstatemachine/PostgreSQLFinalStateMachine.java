package com.github.isuhorukov.sqlstatemachine;


import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgreSQLFinalStateMachine {
    @Container
    private PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15.2-bullseye")
            .withDatabaseName("fsm")
            .withUsername("test")
            .withPassword("secret");
}
