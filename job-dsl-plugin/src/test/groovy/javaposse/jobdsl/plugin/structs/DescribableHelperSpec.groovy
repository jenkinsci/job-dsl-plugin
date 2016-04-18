package javaposse.jobdsl.plugin.structs

import hudson.model.Describable
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.helpers.scm.RemoteContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.plugin.fixtures.ADuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.BDuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.BrokenContext
import javaposse.jobdsl.plugin.fixtures.DummyTrigger
import javaposse.jobdsl.plugin.fixtures.FooTrigger
import javaposse.jobdsl.plugin.fixtures.InvalidContext
import jenkins.triggers.ReverseBuildTrigger
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

class DescribableHelperSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'find descriptors for unknown name'() {
        when:
        def descriptors = DescribableHelper.findDescribableModels(TriggerContext, 'unknown')

        then:
        descriptors != null
        descriptors.empty
    }

    def 'find descriptors for uncapitalized simple name'() {
        when:
        def describableModels = DescribableHelper.findDescribableModels(TriggerContext, 'reverseBuildTrigger')

        then:
        describableModels != null
        describableModels.size() == 1
        describableModels.first().type == ReverseBuildTrigger
    }

    def 'find descriptors for symbol'() {
        when:
        def describableModels = DescribableHelper.findDescribableModels(TriggerContext, 'dummy')

        then:
        describableModels != null
        describableModels.size() == 1
        describableModels.first().type == DummyTrigger
    }

    def 'find descriptors for duplicate symbol'() {
        when:
        def describableModels = DescribableHelper.findDescribableModels(StepContext, 'duplicate')

        then:
        describableModels != null
        describableModels.size() == 2
        describableModels.any { it.type == ADuplicateBuilder }
        describableModels.any { it.type == BDuplicateBuilder }
    }

    def 'symbol has precedence over uncapitalized simple name'() {
        when:
        def describableModels = DescribableHelper.findDescribableModels(TriggerContext, 'foo')

        then:
        describableModels != null
        describableModels.size() == 1
        describableModels.first().type == FooTrigger
    }

    def 'find descriptors for context without type'() {
        when:
        def descriptors = DescribableHelper.findDescribableModels(RemoteContext, 'foo')

        then:
        descriptors != null
        descriptors.empty
    }

    def 'find descriptors for context unknown type'() {
        when:
        DescribableHelper.findDescribableModels(BrokenContext, 'foo')

        then:
        Exception e = thrown(DslException)
        e.message.contains('a.Foo')
        e.message.contains(BrokenContext.name)
    }

    def 'find descriptors for illegal context type'() {
        when:
        DescribableHelper.findDescribableModels(InvalidContext, 'foo')

        then:
        Exception e = thrown(DslException)
        e.message.contains('java.util.TimeZone')
        e.message.contains(InvalidContext.name)
        e.message.contains(Describable.name)
    }

    def 'is optional closure argument'() {
        expect:
        DescribableHelper.isOptionalClosureArgument()
        DescribableHelper.isOptionalClosureArgument {}
        !DescribableHelper.isOptionalClosureArgument('foo')
        !DescribableHelper.isOptionalClosureArgument('foo') {}
    }

    def 'uncapitalize class names'() {
        expect:
        DescribableHelper.uncapitalize(DescribableHelper) == 'describableHelper'
        DescribableHelper.uncapitalize(URL) == 'url'
        DescribableHelper.uncapitalize(URLClassLoader) == 'urlClassLoader'
        DescribableHelper.uncapitalize(Character.UnicodeBlock) == 'unicodeBlock'
    }
}
