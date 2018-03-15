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

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.NumberAwareComparator;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CpsOrderBy<T> implements Comparator<T> {
    private final List<Closure> closures;
    private boolean equalityCheck;
    private final NumberAwareComparator<Object> numberAwareComparator = new NumberAwareComparator<Object>();

    public CpsOrderBy() {
        this(new ArrayList<Closure>(), false);
    }

    public CpsOrderBy(boolean equalityCheck) {
        this(new ArrayList<Closure>(), equalityCheck);
    }

    public CpsOrderBy(Closure closure) {
        this(closure, false);
    }

    public CpsOrderBy(Closure closure, boolean equalityCheck) {
        this(new ArrayList<Closure>(), equalityCheck);
        closures.add(closure);
    }

    public CpsOrderBy(List<Closure> closures) {
        this(closures, false);
    }

    public CpsOrderBy(List<Closure> closures, boolean equalityCheck) {
        this.equalityCheck = equalityCheck;
        this.closures = closures;
    }

    public void add(Closure closure) {
        closures.add(closure);
    }

    public int compare(T object1, T object2) {
        for (Closure closure : closures) {
            if (closure instanceof CpsClosure) {
                closure = new CpsDelegatingClosure((CpsClosure)closure);
            }
            Object value1 = closure.call(object1);
            Object value2 = closure.call(object2);
            int result;
            if (!equalityCheck || (value1 instanceof Comparable && value2 instanceof Comparable)) {
                result = numberAwareComparator.compare(value1, value2);
            } else {
                result = DefaultTypeTransformation.compareEqual(value1, value2) ? 0 : -1;
            }
            if (result == 0) continue;
            return result;
        }
        return 0;
    }

    public boolean isEqualityCheck() {
        return equalityCheck;
    }

    public void setEqualityCheck(boolean equalityCheck) {
        this.equalityCheck = equalityCheck;
    }

}
