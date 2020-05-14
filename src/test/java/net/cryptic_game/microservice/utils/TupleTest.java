package net.cryptic_game.microservice.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TupleTest {

    private Tuple<String, String> tuple = new Tuple<>("abc", "def");

    @Test
    void checkOutput() {
        assertEquals(tuple.getA(), "abc");
        assertEquals(tuple.getB(), "def");
    }
}
