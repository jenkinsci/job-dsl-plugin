package javaposse.jobdsl.plugin

import hudson.ExtensionList
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.plugin.fixtures.ExampleJobDslExtension
import javaposse.jobdsl.plugin.fixtures.TestContextExtensionPoint
import javaposse.jobdsl.plugin.fixtures.TestContextExtensionPoint2
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

import static groovy.lang.Closure.DELEGATE_FIRST

class ContextExtensionPointSpec extends Specification {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @SuppressWarnings('UnnecessarySetter') // false positives for setDelegate and setResolveStrategy
    def 'executeInContext'() {
        setup:
        Closure closure = Mock(Closure)
        Context context = Mock(Context)

        when:
        ContextExtensionPoint.executeInContext(closure, context)

        then:
        1 * closure.setDelegate(context)
        1 * closure.setResolveStrategy(DELEGATE_FIRST)
        1 * closure.call()
    }

    def 'executeInContext runnable not a closure'() {
        when:
        ContextExtensionPoint.executeInContext(Mock(Runnable), Mock(Context))

        then:
        thrown(DslScriptException)
    }

    def 'executeInContext no closure'() {
        when:
        ContextExtensionPoint.executeInContext(null, Mock(Context))

        then:
        noExceptionThrown()
    }

    def 'get all'() {
        when:
        ExtensionList<ContextExtensionPoint> all = ContextExtensionPoint.all()

        then:
        all.size() == 3
        all.any { it instanceof TestContextExtensionPoint }
        all.any { it instanceof TestContextExtensionPoint2 }
        all.any { it instanceof ExampleJobDslExtension }
    }
}
