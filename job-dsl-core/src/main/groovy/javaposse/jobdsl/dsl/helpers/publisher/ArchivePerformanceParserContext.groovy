package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class ArchivePerformanceParserContext implements Context {

    List<Node> parserNodes = []

    void jmeter(@DslContext(ArchivePerformanceJMeterContext) Closure jmeterClosure = null) {
        ArchivePerformanceJMeterContext jMeterContext = new ArchivePerformanceJMeterContext()
        ContextHelper.executeInContext(jmeterClosure, jMeterContext)

        parserNodes << new NodeBuilder().'hudson.plugins.performance.JMeterParser' {
            glob jMeterContext.glob
        }
    }

    void jmeter(String aglob) {
        jmeter { glob aglob }
    }
}
