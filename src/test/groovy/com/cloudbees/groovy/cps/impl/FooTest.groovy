package com.cloudbees.groovy.cps.impl

import org.junit.Test

public class FooTest {

    @Test
    public void test1() {
        assert 4==add(1,3);
    }

    public int add(int a, int b) { // CPS transformed version
        if (Caller.isAsynchronous(this, "add", [a,b])) {
            throw new CpsCallableInvocation(null /*dummy*/ ,this, [a,b]);
        } else {
            return a+b; //
        }
    }
}
