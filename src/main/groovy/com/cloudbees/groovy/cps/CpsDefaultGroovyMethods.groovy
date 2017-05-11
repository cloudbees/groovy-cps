package com.cloudbees.groovy.cps

import com.cloudbees.groovy.cps.impl.Caller
import com.cloudbees.groovy.cps.impl.CpsCallableInvocation
import com.cloudbees.groovy.cps.impl.CpsFunction
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.InvokerHelper

/**
 *
 * TODO: any way to apply CPS transformation?
 *
 * @author Kohsuke Kawaguchi
 */
public class CpsDefaultGroovyMethods {
    private static MethodLocation loc(String methodName) {
        return new MethodLocation(CpsDefaultGroovyMethods.class, methodName);
    }

    /**
     * Interception is successful. The trick is to pre-translate this method into CPS.
     */
    public static <T> T each(T self, Closure closure) {
        return _each(self, closure);
    }

    public static <T> Iterable<T> each(Iterable<T> self, Closure closure) {
        return _each(self, closure);
    }

    public static <T> Set<T> each(Set<T> self, Closure closure) {
        return _each(self, closure);
    }

    public static <T> SortedSet<T> each(SortedSet<T> self, Closure closure) {
        return _each(self, closure);
    }

    public static <T> Collection<T> each(Collection<T> self, Closure closure) {
        return _each(self, closure);
    }

    public static <T> List<T> each(List<T> list, Closure closure) {
        return _each(list, closure);
    }

    private static <T> T _each(T self, Closure closure) {
        if (!Caller.isAsynchronous(self, "each", closure)
            && !Caller.isAsynchronous(CpsDefaultGroovyMethods.class, "each", self, closure))
            return DefaultGroovyMethods.each(self, closure);

        /*
        each(InvokerHelper.asIterator(self), closure);
        return self;
        */

        def b = new Builder(loc("each"));
        def f = new CpsFunction(["self", "closure"], b.block(
            b.staticCall(-1, CpsDefaultGroovyMethods.class, "each",
                b.staticCall(-1, InvokerHelper.class, "asIterator",
                    b.localVariable("self")
                ),
                b.localVariable("closure")
            ),
            b.return_(b.localVariable("self"))
        ));

        throw new CpsCallableInvocation(f, null, self, closure);
    }

    public static <T> Iterator<T> each(Iterator<T> iter, Closure closure) {
        if (!Caller.isAsynchronous(iter, "each", closure)
            && !Caller.isAsynchronous(CpsDefaultGroovyMethods.class, "each", iter, closure))
            return DefaultGroovyMethods.each(iter, closure);

/*
        while (iter.hasNext()) {
            Object arg = iter.next();
            closure.call(arg);
        }
        return iter;
*/


        def b = new Builder(loc("each"));
        def $iter = b.localVariable("iter")

        def f = new CpsFunction(["iter", "closure"], b.block(
            b.while_(null, b.functionCall(1, $iter, "hasNext"),
                b.block(
                    b.declareVariable(2, Object.class, "arg", b.functionCall(2, $iter, "next")),
                    b.functionCall(3, b.localVariable("closure"), "call", b.localVariable("arg"))
                )
            ),
            b.return_($iter)
        ));

        throw new CpsCallableInvocation(f, null, iter, closure);
    }

    public static <K, V> Map<K, V> each(Map<K, V> map, Closure closure) {
        if (!Caller.isAsynchronous(map, "each", closure)
            && !Caller.isAsynchronous(CpsDefaultGroovyMethods.class, "each", map, closure)) {
            return DefaultGroovyMethods.each(map, closure)
        }

        def b = new Builder(loc("each"))
        def $iter = b.localVariable("iter")

        def callBlock
        if (closure.getMaximumNumberOfParameters() == 2) {
            callBlock = b.block(
                b.declareVariable(2, Map.Entry.class, "argEntry", b.functionCall(2, $iter, "next")),
                b.declareVariable(3, Object.class, "key", b.functionCall(3, b.localVariable("argEntry"), "getKey")),
                b.declareVariable(4, Object.class, "value", b.functionCall(4, b.localVariable("argEntry"), "getValue")),
                b.functionCall(5, b.localVariable("closure"), "call", b.localVariable("key"), b.localVariable("value"))
            )
        } else {
            callBlock = b.block(
                b.declareVariable(2, Map.Entry.class, "argEntry", b.functionCall(2, $iter, "next")),
                b.functionCall(3, b.localVariable("closure"), "call", b.localVariable("argEntry"))
            )

        }
        def f = new CpsFunction(["iter", "closure"], b.block(
            b.while_(null, b.functionCall(1, $iter, "hasNext"),
                callBlock
            ),
            b.return_($iter)
        ))

        throw new CpsCallableInvocation(f, null, map.entrySet().iterator(), closure)
    }

