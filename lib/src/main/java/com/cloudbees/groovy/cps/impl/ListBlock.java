package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * [a,b,c,d] to list.
 *
 * @author Kohsuke Kawaguchi
 */
public class ListBlock extends CollectionLiteralBlock {
    public ListBlock(Block... args) {
        super(args);
    }

    @Override
    protected Object toCollection(Object[] result) {
        List<Object> answer = new ArrayList<>();
        for (Object o : result) {
            if (o instanceof SpreadList) {
                answer.addAll((SpreadList) o);
            } else {
                answer.add(o);
            }
        }
        return answer;
    }

    private static final long serialVersionUID = 1L;
}
