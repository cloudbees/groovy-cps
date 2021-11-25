package com.cloudbees.groovy.cps.impl

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest
import org.junit.Test

class SpreadMapBlockTest extends AbstractGroovyCpsTest {
    @Test
    void simpleMapSpreadStart() {
        Object result = evalCPS("""
            def y = [a: 'a']
            return [*:y, b: 'b', c: 'c']
        """)
        assertEquals([a: 'a', b: 'b', c: 'c'], result)
    }

    @Test
    void simpleMapSpreadMiddle() {
        Object result = evalCPS("""
            def y = [b: 'b']
            return [a: 'a', *:y, c: 'c']
        """)
        assertEquals([a: 'a', b: 'b', c: 'c'], result)
    }

    @Test
    void simpleMapSpreadEnd() {
        Object result = evalCPS("""
            def y = [c: 'c']
            return [a: 'a', b: 'b', *:y]
        """)
        assertEquals([a: 'a', b: 'b', c: 'c'], result)
    }

    @Test
    void mapSpreadWithReplace() {
        Object result = evalCPS("""
            def y = [a: 'a1', b: 'b1']
            return [a: 'a2', *:y, b: 'b2']
        """)
        assertEquals([a: 'a1', b: 'b2'], result)
    }

    @Test
    void mapSpreadInline() {
        Object result = evalCPS("""
            return [*:[foo: 'bar'], foo: 'hello']
        """)
        assertEquals([foo: 'hello'], result)
    }

    @Test
    void complexMapSpread() {
        Object result = evalCPS("""
            def a = [a: 'abcd', test:123]
            def b = [b: 'bcd', test:456, a: 'a']
            return [*:a, *:b, a: true]
        """)
        assertEquals([b: 'bcd', test: 456, a: true], result)
    }

    @Test
    void spreadEmptyMap() {
        Object result = evalCPS("""
            def y = [:]
            return [*:y, a: 'hello']
        """)
        assertEquals([a: 'hello'], result)
    }

    @Test(expected = NullPointerException.class)
    void spreadNull() {
        evalCPS("""
            def y = null
            return [*:y, a: 'a']
        """)
    }
}
