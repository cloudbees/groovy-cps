package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.Block;
import com.cloudbees.groovy.cps.Continuation;
import com.cloudbees.groovy.cps.Env;
import com.cloudbees.groovy.cps.Next;

public class SpreadListBlock implements Block {
    private final Block b;

    public SpreadListBlock(Block b) {
        this.b = b;
    }

    public Next eval(Env e, final Continuation k) {
        return b.eval(e, new Continuation() {
            @Override
            public Next receive(Object o) {
                return k.receive(SpreadList.of(o));
            }
        });
    }

    private static final long serialVersionUID = 1L;
}
