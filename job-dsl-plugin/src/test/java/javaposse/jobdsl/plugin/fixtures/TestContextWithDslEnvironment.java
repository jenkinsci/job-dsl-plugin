package javaposse.jobdsl.plugin.fixtures;

import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.plugin.DslEnvironment;

public class TestContextWithDslEnvironment implements Context {
    public TestContextWithDslEnvironment(DslEnvironment dslEnvironment) {
        this.dslEnvironment = dslEnvironment;
    }

    public DslEnvironment getDslEnvironment() {
        return dslEnvironment;
    }

    public void setDslEnvironment(DslEnvironment dslEnvironment) {
        this.dslEnvironment = dslEnvironment;
    }

    private DslEnvironment dslEnvironment;
}
