package com.cloudbees.groovy.cps

import org.junit.Test

/**
 * Set the breakpoint here to understand how the method dispatching is working in this case
 *
 * @author Kohsuke Kawaguchi
 * @see CpsTransformerTest#closure_rehydrate()
 */
public class Issue28 {
    class MyStrategy {
        Closure<String> process() {
            return {
                speak()
            }
        }
    }
    String speak() {
        'from Script instance'
    }

    @Test
    public void run() {
        Closure<String> closure = new MyStrategy().process()
        println closure.rehydrate(this, this, this).call()
    }
}
