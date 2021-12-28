package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpreadMapBlockTest extends AbstractGroovyCpsTest {
    @Test
    public void multipleSpreads() {
        Object result = evalCPS(
            "def x1 = [a: 'a']\n" +
            "def x2 = [c: 'c', d: 'd']\n" +
            "def x3 = [f: 'f', g: 'g', h: 'h']\n" +
            "return [*:x1, b: 'b', *:x2, e: 'e', *:x3]"
        );
        Map<String, String> expectedMap = new LinkedHashMap<>();
        for (String ch : Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h")) {
            expectedMap.put(ch, ch);
        }
        assertEquals(expectedMap, result);
    }

    @Test
    public void mapSpreadWithReplace() {
        Object result = evalCPS(
            "def y = [a: 'a1', b: 'b1']\n" +
            "return [a: 'a2', *:y, b: 'b2']"
        );
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("a", "a1");
        expectedMap.put("b", "b2");
        assertEquals(expectedMap, result);
    }

    @Test
    public void mapSpreadInline() {
        Object result = evalCPS("return [*:[foo: 'bar'], foo: 'hello']");
        assertEquals(Collections.singletonMap("foo", "hello"), result);
    }

    @Test
    public void complexMapSpread() {
        Object result = evalCPS(
            "def a = [a: 'abcd', test:123]\n" +
            "def b = [b: 'bcd', test:456, a: 'a']\n" +
            "return [*:a, *:b, a: true]"
        );
        Map<Object, Object> expectedMap = new LinkedHashMap<>();
        expectedMap.put("a", true);
        expectedMap.put("test", 456);
        expectedMap.put("b", "bcd");
        assertEquals(expectedMap, result);
    }

    @Test
    public void spreadEmptyMap() {
        Object result = evalCPS("def y = [:]; return [*:y, a: 'hello']");
        assertEquals(Collections.singletonMap("a", "hello"), result);
    }

    @Test
    public void spreadNull() {
        try {
            evalCPS("def y = null; return [*:y, a: 'a']");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertNull(e.getMessage());
        }
    }
}
