package com.github.isuhorukov.sqlstatemachine.parser;

import com.github.isuhorukov.sqlstatemachine.model.State;
import com.github.isuhorukov.sqlstatemachine.model.StateMachine;
import com.github.isuhorukov.sqlstatemachine.model.StateTransition;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantUmlStateMachineParser implements StateMachineParser {

    protected static final String TERMINAL_STATE = "[*]";
    protected static final Pattern STATE_REGEX = Pattern.compile("^state\\s+\\\"(.*)\\\"\\s+as\\s+(\\d+)$");
    protected static final Pattern DESCRIPTION_REGEX = Pattern.compile("^(\\d+)\\s*:\\s+(.*)$");
    protected static final Pattern TRANSITION_REGEX = Pattern.compile(
                                                         "^(\\d+|\\[\\*\\])\\s*-->\\s*(\\d+|\\[\\*\\])\\s*:\\s+(.*)$");

    @Override
    public StateMachine parse(BufferedReader bufferedSource)  throws IOException {
        List<StateTransition> stateTransitions = new ArrayList<>();
        Map<Short, State> states = new TreeMap<>();

        String line;
        while ((line = bufferedSource.readLine()) != null){
            Matcher descriptionMatcher = DESCRIPTION_REGEX.matcher(line);
            Matcher transitionMatcher = TRANSITION_REGEX.matcher(line);
            Matcher stateMatcher = STATE_REGEX.matcher(line);
            boolean descriptionMatch = descriptionMatcher.matches();
            boolean transitionMatch = transitionMatcher.matches();
            boolean stateMatch = stateMatcher.matches();
            if(transitionMatch){
                String fromString = transitionMatcher.group(1);
                String toString = transitionMatcher.group(2);
                String transitionExpression = transitionMatcher.group(3);
                short from;
                short to;
                if(TERMINAL_STATE.equals(fromString)){
                    from = Short.MIN_VALUE;
                } else {
                    from = Short.parseShort(fromString);
                }
                if(TERMINAL_STATE.equals(toString)){
                    to = Short.MIN_VALUE;
                } else {
                    to = Short.parseShort(toString);
                }
                stateTransitions.add(new StateTransition(from, to, transitionExpression));
            } else if(stateMatch){
                String stateName = stateMatcher.group(1);
                String stateId = stateMatcher.group(2);
                short id = Short.parseShort(stateId);
                states.put(id, new State(id, stateName));
            } else if(descriptionMatch){
                String stateId = descriptionMatcher.group(1);
                String descriptionString = descriptionMatcher.group(2);
                short id = Short.parseShort(stateId);
                State state1 = states.get(id);
                if(state1!=null){
                    state1.setDescription(state1.getDescription()==null?
                            descriptionString : state1.getDescription()+"\n"+descriptionString);
                }
            }
        }
        return new StateMachine(new ArrayList<>(states.values()), stateTransitions);
    }
}
