package com.cloudbees.groovy.cps.impl

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest
import org.junit.Test

class SpreadMethodArgumentsTest extends AbstractGroovyCpsTest {
    @Test
    void onlySpreadArgument() {
        Object result = evalCPS('''
            int sum(int a, int b, int c, int d) {
                return a + b + c + d
            }
            def args = [1, 2, 3, 4]
            return sum(*args)
        ''')
        assertEquals(10, result)
    }

    @Test
    void spreadArgumentStart() {
        Object result = evalCPS('''
            int sum(int a, int b, int c, int d) {
                return a + b + c + d
            }
            def args = [1, 2]
            return sum(*args, 3, 4)
        ''')
        assertEquals(10, result)
    }

    @Test
    void spreadArgumentMiddle() {
        Object result = evalCPS('''
            int sum(int a, int b, int c, int d) {
                return a + b + c + d
            }
            def args = [2, 3]
            return sum(1, *args, 4)
        ''')
        assertEquals(10, result)
    }

    @Test
    void spreadArgumentEnd() {
        Object result = evalCPS('''
            int sum(int a, int b, int c, int d) {
                return a + b + c + d
            }
            def args = [3, 4]
            return sum(1, 2, *args)
        ''')
        assertEquals(10, result)
    }

    @Test
    void emptyListAsSpreadArgument() {
        Object result = evalCPS('''
            int sum(int a, int b) {
                return a + b
            }
            def args = []
            return sum(*args, 1, 2)
        ''')
        assertEquals(3, result)
    }

    @Test(expected = MissingMethodException)
    void nullAsSpreadArgument() {
        evalCPS('''
            int sum(int a, int b) {
                return a + b
            }
            def args = null
            return sum(*args, 1, 2)
        ''')
    }

    @Test
    void onlySpreadArgumentForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = [1, 2, 3, 4]
            return sum(*args)
        ''')
        assertEquals(10, result)
    }

    @Test
    void spreadArgumentStartForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = [1, 2]
            return sum(*args, 3, 4)
        ''')
        assertEquals(10, result)
    }

    @Test
    void spreadArgumentMiddleForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = [2, 3]
            return sum(1, *args, 4)
        ''')
        assertEquals(10, result)
    }

    @Test
    void spreadArgumentEndForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = [3, 4]
            return sum(1, 2, *args)
        ''')
        assertEquals(10, result)
    }

    @Test
    void emptyListAsSpreadArgumentForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = []
            return sum(*args)
        ''')
        assertEquals(0, result)
    }

    @Test
    void nullAsSpreadArgumentForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = null
            return sum(*args)
        ''')
        assertEquals(0, result)
    }

    @Test
    void nullAsSpreadArgumentFirstForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = null
            return sum(*args, 1, 2)
        ''')
        assertEquals(3, result)
    }

    @Test
    void nullAsSpreadArgumentMiddleForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = null
            return sum(1, *args, 2)
        ''')
        assertEquals(3, result)
    }

    @Test
    void nullAsSpreadArgumentEndForArray() {
        Object result = evalCPS('''
            int sum(int[] args) {
                def result = 0
                for(int i in args) { result += i }
                return result
            }
            def args = null
            return sum(1, 2, *args)
        ''')
        assertEquals(3, result)
    }
}
