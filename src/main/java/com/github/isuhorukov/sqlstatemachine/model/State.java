package com.github.isuhorukov.sqlstatemachine.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class State {
    protected short id;
    protected String name;
    @Setter
    protected String description;

    public State(short id, String name) {
        this.id = id;
        this.name = name;
    }
}
