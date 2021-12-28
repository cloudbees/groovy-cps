package com.cloudbees.groovy.cps.impl;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.util.*;

/**
 * Marker class to keep track of values that originate from a list spread
 */
class SpreadList extends ArrayList<Object> {
    private SpreadList(List<?> list) {
        super(list);
    }

    static SpreadList of(Object value) {
        // spread list logic adapted from
        // https://github.com/apache/groovy/blob/da73115728834792b22065de637bd2ea9686cce2/src/main/java/org/codehaus/groovy/runtime/ScriptBytecodeAdapter.java#L899
        if (value == null) {
            return new SpreadList(Collections.singletonList(null));
        }

        if (value.getClass().isArray()) {
            return new SpreadList(DefaultTypeTransformation.primitiveArrayToList(value));
        }

        if (value instanceof List) {
            return new SpreadList((List<?>) value);
        }

        String error = "cannot spread the type " + value.getClass().getName() + " with value " + value;
        if (value instanceof Map) {
            error += ", did you mean to use the spread-map operator instead?";
        }
        throw new IllegalArgumentException(error);
    }
}
