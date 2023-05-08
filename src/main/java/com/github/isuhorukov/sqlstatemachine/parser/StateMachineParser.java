package com.github.isuhorukov.sqlstatemachine.parser;

import com.github.isuhorukov.sqlstatemachine.model.StateMachine;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public interface StateMachineParser {

    StateMachine parse(BufferedReader bufferedSource)  throws IOException;

    @SneakyThrows
    default public StateMachine parse(String source) {
        Objects.requireNonNull(source,"'source' should be not null");
        if(source.isEmpty() || source.isBlank()){
            throw new IllegalArgumentException("'source' should be not empty and not blank");
        }
        return parse(new BufferedReader(new StringReader(source)));
    }
    default public StateMachine parse(URL source)  throws IOException {
        Objects.requireNonNull(source,"'source' URL should be not null");
        return parse(new BufferedReader(new InputStreamReader(source.openStream(), StandardCharsets.UTF_8)));
    }
}
