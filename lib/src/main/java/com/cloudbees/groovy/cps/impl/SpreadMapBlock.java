package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.Block;
import com.cloudbees.groovy.cps.Continuation;
import com.cloudbees.groovy.cps.Env;
import com.cloudbees.groovy.cps.Next;
import org.codehaus.groovy.runtime.InvokerHelper;

public class SpreadMapBlock implements Block {
    private final Block b;

    public SpreadMapBlock(Block b) {
        this.b = b;
    }

    public Next eval(Env e, final Continuation k) {
        return b.eval(e, new Continuation() {
            @Override
            public Next receive(Object o) {
                return k.receive(InvokerHelper.spreadMap(o));
            }
        });
    }

    private static final long serialVersionUID = 1L;
}
