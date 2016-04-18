package javaposse.jobdsl.plugin.fixtures

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextType

@ContextType('java.util.TimeZone')
class InvalidContext implements Context {
}
