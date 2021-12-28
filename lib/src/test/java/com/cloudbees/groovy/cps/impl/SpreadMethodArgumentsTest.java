package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest;
import groovy.lang.MissingMethodException;
import org.junit.Test;

public class SpreadMethodArgumentsTest extends AbstractGroovyCpsTest {
    @Test
    public void onlySpreadArgument() {
        Object result = evalCPS(
                "int sum(int a, int b, int c, int d) { return a + b + c + d }\n" +
                "def args = [1, 2, 3, 4]\n" +
                "return sum(*args)"
        );
        assertEquals(10, result);
    }

    @Test
    public void spreadArgumentMiddle() {
        Object result = evalCPS(
                "int sum(int a, int b, int c, int d) { return a + b + c + d }\n" +
                "def args = [2, 3]\n" +
                "return sum(1, *args, 4)"
        );
        assertEquals(10, result);
    }

    @Test
    public void spreadArgumentStart() {
        Object result = evalCPS(
                "int sum(int a, int b, int c, int d, int e) { return a + b + c + d + e }\n" +
                "def args1 = [1, 2]\n" +
                "def args2 = [4, 5]\n" +
                "return sum(*args1, 3, *args2)"
        );
        assertEquals(15, result);
    }

    @Test
    public void emptyListAsSpreadArgument() {
        Object result = evalCPS(
                "int sum(int a, int b) { return a + b }\n" +
                "def args = []\n" +
                "return sum(*args, 1, 2)"
        );
        assertEquals(3, result);
    }

    @Test
    public void nullAsSpreadArgument() {
        try {
            evalCPS(
                    "int sum(int a, int b) { return a + b }\n" +
                    "def args = null\n" +
                    "return sum(*args, 1, 2)"
            );
            fail("Expected MissingMethodException");
        } catch (MissingMethodException e) {
            assertEquals("sum", e.getMethod());
            assertEquals("Script1", e.getType().getName());
            assertArrayEquals(new Object[]{null, 1, 2}, e.getArguments());
        }
    }

    @Test
    public void onlySpreadArgumentForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args = [1, 2, 3, 4]\n" +
                "return sum(*args)"
        );
        assertEquals(10, result);
    }

    @Test
    public void spreadArgumentMiddleForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args = [2, 3]\n" +
                "return sum(1, *args, 4)"
        );
        assertEquals(10, result);
    }

    @Test
    public void spreadArgumentOutsideForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args1 = [1, 2]\n" +
                "def args2 = [4, 5]\n" +
                "return sum(*args1, 3, *args2)"
        );
        assertEquals(15, result);
    }

    @Test
    public void emptyListAsSpreadArgumentForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args = []\n" +
                "return sum(*args)"
        );
        assertEquals(0, result);
    }

    @Test
    public void nullAsSpreadArgumentForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args = null\n" +
                "return sum(*args)"
        );
        assertEquals(0, result);
    }

    @Test
    public void nullAsSpreadArgumentMiddleForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args = null\n" +
                "return sum(1, *args, 4)"
        );
        assertEquals(5, result);
    }

    @Test
    public void nullAsSpreadArgumentOutsideForArray() {
        Object result = evalCPS(
                "int sum(int[] args) {\n" +
                "    def result = 0\n" +
                "    for(int i in args) { result += i }\n" +
                "    return result\n" +
                "}\n" +
                "def args1 = null\n" +
                "def args2 = null\n" +
                "return sum(*args1, 1, 2, *args2)"
        );
        assertEquals(3, result);
    }
}