    public static <T> Collection<T> collect(Object self, Closure<T> transform) {
        return _collect(self, new ArrayList<T>(), transform);
    }

    public static <S, T> Collection<T> collect(Collection<S> self, Closure<T> transform) {
        return _collect(self, new ArrayList<T>(), transform);
    }

    public static <S, T> List<T> collect(List<S> list, Closure<T> transform) {
        return _collect(list, new ArrayList<T>(), transform);
    }

    public static Collection collect(Object self) {
        return _collect(self, new ArrayList(), Closure.IDENTITY)
    }

    public static <T> List<T> collect(Collection<T> self) {
        return _collect(self, new ArrayList<T>(), Closure.IDENTITY)
    }

    public static <T> Collection<T> collect(Object self, Collection<T> collector, Closure<? extends T> transform) {
        return _collect(self, collector, transform)
    }

    public static <S, T> Collection<T> collect(Collection<S> self, Collection<T> collector, Closure<? extends T> transform) {
        return _collect(self, collector, transform)
    }

    private static <T> Collection<T> _collect(Object self, Collection<T> collector, Closure<T> closure) {
        if (!Caller.isAsynchronous(self, "collect", closure)
            && !Caller.isAsynchronous(CpsDefaultGroovyMethods.class, "collect", self, closure))
            return DefaultGroovyMethods.collect(self, collector, closure);

        def b = new Builder(loc("collect"));
        def f = new CpsFunction(["self", "collector", "closure"], b.block(
            b.return_(b.staticCall(-1, CpsDefaultGroovyMethods.class, "collect",
                b.staticCall(-1, InvokerHelper.class, "asIterator",
                    b.localVariable("self")
                ),
                b.localVariable("collector"),
                b.localVariable("closure")
            ))
        ));

        throw new CpsCallableInvocation(f, null, self, collector, closure);
    }

    public static <T> Collection<T> collect(Iterator<T> iter, Collection<T> collector, Closure closure) {
        if (!Caller.isAsynchronous(iter, "collect", collector, closure)
            && !Caller.isAsynchronous(CpsDefaultGroovyMethods.class, "collect", iter, collector, closure))
            return DefaultGroovyMethods.collect(iter, collector, closure);

        def b = new Builder(loc("collect"));
        def $iter = b.localVariable("iter")
        def $collector = b.localVariable("collector")

        def f = new CpsFunction(["iter", "collector", "closure"], b.block(
            b.while_(null, b.functionCall(1, $iter, "hasNext"),
                b.block(
                    b.declareVariable(2, Object.class, "arg", b.functionCall(2, $iter, "next")),
                    b.functionCall(3, $collector, "add",
                        b.functionCall(3, b.localVariable("closure"), "call", b.localVariable("arg"))
                    )
                )
            ),
            b.return_($collector)
        ));

        throw new CpsCallableInvocation(f, null, iter, collector, closure);
    }

    public static <T,K,V> List<T> collect(Map<K,V> self, Closure<T> transform) {
        return collect(self, new ArrayList<T>(self.size()), transform)
    }

    public static <T, K, V> Collection<T> collect(Map<K, V> self, Collection<T> collector, Closure<? extends T> transform) {
        if (!Caller.isAsynchronous(self, "collect", collector, transform)
            && !Caller.isAsynchronous(CpsDefaultGroovyMethods.class, "collect", self, collector, transform)) {
            return DefaultGroovyMethods.collect(self, collector, transform)
        }

        def b = new Builder(loc("collect"));
        def $iter = b.localVariable("iter")
        def $collector = b.localVariable("collector")

        def callBlock
        if (transform.getMaximumNumberOfParameters() == 2) {
            callBlock = b.block(
                b.declareVariable(2, Map.Entry.class, "argEntry", b.functionCall(2, $iter, "next")),
                b.declareVariable(3, Object.class, "key", b.functionCall(3, b.localVariable("argEntry"), "getKey")),
                b.declareVariable(4, Object.class, "value", b.functionCall(4, b.localVariable("argEntry"), "getValue")),
                b.functionCall(5, $collector, "add",
                    b.functionCall(5, b.localVariable("closure"), "call", b.localVariable("key"), b.localVariable("value"))
                )
            )
        } else {
            callBlock = b.block(
                b.declareVariable(2, Map.Entry.class, "argEntry", b.functionCall(2, $iter, "next")),
                b.functionCall(3, $collector, "add",
                    b.functionCall(3, b.localVariable("closure"), "call", b.localVariable("argEntry"))
                )
            )

        }

        def f = new CpsFunction(["iter", "collector", "closure"], b.block(
            b.while_(null, b.functionCall(1, $iter, "hasNext"),
                b.block(
                    callBlock
                )
            ),
            b.return_($collector)
        ));

        throw new CpsCallableInvocation(f, null, self.entrySet().iterator(), collector, transform);

    }
}
