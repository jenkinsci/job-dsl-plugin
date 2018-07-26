package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MavenWrapperContextSpec extends Specification {
    private final JobManagement mockJobManagement = Mock(JobManagement)
    private final Item item = Mock(Item)
    private final MavenWrapperContext context = new MavenWrapperContext(mockJobManagement, item)

    def 'configure m2release plugin with least args'() {
        when:
        context.mavenRelease()

        then:
        context.wrapperNodes.size() == 1
        def m2releaseNode = context.wrapperNodes[0]

        m2releaseNode.scmUserEnvVar[0].value() == ''
        m2releaseNode.scmPasswordEnvVar[0].value() == ''
        m2releaseNode.releaseEnvVar[0].value() == 'IS_M2RELEASEBUILD'
        m2releaseNode.releaseGoals[0].value() == '-Dresume=false release:prepare release:perform'
        m2releaseNode.dryRunGoals[0].value() == '-Dresume=false -DdryRun=true release:prepare'
        m2releaseNode.selectCustomScmCommentPrefix[0].value() == false
        m2releaseNode.selectAppendHudsonUsername[0].value() == false
        m2releaseNode.selectScmCredentials[0].value() == false
        m2releaseNode.numberOfReleaseBuildsToKeep[0].value() == 1

        1 * mockJobManagement.requirePlugin('m2release')
    }

    def 'configure m2release plugin with all args'() {
        when:
        context.mavenRelease {
            scmUserEnvVar 'MY_USER_ENV'
            scmPasswordEnvVar 'MY_PASSWORD_ENV'
            releaseEnvVar 'RELEASE_ENV'
            releaseGoals 'release:prepare release:perform'
            dryRunGoals '-DdryRun=true release:prepare'
            selectCustomScmCommentPrefix()
            selectAppendJenkinsUsername()
            selectScmCredentials()
            numberOfReleaseBuildsToKeep 10
        }

        then:
        context.wrapperNodes.size() == 1
        def m2releaseNode = context.wrapperNodes[0]

        m2releaseNode.scmUserEnvVar[0].value() == 'MY_USER_ENV'
        m2releaseNode.scmPasswordEnvVar[0].value() == 'MY_PASSWORD_ENV'
        m2releaseNode.releaseEnvVar[0].value() == 'RELEASE_ENV'
        m2releaseNode.releaseGoals[0].value() == 'release:prepare release:perform'
        m2releaseNode.dryRunGoals[0].value() == '-DdryRun=true release:prepare'
        m2releaseNode.selectCustomScmCommentPrefix[0].value() == true
        m2releaseNode.selectAppendHudsonUsername[0].value() == true
        m2releaseNode.selectScmCredentials[0].value() == true
        m2releaseNode.numberOfReleaseBuildsToKeep[0].value() == 10

        1 * mockJobManagement.requirePlugin('m2release')
    }
}
