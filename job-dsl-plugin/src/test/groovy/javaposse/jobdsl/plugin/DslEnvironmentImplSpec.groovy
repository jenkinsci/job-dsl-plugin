package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
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
    }

    def 'createContext with AbstractExtensibleContext'() {
        when:
        AbstractExtensibleContext context = dslEnvironment.createContext(TestAbstractExtensibleContext)

        then:
        context instanceof TestAbstractExtensibleContext
    }

    @SuppressWarnings('UnnecessaryPublicModifier') // false positive, fixed in CodeNarc 0.25
    def 'createContext with more than one public constructor'() {
        when:
        dslEnvironment.createContext(InvalidConstructorCountContext)

        then:
        Exception e = thrown(IllegalArgumentException)
        e.message == 'the context class must have exactly one public constructor'
    }

    @SuppressWarnings('UnnecessaryPublicModifier') // false positive, fixed in CodeNarc 0.25
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

    @SuppressWarnings('EmptyClass')
    static class TestContext implements Context {
    }

    static class TestAbstractContext extends AbstractContext {
        TestAbstractContext(JobManagement jobManagement) {
            super(jobManagement)
        }
    }

    static class TestAbstractExtensibleContext extends AbstractExtensibleContext {
        TestAbstractExtensibleContext(JobManagement jobManagement, Item item) {
            super(jobManagement, item)
        }

        @Override
        protected void addExtensionNode(Node node) {
        }
    }

    static class InvalidConstructorCountContext implements Context {
        InvalidConstructorCountContext() {
        }

        @SuppressWarnings('GroovyUnusedDeclaration')
        InvalidConstructorCountContext(JobManagement jobManagement) {
        }
    }

    static class NoPublicConstructorContext implements Context {
        private NoPublicConstructorContext() {
        }
    }

    static class UnsupportedConstructorArgContext implements Context {
        @SuppressWarnings('GroovyUnusedDeclaration')
        UnsupportedConstructorArgContext(String arg) {
        }
    }
}
