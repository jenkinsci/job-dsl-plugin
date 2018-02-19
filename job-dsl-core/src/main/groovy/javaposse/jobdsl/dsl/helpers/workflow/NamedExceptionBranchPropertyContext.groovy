package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context

class NamedExceptionBranchPropertyContext extends PropertyStrategyContext implements Context {
    String branch

    /**
     * Branch to apply property exception to.
     */
    void branch(String branchName) {
        this.branch = branchName
    }
}
