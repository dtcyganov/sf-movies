package com.movies.domain;

/**
 * Class for actor.
 *
 * Participating in serialization/deserialization with
 * gson and objectify (pay attention during refactoring).
 */
public class Actor {

    private String name;

    // gson deserialization needs default constructor
    public Actor() {
    }

    public Actor(String name) {
        this.name = name;
    }
}
