package com.cloudbees.groovy.cps.impl

import com.cloudbees.groovy.cps.AbstractGroovyCpsTest
import org.junit.Test

public class FooTest extends AbstractGroovyCpsTest {

    @Test
    public void test1() {
        assert 4==add(1,3);
    }

    @Test
    public void callAddFromCps() {
        assert 3==evalCPS("new FooTest().add(1,2)");
    }

    public int add(int a, int b) { // CPS transformed version
        if (Caller.isAsynchronous(this, "add", [a,b])) {
            throw new CpsCallableInvocation(asyncAdd, this, [a,b]);
        } else {
            return a+b; //
        }
    }

    public final CpsCallable asyncAdd = new CpsFunction(["a","b"],
            new ReturnBlock(new FunctionCallBlock(null,
                    new LocalVariableBlock("a"),
                    new ConstantBlock("plus"),
                    new LocalVariableBlock("y")
        )));
}
