package javaposse.jobdsl.plugin.structs

import hudson.model.Describable
import hudson.triggers.SCMTrigger
import hudson.triggers.TimerTrigger
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.helpers.scm.RemoteContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.plugin.fixtures.ABean
import javaposse.jobdsl.plugin.fixtures.ADuplicateBean
import javaposse.jobdsl.plugin.fixtures.ADuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.BDuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.Boolean
import javaposse.jobdsl.plugin.fixtures.BrokenContext
import javaposse.jobdsl.plugin.fixtures.DummyTrigger
import javaposse.jobdsl.plugin.fixtures.Foo
import javaposse.jobdsl.plugin.fixtures.FooTrigger
import javaposse.jobdsl.plugin.fixtures.IntegerTrigger
import javaposse.jobdsl.plugin.fixtures.InvalidContext
import javaposse.jobdsl.plugin.fixtures.SomeTrigger
import jenkins.triggers.ReverseBuildTrigger
import org.jenkinsci.plugins.structs.describable.DescribableModel
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
        Collection<DescribableModel> models = DescribableHelper.findDescribableModels(BrokenContext, 'foo')

        then:
        models.empty
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

    def 'models indexed by symbolic name'() {
        given:
        DescribableModel model1 = new DescribableModel(ABean)
        DescribableModel model2 = new DescribableModel(FooTrigger)

        when:
        Map<String, DescribableModel> models = DescribableHelper.findDescribableModels([model1, model2], ['foo'])

        then:
        models.size() == 2
        models['aBean'] == model1
        models['bar'] == model2
    }

    def 'models indexed by symbolic name with duplicate symbol'() {
        given:
        DescribableModel model1 = new DescribableModel(ADuplicateBuilder)
        DescribableModel model2 = new DescribableModel(BDuplicateBuilder)

        when:
        Map<String, DescribableModel> models = DescribableHelper.findDescribableModels([model1, model2])

        then:
        models.size() == 2
        models['aDuplicateBuilder'].type == ADuplicateBuilder
        models['bDuplicateBuilder'].type == BDuplicateBuilder
    }

    def 'models indexed by symbolic name without symbol and with duplicate uncapitalized class name'() {
        given:
        DescribableModel model1 = new DescribableModel(ADuplicateBean)
        DescribableModel model2 = new DescribableModel(javaposse.jobdsl.plugin.fixtures.other.ADuplicateBean)

        when:
        Map<String, DescribableModel> models = DescribableHelper.findDescribableModels([model1, model2])

        then:
        models.isEmpty()
    }

    def 'models indexed by symbolic name with symbol preceding uncapitalized class name'() {
        given:
        DescribableModel model1 = new DescribableModel(Foo)
        DescribableModel model2 = new DescribableModel(FooTrigger)

        when:
        Map<String, DescribableModel> models = DescribableHelper.findDescribableModels([model1, model2], ['bar'])

        then:
        models.size() == 1
        models['foo'].type == FooTrigger
    }

    def 'models indexed by symbolic name without keywords'() {
        given:
        DescribableModel model1 = new DescribableModel(Boolean)
        DescribableModel model2 = new DescribableModel(IntegerTrigger)

        when:
        Map<String, DescribableModel> models = DescribableHelper.findDescribableModels([model1, model2])

        then:
        models.size() == 1
        models['integerTrigger'].type == IntegerTrigger
    }

    def 'models indexed by symbolic name from context'() {
        when:
        Map<String, DescribableModel> models = DescribableHelper.findDescribableModels(TriggerContext)

        then:
        models.size() == 7
        models['dummy'].type == DummyTrigger
        models['foo'].type == FooTrigger
        models['bar'].type == FooTrigger
        models['timerTrigger'].type == TimerTrigger
        models['scmTrigger'].type == SCMTrigger
        models['integerTrigger'].type == IntegerTrigger
        models['someTrigger'].type == SomeTrigger
    }
}
