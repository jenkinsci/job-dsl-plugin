package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.plugin.ExtensionPointHelper.DescribableExtension
import javaposse.jobdsl.plugin.ExtensionPointHelper.DslExtension
import javaposse.jobdsl.plugin.ExtensionPointHelper.ExtensionPointMethod
import javaposse.jobdsl.plugin.fixtures.FooTrigger
import javaposse.jobdsl.plugin.fixtures.TestContextExtensionPoint
import javaposse.jobdsl.plugin.fixtures.TestContextExtensionPoint2
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Method

class ExtensionPointHelperSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'find extension methods in TriggerContext'() {
        when:
        Set<DslExtension> extensionPoints = ExtensionPointHelper.findExtensionPoints(
                'someTrigger', TriggerContext, 47, 'test'
        ) {}

        then:
        extensionPoints.size() == 1
        extensionPoints.first() instanceof ExtensionPointMethod
        ExtensionPointMethod extensionPoint = (ExtensionPointMethod) extensionPoints.first()
        extensionPoint.extensionPoint instanceof TestContextExtensionPoint
        extensionPoint.method.name == 'someTrigger'
        extensionPoint.method.parameterTypes.length == 4
        extensionPoint.method.parameterTypes[0] == DslEnvironment
        extensionPoint.method.parameterTypes[1] == int
        extensionPoint.method.parameterTypes[2] == String
        extensionPoint.method.parameterTypes[3] == Runnable
    }

    def 'find duplicate methods in StepContext'() {
        when:
        Set<DslExtension> extensionPoints = ExtensionPointHelper.findExtensionPoints('twice', StepContext)

        then:
        extensionPoints.size() == 2
    }

    def 'find generated method with closure parameter'() {
        when:
        Set<DslExtension> extensionPoints = ExtensionPointHelper.findExtensionPoints('foo', TriggerContext) {}

        then:
        extensionPoints.size() == 1
        extensionPoints.first() instanceof DescribableExtension
        DescribableExtension describableExtension = (DescribableExtension) extensionPoints.first()
        describableExtension.describableModel.type == FooTrigger
    }

    def 'find generated method without closure parameter'() {
        when:
        Set<DslExtension> extensionPoints = ExtensionPointHelper.findExtensionPoints('foo', TriggerContext)

        then:
        extensionPoints.size() == 1
        extensionPoints.first() instanceof DescribableExtension
        DescribableExtension describableExtension = (DescribableExtension) extensionPoints.first()
        describableExtension.describableModel.type == FooTrigger
    }

    def 'find generated method with incompatible parameter'() {
        when:
        Set<DslExtension> extensionPoints = ExtensionPointHelper.findExtensionPoints('foo', TriggerContext, 'foo')

        then:
        extensionPoints.size() == 0
    }

    def 'find extension methods for TriggerContext'() {
        when:
        Map<Method, ContextExtensionPoint> methods = ExtensionPointHelper.findExtensionMethods(TriggerContext)

        then:
        methods.size() == 2
        Method method1 = TestContextExtensionPoint.getMethod('someTrigger', DslEnvironment, int, String, Runnable)
        methods[method1] instanceof TestContextExtensionPoint
        Method method2 = TestContextExtensionPoint.getMethod('someTrigger', DslEnvironment, int, Runnable)
        methods[method2] instanceof TestContextExtensionPoint
    }

    def 'find no extension methods for PublisherContext'() {
        when:
        Map<Method, ContextExtensionPoint> methods = ExtensionPointHelper.findExtensionMethods(PublisherContext)

        then:
        methods.size() == 0
    }

    def 'filter parameter types'() {
        given:
        Method method1 = TestContextExtensionPoint.getMethod('someTrigger', DslEnvironment, int, String, Runnable)
        Method method2 = TestContextExtensionPoint.getMethod('test')
        Method method3 = TestContextExtensionPoint.getMethod('testComplexObject', String, int, boolean)

        expect:
        Arrays.equals(ExtensionPointHelper.filterParameterTypes(method1), [int, String, Runnable] as Class[])
        Arrays.equals(ExtensionPointHelper.filterParameterTypes(method2), [] as Class[])
        Arrays.equals(ExtensionPointHelper.filterParameterTypes(method3), [String, int, boolean] as Class[])
    }

    def 'identical signatures'() {
        given:
        Method method1 = TestContextExtensionPoint2.getMethod('testSignatureOne', DslEnvironment)
        Method method2 = TestContextExtensionPoint2.getMethod('testSignatureOne', DslEnvironment, String)
        Method method3 = TestContextExtensionPoint2.getMethod('testSignatureOne', String)
        Method method4 = TestContextExtensionPoint2.getMethod('testSignatureTwo')
        Method method5 = TestContextExtensionPoint2.getMethod('testSignatureTwo', String)

        expect:
        !ExtensionPointHelper.hasIdenticalSignature(method1, method2)
        !ExtensionPointHelper.hasIdenticalSignature(method2, method1)
        ExtensionPointHelper.hasIdenticalSignature(method2, method3)
        ExtensionPointHelper.hasIdenticalSignature(method3, method2)
        !ExtensionPointHelper.hasIdenticalSignature(method1, method3)
        !ExtensionPointHelper.hasIdenticalSignature(method3, method1)
        !ExtensionPointHelper.hasIdenticalSignature(method1, method4)
        !ExtensionPointHelper.hasIdenticalSignature(method4, method1)
        !ExtensionPointHelper.hasIdenticalSignature(method4, method5)
        !ExtensionPointHelper.hasIdenticalSignature(method5, method4)
        !ExtensionPointHelper.hasIdenticalSignature(method3, method5)
        !ExtensionPointHelper.hasIdenticalSignature(method5, method3)
    }

    def 'visible parameter type'() {
        expect:
        ExtensionPointHelper.isVisibleParameterType(String)
        ExtensionPointHelper.isVisibleParameterType(int)
        !ExtensionPointHelper.isVisibleParameterType(DslEnvironment)
        !ExtensionPointHelper.isVisibleParameterType(DslEnvironmentImpl)
    }
}
