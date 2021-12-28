package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class SpreadListBlockTest extends AbstractGroovyCpsTest {
    @Test
    public void spreadOne() {
        Object result = evalCPS("def x = [1]; return [*x]");
        assertEquals(Collections.singletonList(1), result);
    }

    @Test
    public void spreadMultiple() {
        Object result = evalCPS(
            "def x1 = [1, 2]\n" +
            "def x2 = [4, 5]\n" +
            "def x3 = [7, 8]\n" +
            "return [*x1, 3, *x2, 6, *x3]"
        );
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), result);
    }

    @Test
    public void spreadEmptyList() {
        Object result = evalCPS("def x = []; return [*x, 1, 2]");
        assertEquals(Arrays.asList(1, 2), result);
    }

    @Test
    public void spreadNull() {
        Object result = evalCPS("def x = null; return [*x, 1, 2]");
        assertEquals(Arrays.asList(null, 1, 2), result);
    }

    @Test
    public void spreadArray() {
        Object result = evalCPS("def x = (Object[])[1, 2, 3]; return [*x, 1]");
        assertEquals(Arrays.asList(1, 2, 3, 1), result);
    }

    @Test
    public void testCanNotSpreadSet() {
        try {
            evalCPS("def x = new HashSet(Arrays.asList(1,2,3)); return [*x, 1]");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("cannot spread the type java.util.HashSet with value [1, 2, 3]", e.getMessage());
        }
    }
}
