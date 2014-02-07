package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context

interface BuildDslContext extends Context {
    /**
     * Specifies Dsl text for the buildDsl Dsl block.
     * @param block of groovy DSL to be applied to the DSL block.
     */
    def buildDslBlock(String buildDslBlock)

}
