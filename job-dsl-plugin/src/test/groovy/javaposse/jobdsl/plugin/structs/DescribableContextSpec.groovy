package javaposse.jobdsl.plugin.structs

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.plugin.fixtures.ABean
import javaposse.jobdsl.plugin.fixtures.ADescribable
import javaposse.jobdsl.plugin.fixtures.ADuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.DeprecatedTrigger
import javaposse.jobdsl.plugin.fixtures.DummyTrigger
import jenkins.mvn.DefaultSettingsProvider
import jenkins.mvn.FilePathSettingsProvider
import org.jenkinsci.plugins.structs.describable.DescribableModel
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class DescribableContextSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    JobManagement jobManagement = Mock(JobManagement)

    def 'no argument'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aString()

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aString'
        e.arguments.length == 0
        !e.static
    }

    def 'more than one argument'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aString('foo', 'bar')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aString'
        e.arguments == ['foo', 'bar']
        !e.static
    }

    def 'unknown method'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.foo('foo')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'foo'
        e.arguments == ['foo']
        !e.static
    }

    def 'string property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aString(1)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aString'
        e.arguments == [1]
        !e.static
    }

    def 'string property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aString('foo')
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aString == 'foo'
    }

    def 'string property with GString'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)
        String foo = 'foo'

        when:
        context.aString("${foo}")
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aString == 'foo'
    }

    def 'string property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aString(null)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aString == null
    }

    def 'boolean property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aBoolean('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aBoolean'
        e.arguments == ['true']
        !e.static
    }

    def 'boolean property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aBoolean(true)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aBoolean
    }

    def 'boolean property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aBoolean(null)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aBoolean'
        e.arguments == [null]
        !e.static
    }

    def 'integer property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anInt('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'anInt'
        e.arguments == ['true']
        !e.static
    }

    def 'integer property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anInteger(4711)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.anInteger == 4711
    }

    def 'integer property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anInteger(null)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.anInteger == null
    }

    def 'enum property with invalid value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anEnum('true')

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ "invalid enum value 'true', must be one of 'NEW', 'RUNNABLE', 'BLOCKED', 'WAITING', 'TIMED_WAITING"
    }

    def 'enum property with string value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anEnum('NEW')
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.anEnum == Thread.State.NEW
    }

    @Ignore('https://github.com/jenkinsci/structs-plugin/pull/14')
    def 'enum property with GString value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)
        String foo = 'NEW'

        when:
        context.anEnum("${foo}")
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.anEnum == Thread.State.NEW
    }

    def 'enum property with enum value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anEnum(Thread.State.NEW)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.anEnum == Thread.State.NEW
    }

    def 'enum property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.anEnum(null)

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ "invalid enum value 'null', must be one of 'NEW', 'RUNNABLE', 'BLOCKED', 'WAITING', 'TIMED_WAITING"
    }

    def 'heterogeneous property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneous('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHeterogeneous'
        e.arguments == ['true']
        !e.static
    }

    def 'heterogeneous property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneous {
            defaultSettingsProvider {}
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneous instanceof DefaultSettingsProvider
    }

    def 'heterogeneous property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneous(null)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneous == null
    }

    def 'empty heterogeneous property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneous {}
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneous == null
    }

    def 'last heterogeneous wins'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneous {
            filePathSettingsProvider {
                path('foo')
            }
            defaultSettingsProvider {}
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneous instanceof DefaultSettingsProvider
    }

    def 'heterogeneous list property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneousList('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHeterogeneousList'
        e.arguments == ['true']
        !e.static
    }

    def 'heterogeneous list property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneousList(null)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHeterogeneousList'
        e.arguments == [null]
        !e.static
    }

    def 'heterogeneous list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneousList {
            defaultSettingsProvider {}
            filePathSettingsProvider {
                path('foo')
            }
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneousList.size() == 2
        instance.aHeterogeneousList[0] instanceof DefaultSettingsProvider
        instance.aHeterogeneousList[1] instanceof FilePathSettingsProvider
    }

    def 'empty heterogeneous list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHeterogeneousList {}
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneousList.empty
    }

    def 'unset heterogeneous list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHeterogeneousList.empty
    }

    def 'homogeneous property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneous('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHomogeneous'
        e.arguments == ['true']
        !e.static
    }

    def 'homogeneous property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneous {
            foo('foo')
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneous instanceof ADescribable
        instance.aHomogeneous.foo == 'foo'
    }

    def 'homogeneous property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneous(null)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneous == null
    }

    def 'empty homogeneous property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneous {}
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneous instanceof ADescribable
    }

    def 'homogeneous list property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousList('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHomogeneousList'
        e.arguments == ['true']
        !e.static
    }

    def 'homogeneous list property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousList(null)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHomogeneousList'
        e.arguments == [null]
        !e.static
    }

    def 'homogeneous list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousList {
            aDescribable {
                foo('one')
            }
            aDescribable {
                bar(13)
            }
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousList.size() == 2
        instance.aHomogeneousList[0] instanceof ADescribable
        instance.aHomogeneousList[0].foo == 'one'
        instance.aHomogeneousList[0].bar == 0
        instance.aHomogeneousList[1] instanceof ADescribable
        instance.aHomogeneousList[1].foo == null
        instance.aHomogeneousList[1].bar == 13
    }

    def 'empty homogeneous property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousList {}
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousList.empty
    }

    def 'unset homogeneous property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousList.empty
    }

    def 'homogeneous bean property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBean('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHomogeneousBean'
        e.arguments == ['true']
        !e.static
    }

    def 'homogeneous bean property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBean {
            prop('foo')
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousBean instanceof ABean
        instance.aHomogeneousBean.prop == 'foo'
    }

    def 'homogeneous bean property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBean(null)
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousBean == null
    }

    def 'empty homogeneous bean property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBean {}
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousBean instanceof ABean
    }

    def 'homogeneous bean list property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBeanList('true')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHomogeneousBeanList'
        e.arguments == ['true']
        !e.static
    }

    def 'homogeneous bean list property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBeanList(null)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'aHomogeneousBeanList'
        e.arguments == [null]
        !e.static
    }

    def 'homogeneous bean list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBeanList {
            aBean {
                prop('one')
            }
            aBean {
                prop('two')
            }
        }
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousBeanList.size() == 2
        instance.aHomogeneousBeanList[0] instanceof ABean
        instance.aHomogeneousBeanList[0].prop == 'one'
        instance.aHomogeneousBeanList[1] instanceof ABean
        instance.aHomogeneousBeanList[1].prop == 'two'
    }

    def 'empty homogeneous bean property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.aHomogeneousBeanList {}
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousBeanList.empty
    }

    def 'unset homogeneous bean list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.aHomogeneousBeanList.empty
    }

    def 'string list property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.stringList(this)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'stringList'
        e.arguments == [this]
        !e.static
    }

    def 'string list property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.stringList(null)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'stringList'
        e.arguments == [null]
        !e.static
    }

    def 'string list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.stringList(['foo', 'bar'])
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.stringList.size() == 2
        instance.stringList[0] == 'foo'
        instance.stringList[1] == 'bar'
    }

    def 'string list property with GString'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)
        String foo = 'foo'
        String bar = 'bar'

        when:
        context.stringList(["${foo}", "${bar}"])
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.stringList.size() == 2
        instance.stringList[0] == 'foo'
        instance.stringList[1] == 'bar'
    }

    def 'empty string property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.stringList([])
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.stringList.empty
    }

    def 'unset string property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.stringList.empty
    }

    def 'enum list property with invalid type'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.enumList(this)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'enumList'
        e.arguments == [this]
        !e.static
    }

    def 'enum list property with null value'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.enumList(null)

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableContext
        e.method == 'enumList'
        e.arguments == [null]
        !e.static
    }

    def 'enum list property'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.enumList(['NEW', 'BLOCKED'])
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.enumList.size() == 2
        instance.enumList[0] == Thread.State.NEW
        instance.enumList[1] == Thread.State.BLOCKED
    }

    @Ignore('https://github.com/jenkinsci/structs-plugin/pull/14')
    def 'enum list property with GString'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)
        String one = 'NEW'
        String two = 'BLOCKED'

        when:
        context.enumList(["${one}", "${two}"])
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.enumList.size() == 2
        instance.enumList[0] == Thread.State.NEW
        instance.enumList[1] == Thread.State.BLOCKED
    }

    def 'empty enum property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        context.enumList([])
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.enumList.empty
    }

    def 'unset enum property list'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DummyTrigger), jobManagement)

        when:
        def instance = context.createInstance()

        then:
        instance != null
        instance instanceof DummyTrigger
        instance.enumList.empty
    }

    def 'required parameter missing'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(ADuplicateBuilder), jobManagement)

        when:
        context.createInstance()

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'the following options are required and must be specified: foo'
    }

    def 'log deprecation warning'() {
        setup:
        DescribableContext context = new DescribableContext(new DescribableModel(DeprecatedTrigger), jobManagement)

        when:
        context.deprecatedOption('foo')

        then:
        1 * jobManagement.logDeprecationWarning('deprecatedOption')
    }
}
