package com.cloudbees.groovy.cps.impl

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest
import org.junit.Test

class SpreadListBlockTest extends AbstractGroovyCpsTest {
    @Test
    void simpleListSpreadStart() {
        Object result = evalCPS('''
            def x = [1, 2, 3]
            return [*x, 4, 5]
        ''')
        assertEquals([1, 2, 3, 4, 5], result)
    }

    @Test
    void simpleListSpreadMiddle() {
        Object result = evalCPS('''
            def x = [2, 3, 4]
            return [1, *x, 5]
        ''')
        assertEquals([1, 2, 3, 4, 5], result)
    }

    @Test
    void simpleListSpreadEnd() {
        Object result = evalCPS('''
            def x = [3, 4, 5]
            return [1, 2, *x]
        ''')
        assertEquals([1, 2, 3, 4, 5], result)
    }

    @Test
    void spreadEmptyList() {
        Object result = evalCPS('''
            def x = []
            return [*x, 1, 2]
        ''')
        assertEquals([1, 2], result)
    }

    @Test
    void spreadNull() {
        Object result = evalCPS('''
            def x = null
            return [*x, 1, 2]
        ''')
        assertEquals([null, 1, 2], result)
    }
}
