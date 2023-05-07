package com.github.isuhorukov.sqlstatemachine.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class StateTransition {
    protected short from;
    protected short to;
    protected String transitionRule;
}
