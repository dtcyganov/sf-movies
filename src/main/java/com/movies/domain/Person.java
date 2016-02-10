package com.movies.domain;

/**
 * Class for person.
 *
 * Participating in serialization/deserialization with
 * gson and objectify (pay attention during refactoring).
 */
public class Person {

    private String name;

    // gson deserialization needs default constructor
    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }
}
