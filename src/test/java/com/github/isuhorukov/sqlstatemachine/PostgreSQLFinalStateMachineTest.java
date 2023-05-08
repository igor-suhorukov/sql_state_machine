package com.github.isuhorukov.sqlstatemachine;


import com.github.isuhorukov.sqlstatemachine.generator.PostgreSQLGenerator;
import com.github.isuhorukov.sqlstatemachine.model.StateMachine;
import com.github.isuhorukov.sqlstatemachine.parser.PlantUmlStateMachineParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Testcontainers
public class PostgreSQLFinalStateMachineTest {
    @Container
    private PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15.2-bullseye")
            .withDatabaseName("fsm")
            .withUsername("test")
            .withPassword("secret");

    @Test
    @SneakyThrows
    void testCreateFsmAggregateFunction() {
        URL stateMachineDefinition = PlantUmlStateMachineParser.class.getResource("/stateMachine.puml");
        PlantUmlStateMachineParser stateMachineParser = new PlantUmlStateMachineParser();
        StateMachine fsm = stateMachineParser.parse(stateMachineDefinition);
        PostgreSQLGenerator postgreSQLGenerator = new PostgreSQLGenerator();
        final String functionName = "learning";
        String stateTransitionFunction = postgreSQLGenerator.generateStateTransitionFunction(fsm, functionName);
        String stateTransitionType = postgreSQLGenerator.generateStateTransitionType(fsm, functionName);
        String stateTransitionAggregateFunction = postgreSQLGenerator.generateStateTransitionAggregateFunction(functionName);
        String stateTransitionFinalFunction = postgreSQLGenerator.generateStateTransitionFinalFunction(functionName);
        try (Connection connection = DriverManager.getConnection(postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),postgresqlContainer.getPassword());
             Statement statement = connection.createStatement();){
            statement.executeUpdate(stateTransitionType);
            statement.executeUpdate(stateTransitionFunction);
            statement.executeUpdate(stateTransitionFinalFunction);
            statement.executeUpdate(stateTransitionAggregateFunction);
        }
    }
}
