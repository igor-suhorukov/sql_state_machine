package com.github.isuhorukov.sqlstatemachine;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.isuhorukov.sqlstatemachine.generator.PostgreSQLGenerator;
import com.github.isuhorukov.sqlstatemachine.model.StateMachine;
import com.github.isuhorukov.sqlstatemachine.parser.PlantUmlStateMachineParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            statement.executeUpdate("CREATE TABLE life( name text, age int, desire_to_learn boolean, exams text, " +
                                          "CONSTRAINT pk_life PRIMARY KEY (name,age));");
            try (PreparedStatement lifePs = connection.prepareStatement(
                    "INSERT INTO life(name, age, desire_to_learn, exams) VALUES(?,?,?,?);")) {
                Files.lines(Path.of(PlantUmlStateMachineParser.class.getResource("/life.tsv").toURI())).
                        forEach(line ->{
                            prepareInsertString(lifePs, line);
                        });
            }
            String stateAggregateFunction = postgreSQLGenerator.generateAggregateFunctionInvocation(fsm, functionName);
            String stateNames = postgreSQLGenerator.generateStateNames(fsm, functionName);
            String requestStateQuery = "SELECT life_alias.name,life_alias.age,life_alias.desire_to_learn,life_alias.exams, "+
                    functionName +"_state_name.name state FROM \n" +
                    "            (SELECT *, " + stateAggregateFunction +
                    " OVER (PARTITION BY name ORDER BY age) FROM life ORDER BY name,age) life_alias \n" +
                    "INNER JOIN \n" + stateNames + "\n" +
                    "ON " + functionName + "_state_name.state=life_alias."+ functionName +"_fsm;";

            @AllArgsConstructor
            @Getter
            class LearningData{
                String name;
                int age;
                boolean desireToLearn;
                String exams;
                String stateName;
            }
            List<LearningData> dataList = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery(requestStateQuery)){
                while (resultSet.next()){
                    dataList.add(new LearningData(resultSet.getString(1), resultSet.getInt(2),
                            resultSet.getBoolean(3), resultSet.getString(4),
                            resultSet.getString(5)));
                }
            }
            ObjectWriter writer = new ObjectMapper().
                    setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter();
            String data = writer.writeValueAsString(dataList);
            Assertions.assertThat(data).isEqualTo("[ {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 1,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"born\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 2,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"born\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 3,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"goes to kindergarten\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 4,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"goes to kindergarten\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 5,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"goes to kindergarten\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 6,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"goes to kindergarten\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 7,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"elementary school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 8,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"elementary school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 9,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"elementary school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 10,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"elementary school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 11,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"middle school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 12,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"middle school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 13,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"middle school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 14,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"middle school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 15,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"exams\" : \"final exams in the 9th grade of the school\",\n" +
                    "  \"stateName\" : \"basic general education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 16,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"high school student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 17,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"exams\" : \"final exams in the 11th grade of the school\",\n" +
                    "  \"stateName\" : \"secondary general education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 18,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"exams\" : \"university entrance exams\",\n" +
                    "  \"stateName\" : \"institute student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 19,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"institute student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 20,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"institute student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 21,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"institute student\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 22,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"exams\" : \"graduation from university\",\n" +
                    "  \"stateName\" : \"completed higher education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 23,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"completed higher education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 24,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"completed higher education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 25,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"completed higher education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 26,\n" +
                    "  \"desireToLearn\" : true,\n" +
                    "  \"stateName\" : \"completed higher education\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 27,\n" +
                    "  \"desireToLearn\" : false,\n" +
                    "  \"stateName\" : \"no longer learning\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 28,\n" +
                    "  \"desireToLearn\" : false,\n" +
                    "  \"stateName\" : \"no longer learning\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 29,\n" +
                    "  \"desireToLearn\" : false,\n" +
                    "  \"stateName\" : \"no longer learning\"\n" +
                    "}, {\n" +
                    "  \"name\" : \"Matthew\",\n" +
                    "  \"age\" : 30,\n" +
                    "  \"desireToLearn\" : false,\n" +
                    "  \"stateName\" : \"no longer learning\"\n" +
                    "} ]");
        }
    }

    @SneakyThrows
    private void prepareInsertString(PreparedStatement lifePs, String line) {
        String[] parts = line.split("\t");
        lifePs.setString(1,parts[0]);
        lifePs.setInt(2, Integer.parseInt(parts[1]));
        lifePs.setBoolean(3,"t".equals(parts[2]));
        if("\\N".equals(parts[3])){
            lifePs.setNull(4, Types.VARCHAR);
        } else {
            lifePs.setString(4, parts[3]);
        }
        lifePs.executeUpdate();
    }
}
