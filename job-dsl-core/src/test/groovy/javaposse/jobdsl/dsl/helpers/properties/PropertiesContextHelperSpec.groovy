package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification
import spock.lang.Unroll

class PropertiesContextHelperSpec extends Specification {
    PropertiesContextHelper helper = new PropertiesContextHelper([], JobType.Freeform)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))

    def 'environments work with map arg'() {
        when:
        helper.properties {
            environmentVariables([
                    key1: 'val1',
                    key2: 'val2'
            ])
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
    }

    def 'environments work with context'() {
        when:
        helper.properties {
            environmentVariables {
                envs([key1: 'val1', key2: 'val2'])
                env 'key3', 'val3'
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
    }

    def 'environments work with combination'() {
        when:
        helper.properties {
            environmentVariables([key4: 'val4']) {
                env 'key3', 'val3'
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key4=val4')
    }

    def 'environment from groovy script'() {
        when:
        helper.properties {
            environmentVariables {
                groovy '[foo: "bar"]'
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    def 'environment from map and groovy script'() {
        when:
        helper.properties {
            environmentVariables {
                envs([key1: 'val1', key2: 'val2'])
                env 'key3', 'val3'
                groovy '[foo: "bar"]'
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    @Unroll
    def 'environment from #method'(content, method, xmlElement) {
        when:
        helper.properties {
            environmentVariables {
                "$method"(content)
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0]."$xmlElement"[0].value() == content

        where:
        content          || method               || xmlElement
        'some.properties' | 'propertiesFile'      | 'propertiesFilePath'
        '/some/path'      | 'scriptFile'          | 'scriptFilePath'
        'echo "Yeah"'     | 'script'              | 'scriptContent'
        true              | 'loadFilesFromMaster' | 'loadFilesFromMaster'
    }

    @Unroll
    def 'environment sets #method to #content'(method, content, xmlElement) {
        when:
        helper.properties {
            environmentVariables {
                "${method}"(content)
            }
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'EnvInjectJobProperty'[0]."${xmlElement}"[0].value() == content

        where:
        method               || content || xmlElement
        'keepSystemVariables' | true     | 'keepJenkinsSystemVariables'
        'keepSystemVariables' | false    | 'keepJenkinsSystemVariables'
        'keepBuildVariables'  | true     | 'keepBuildVariables'
        'keepBuildVariables'  | false    | 'keepBuildVariables'
    }

    def 'throttle concurrents enabled as project alone'() {
        when:
        helper.properties {
            throttleConcurrentBuilds {
                maxPerNode 1
                maxTotal 2
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def throttleNode = root.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.maxConcurrentPerNode[0].value() == 1
        throttleNode.maxConcurrentTotal[0].value() == 2
        throttleNode.throttleEnabled[0].value() == 'true'
        throttleNode.throttleOption[0].value() == 'project'
        throttleNode.categories[0].children().size() == 0
    }

    def 'throttle concurrents disabled'() {
        when:
        helper.properties {
            throttleConcurrentBuilds {
                throttleDisabled()
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def throttleNode = root.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.throttleEnabled[0].value() == 'false'
    }

    def 'throttle concurrents enabled as part of categories'() {
        when:
        helper.properties {
            throttleConcurrentBuilds {
                maxPerNode 1
                maxTotal 2
                categories(['cat-1', 'cat-2'])
            }
        }
        executeHelperActionsOnRootNode()

        then:
        def throttleNode = root.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.maxConcurrentPerNode[0].value() == 1
        throttleNode.maxConcurrentTotal[0].value() == 2
        throttleNode.throttleEnabled[0].value() == 'true'
        throttleNode.throttleOption[0].value() == 'category'
        throttleNode.categories[0].children().size() == 2
        throttleNode.categories[0].string[0].value() == 'cat-1'
        throttleNode.categories[0].string[1].value() == 'cat-2'
    }

    def 'build blocker xml'() {
        when:
        helper.properties {
            blockOn("MyProject")
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0].useBuildBlocker[0].value() == 'true'
        root.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0].blockingJobs[0].value() == 'MyProject'
    }

    def 'priority constructs xml'() {
        when:
        helper.properties {
            priority(99)
        }
        executeHelperActionsOnRootNode()

        then:
        root.properties.'hudson.queueSorter.PrioritySorterJobProperty'.priority[0].value() == 99
    }

    private executeHelperActionsOnRootNode() {
        helper.withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }
}
