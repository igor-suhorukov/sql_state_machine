package com.github.isuhorukov.sqlstatemachine.parser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
        Assertions.assertThat(fsm.getStates()).isNotNull().hasSize(17);
        Assertions.assertThat(fsm.getTransitions()).isNotNull().hasSize(21);
        ObjectWriter writer = new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter();
        String fsmAsJson = writer.writeValueAsString(fsm);
        Assertions.assertThat(fsmAsJson).isEqualTo("{\n" +
                "  \"states\" : [ {\n" +
                "    \"id\" : -32768,\n" +
                "    \"name\" : \"[*]\"\n" +
                "  }, {\n" +
                "    \"id\" : 0,\n" +
                "    \"name\" : \"born\"\n" +
                "  }, {\n" +
                "    \"id\" : 1,\n" +
                "    \"name\" : \"goes to kindergarten\"\n" +
                "  }, {\n" +
                "    \"id\" : 2,\n" +
                "    \"name\" : \"elementary school student\"\n" +
                "  }, {\n" +
                "    \"id\" : 3,\n" +
                "    \"name\" : \"middle school student\"\n" +
                "  }, {\n" +
                "    \"id\" : 4,\n" +
                "    \"name\" : \"basic general education\"\n" +
                "  }, {\n" +
                "    \"id\" : 5,\n" +
                "    \"name\" : \"high school student\"\n" +
                "  }, {\n" +
                "    \"id\" : 6,\n" +
                "    \"name\" : \"secondary general education\"\n" +
                "  }, {\n" +
                "    \"id\" : 7,\n" +
                "    \"name\" : \"technical school student\"\n" +
                "  }, {\n" +
                "    \"id\" : 8,\n" +
                "    \"name\" : \"technical school graduate\"\n" +
                "  }, {\n" +
                "    \"id\" : 9,\n" +
                "    \"name\" : \"institute student\",\n" +
                "    \"description\" : \"funniest time for young people\\n" +
                "in the same time each student need to improve self education habits\"\n" +
                "  }, {\n" +
                "    \"id\" : 10,\n" +
                "    \"name\" : \"completed higher education\"\n" +
                "  }, {\n" +
                "    \"id\" : 11,\n" +
                "    \"name\" : \"postgraduate\"\n" +
                "  }, {\n" +
                "    \"id\" : 12,\n" +
                "    \"name\" : \"candidate for a degree\",\n" +
                "    \"description\" : \"make research\"\n" +
                "  }, {\n" +
                "    \"id\" : 13,\n" +
                "    \"name\" : \"PhD\"\n" +
                "  }, {\n" +
                "    \"id\" : 14,\n" +
                "    \"name\" : \"no longer learning\"\n" +
                "  }, {\n" +
                "    \"id\" : 32767,\n" +
                "    \"name\" : \"[*]\"\n" +
                "  } ],\n" +
                "  \"transitions\" : [ {\n" +
                "    \"from\" : -32768,\n" +
                "    \"to\" : 0,\n" +
                "    \"transitionRule\" : \"true\"\n" +
                "  }, {\n" +
                "    \"from\" : 0,\n" +
                "    \"to\" : 1,\n" +
                "    \"transitionRule\" : \"age>=3 and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 1,\n" +
                "    \"to\" : 2,\n" +
                "    \"transitionRule\" : \"age>=7 and exams is null and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 2,\n" +
                "    \"to\" : 3,\n" +
                "    \"transitionRule\" : \"age>=11 and exams is null and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 3,\n" +
                "    \"to\" : 4,\n" +
                "    \"transitionRule\" : \"age>=15 and exams='final exams in the 9th grade of the school'\"\n" +
                "  }, {\n" +
                "    \"from\" : 4,\n" +
                "    \"to\" : 5,\n" +
                "    \"transitionRule\" : \"age>=15 and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 5,\n" +
                "    \"to\" : 6,\n" +
                "    \"transitionRule\" : \"age>=17 and exams='final exams in the 11th grade of the school'\"\n" +
                "  }, {\n" +
                "    \"from\" : 4,\n" +
                "    \"to\" : 7,\n" +
                "    \"transitionRule\" : \"age>=16 and exams='college entrance exams' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 7,\n" +
                "    \"to\" : 8,\n" +
                "    \"transitionRule\" : \"age>=18 and exams='graduation from college'\"\n" +
                "  }, {\n" +
                "    \"from\" : 6,\n" +
                "    \"to\" : 9,\n" +
                "    \"transitionRule\" : \"age>=18 and exams='university entrance exams' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 8,\n" +
                "    \"to\" : 9,\n" +
                "    \"transitionRule\" : \"age>=18 and exams='university entrance exams' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 9,\n" +
                "    \"to\" : 10,\n" +
                "    \"transitionRule\" : \"age>=22 and exams='graduation from university' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 10,\n" +
                "    \"to\" : 11,\n" +
                "    \"transitionRule\" : \"age>=22  and exams='postgraduate exams' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 11,\n" +
                "    \"to\" : 12,\n" +
                "    \"transitionRule\" : \"age>=24 and exams='candidate of sciences exams' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 12,\n" +
                "    \"to\" : 13,\n" +
                "    \"transitionRule\" : \"age>=25 and exams='PhD thesis' and desire_to_learn\"\n" +
                "  }, {\n" +
                "    \"from\" : 4,\n" +
                "    \"to\" : 14,\n" +
                "    \"transitionRule\" : \"not(desire_to_learn)\"\n" +
                "  }, {\n" +
                "    \"from\" : 6,\n" +
                "    \"to\" : 14,\n" +
                "    \"transitionRule\" : \"not(desire_to_learn)\"\n" +
                "  }, {\n" +
                "    \"from\" : 8,\n" +
                "    \"to\" : 14,\n" +
                "    \"transitionRule\" : \"not(desire_to_learn)\"\n" +
                "  }, {\n" +
                "    \"from\" : 10,\n" +
                "    \"to\" : 14,\n" +
                "    \"transitionRule\" : \"not(desire_to_learn)\"\n" +
                "  }, {\n" +
                "    \"from\" : 13,\n" +
                "    \"to\" : 14,\n" +
                "    \"transitionRule\" : \"not(desire_to_learn)\"\n" +
                "  }, {\n" +
                "    \"from\" : 14,\n" +
                "    \"to\" : 32767,\n" +
                "    \"transitionRule\" : \"age>=100\"\n" +
                "  } ]\n" +
                "}");
    }
}
