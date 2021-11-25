package com.cloudbees.groovy.cps.impl;

import groovy.lang.SpreadListEvaluatingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Marker class to keep track of values that originate from a list spread
 */
class SpreadList extends ArrayList<Object> {
    private SpreadList(Collection<?> collection) {
        super(collection);
    }

    static SpreadList of(Object value) {
        if (value == null) {
            return new SpreadList(Collections.singleton(null));
        }

        if (value instanceof Collection) {
            return new SpreadList((Collection<?>) value);
        }
        throw new SpreadListEvaluatingException("Cannot spread the list " + value.getClass().getName() + ", value " + value);
    }
}
