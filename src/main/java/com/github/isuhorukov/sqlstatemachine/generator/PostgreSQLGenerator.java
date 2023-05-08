package com.github.isuhorukov.sqlstatemachine.generator;

import com.github.isuhorukov.sqlstatemachine.model.State;
import com.github.isuhorukov.sqlstatemachine.model.StateMachine;
import com.github.isuhorukov.sqlstatemachine.model.StateTransition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PostgreSQLGenerator {

    public String generateStateTransitionSelect(StateMachine stateMachine){
        Map<Short, State> stateMap = stateMachine.getStates().stream().
                collect(Collectors.toMap(State::getId, Function.identity()));
        Map<Short, List<StateTransition>> transitions = stateMachine.getTransitions().stream().
                collect(Collectors.groupingBy(StateTransition::getFrom,
                                                Collectors.mapping(Function.identity(), Collectors.toList())));

        return  "select CASE\n" + transitions.entrySet().stream().map(fromEntry -> {
            final StringBuilder builder = new StringBuilder();
            builder.append(" WHEN state=").append(fromEntry.getKey()).append(" THEN ")
                    .append("--").append(stateMap.get(fromEntry.getKey()).getName()).append('\n');
            builder.append("\tCASE\n").append(fromEntry.getValue().stream()
                    .map(toEntry -> "\t\tWHEN transition.\"st" + fromEntry.getKey() + "_" + toEntry.getTo()
                            + "\" -- " + toEntry.getTransitionRule()
                            + "\n\t\t\tTHEN " + toEntry.getTo()
                            + " --" + stateMap.get(toEntry.getTo()).getName()).collect(Collectors.joining("\n"))).
                    append("\n\t\tELSE state\n\tEND\n");
            return builder.toString();
        }).collect(Collectors.joining())+"\tELSE state\nEND\n";
    }

    public String generateStateTransitionFunction(StateMachine stateMachine, String functionName){
        return "CREATE OR REPLACE FUNCTION " + functionName + "_fsm_transition(\n" +
                "  state smallint,\n" +
                "  transition "+functionName+"_transition_parameter\n" +
                ") RETURNS smallint AS $$\n" +
                generateStateTransitionSelect(stateMachine) +
                "$$ LANGUAGE sql;\n";
    }

    public String generateStateTransitionFinalFunction(String functionName){
        return "CREATE OR REPLACE FUNCTION " + functionName +
                "_fsm_final( state smallint) RETURNS smallint AS $$ select state $$ LANGUAGE sql;\n";
    }

    public String generateStateTransitionAggregateFunction(String functionName){
        return "CREATE OR REPLACE AGGREGATE "+functionName+"_fsm(transition "+functionName+"_transition_parameter) (\n" +
                "  sfunc     = " + functionName + "_fsm_transition,\n" +
                "  stype     = smallint,\n" +
                "  finalfunc = " + functionName + "_fsm_final,\n" +
                "  initcond  = '0'\n" +
                ");\n";
    }

    public String generateStateNames(StateMachine stateMachine, String functionName){
        return  "(VALUES "+stateMachine.getStates().stream().map(stateNameEntry -> "("
                + stateNameEntry.getId() + ", '"
                + stateNameEntry.getName() + "')").collect(Collectors.joining(", "))
                + ") AS "+functionName+"_state_name(state, name)";
    }

    public String generateStateTransitionType(StateMachine stateMachine, String functionName){
        List<String> transitionType = stateMachine.getTransitions().stream().
                map(stateTransition -> "\"st" + stateTransition.getFrom() + "_" + stateTransition.getTo() + "\" boolean").
                collect(Collectors.toList());
        return  "CREATE TYPE "+functionName+"_transition_parameter AS ("+String.join(",", transitionType)+")";
    }

    public String generateAggregateFunctionInvocation(StateMachine stateMachine, String functionName){
        return functionName+"_fsm(("+stateMachine.getTransitions().stream().map(StateTransition::getTransitionRule).collect(Collectors.joining(",\n\t\t\t\t"))+"))";
    }
}
