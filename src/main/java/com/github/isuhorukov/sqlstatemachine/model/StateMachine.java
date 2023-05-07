package com.github.isuhorukov.sqlstatemachine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class StateMachine {
    private List<State> states;
    private List<StateTransition> transitions;

    public State getState(short id){
        return states.stream().
                filter(state -> state.id == id).findFirst().orElse(null);
    }
}
