/*
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cloudbees.groovy.cps.impl;

import com.cloudbees.groovy.cps.Continuable;
import com.cloudbees.groovy.cps.Outcome;
import groovy.lang.Closure;
import groovy.lang.MetaClassImpl;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.runtime.CurriedClosure;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import javax.annotation.Nonnull;

public class CpsDelegatingClosure extends Closure {
    private static final Object[] EMPTY_OBJECT_ARRAY = {};

    private final CpsClosure cpsClosure;

    public CpsDelegatingClosure(@Nonnull CpsClosure cpsClosure) {
        super(cpsClosure.getOwner(), cpsClosure.getThisObject());
        this.cpsClosure = cpsClosure;
    }

    @Override
    public Object call() {
        return call(EMPTY_OBJECT_ARRAY);
    }

    @Override
    public Object call(Object... args) {
        try {
            cpsClosure.call(args);
        } catch (CpsCallableInvocation inv) {
            Continuable continuable = new Continuable(inv.asBlock());
            Outcome o = continuable.run0(new Outcome(null, null), Continuable.categories);
            if (o.isSuccess()) {
                return o.getNormal();
            } else {
                throw new RuntimeException(o.getAbnormal());
            }
        }
        return null;
    }

    @Override
    public Object call(Object arguments) {
        if (arguments.getClass().isArray()) {
            return call((Object[]) arguments);
        } else {
            return call(new Object[]{arguments});
        }
    }

    /**
     * {@link ClosureWriter} generates this function with actual argument types.
     * Here we approximate by using varargs.
     * <p>
     * {@link CurriedClosure} invokes this method directly (via {@link MetaClassImpl#invokeMethod(Class, Object, String, Object[], boolean, boolean)}
     */
    public Object doCall(Object... args) {
        return call(args);
    }

    @Override
    public boolean isCase(Object candidate){
        return DefaultTypeTransformation.castToBoolean(call(candidate));
    }

    private static final long serialVersionUID = 1L;

}
