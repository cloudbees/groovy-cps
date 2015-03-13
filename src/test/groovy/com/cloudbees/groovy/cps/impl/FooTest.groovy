package com.cloudbees.groovy.cps.impl

public class FooTest {

    public void test1() {
        assert 4==add(1,3);
    }

    public int add(int a, int b) { // CPS transformed version
        if (???) {
            throw new CpsCallableInvocation(null /*dummy*/ ,this, [a,b]);
        } else {
            return a+b; //
        }
    }
}
