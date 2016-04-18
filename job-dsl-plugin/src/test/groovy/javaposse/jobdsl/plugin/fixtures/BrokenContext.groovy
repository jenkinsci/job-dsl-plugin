package javaposse.jobdsl.plugin.fixtures

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextType

@ContextType('a.Foo')
class BrokenContext implements Context {
}
