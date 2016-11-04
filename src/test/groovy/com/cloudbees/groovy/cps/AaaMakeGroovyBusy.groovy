package com.cloudbees.groovy.cps

import org.junit.Test

/**
 * Do something with Groovy to populate its system.
 *
 * Used to reproduce a failure in JENKINS-26481.
 *
 * @author Kohsuke Kawaguchi
 */
class AaaMakeGroovyBusy {
    @Test
    public void warmUp() {
        def sh = new GroovyShell()
        sh.evaluate("""
            [1,2,3,4].each { println it }
        """)
    }
}
