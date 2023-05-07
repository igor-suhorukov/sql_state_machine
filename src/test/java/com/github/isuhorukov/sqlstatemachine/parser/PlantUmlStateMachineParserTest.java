package com.github.isuhorukov.sqlstatemachine.parser;

import com.github.isuhorukov.sqlstatemachine.model.StateMachine;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class PlantUmlStateMachineParserTest {

    @Test
    @SneakyThrows
    void testParser() {
        URL stateMachineDefinition = PlantUmlStateMachineParser.class.getResource("/stateMachine.puml");
        PlantUmlStateMachineParser stateMachineParser = new PlantUmlStateMachineParser();
        StateMachine fsm = stateMachineParser.parse(stateMachineDefinition);
        Assertions.assertThat(fsm.getStates()).isNotNull().hasSize(15);
        Assertions.assertThat(fsm.getTransitions()).isNotNull().hasSize(21);
    }
}
