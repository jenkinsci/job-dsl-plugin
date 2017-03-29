package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.Context
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AbstractWhitelist

import java.lang.reflect.Method

class JobDslWhitelist extends AbstractWhitelist {
    @Override
    boolean permitsMethod(Method method, Object receiver, Object[] args) {
        Context.isAssignableFrom(method.declaringClass)
    }
}
