package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.plugin.fixtures.InvalidConstructorCountContext
import javaposse.jobdsl.plugin.fixtures.NoPublicConstructorContext
import javaposse.jobdsl.plugin.fixtures.TestAbstractContext
import javaposse.jobdsl.plugin.fixtures.TestContext
import javaposse.jobdsl.plugin.fixtures.TestContextWithDslEnvironment
import javaposse.jobdsl.plugin.fixtures.UnsupportedConstructorArgContext
import spock.lang.Specification

class DslEnvironmentImplSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    DslEnvironmentImpl dslEnvironment = new DslEnvironmentImpl(jobManagement, item)

    def 'createContext with plain Context'() {
        when:
        Context context = dslEnvironment.createContext(TestContext)

        then:
        context instanceof TestContext
    }

    def 'createContext with AbstractContext'() {
        when:
        AbstractContext context = dslEnvironment.createContext(TestAbstractContext)

        then:
        context instanceof TestAbstractContext
        context.jobManagement == jobManagement
    }

    def 'createContext with AbstractExtensibleContext'() {
        when:
        AbstractExtensibleContext context = dslEnvironment.createContext(StepContext)

        then:
        context instanceof StepContext
    }

    def 'createContext with DslEnvironment'() {
        when:
        TestContextWithDslEnvironment context = dslEnvironment.createContext(TestContextWithDslEnvironment)

        then:
        context.dslEnvironment == dslEnvironment
    }

    def 'createContext with more than one public constructor'() {
        when:
        dslEnvironment.createContext(InvalidConstructorCountContext)

        then:
        Exception e = thrown(IllegalArgumentException)
        e.message == 'the context class must have exactly one public constructor'
    }

    def 'createContext with no public constructor'() {
        when:
        dslEnvironment.createContext(NoPublicConstructorContext)

        then:
        Exception e = thrown(IllegalArgumentException)
        e.message == 'the context class must have exactly one public constructor'
    }

    def 'createContext with unsupported constructor argument'() {
        when:
        dslEnvironment.createContext(UnsupportedConstructorArgContext)

        then:
        Exception e = thrown(IllegalArgumentException)
        e.message == 'unsupported constructor parameter type: java.lang.String'
    }
}
