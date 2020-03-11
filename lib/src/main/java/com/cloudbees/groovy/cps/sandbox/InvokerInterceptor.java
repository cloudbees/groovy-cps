package com.cloudbees.groovy.cps.sandbox;

/**
 * Allows to override one class to perform real-life tests of the CPS logic
 *
 * @author Sergei Parshev <sergei@parshev.net>
 */
public abstract class InvokerInterceptor implements Invoker {
    public Object methodCall(Object receiver, String method, Object[] args) throws Throwable {
        return doMethodCall(receiver, method, args);
    }

    abstract Object doMethodCall(Object receiver, String method, Object[] args) throws Throwable;

    private static final long serialVersionUID = 1L;
}
