package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.common.MavenContext
import spock.lang.Specification

public class MavenHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    MavenHelper helper = new MavenHelper(mockActions, JobType.Maven)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'can run rootPOM'() {
        when:
        helper.rootPOM("my_module/pom.xml")

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run rootPOM twice'() {
        when:
        helper.rootPOM("pom.xml")
        helper.rootPOM("my_module/pom.xml")

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run rootPOM for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.rootPOM("pom.xml")

        then:
        thrown(IllegalStateException)
    }

    def 'rootPOM constructs xml'() {
        when:
        def action = helper.rootPOM("my_module/pom.xml")
        action.execute(root)

        then:
        root.rootPOM.size() == 1
        root.rootPOM[0].value() == "my_module/pom.xml"
    }

    def 'can run goals'() {
        when:
        helper.goals("clean verify")

        then:
        1 * mockActions.add(_)
    }

    def 'run goals twice'() {
        when:
        def action = helper.goals("clean")
        helper.goals("verify")
        action.execute(root)

        then:
        1 * mockActions.add(_)
        root.goals.size() == 1
        root.goals[0].value() == "clean verify"
    }

    def 'cannot run goals for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.goals("package")

        then:
        thrown(IllegalStateException)
    }

    def 'goals constructs xml'() {
        when:
        def action = helper.goals("clean verify")
        action.execute(root)

        then:
        root.goals.size() == 1
        root.goals[0].value() == "clean verify"
    }

    def 'can run mavenOpts'() {
        when:
        helper.mavenOpts("-DskipTests")

        then:
        1 * mockActions.add(_)
    }

    def 'run mavenOpts twice'() {
        when:
        def action = helper.mavenOpts("-Xms512m")
        helper.mavenOpts("-Xmx1024m")
        action.execute(root)

        then:
        1 * mockActions.add(_)
        root.mavenOpts.size() == 1
        root.mavenOpts[0].value() == "-Xms512m -Xmx1024m"
    }

    def 'cannot run mavenOpts for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.mavenOpts("-Xmx512m")

        then:
        thrown(IllegalStateException)
    }

    def 'mavenOpts constructs xml'() {
        when:
        def action = helper.mavenOpts("-DskipTests")
        action.execute(root)

        then:
        root.mavenOpts.size() == 1
        root.mavenOpts[0].value() == "-DskipTests"
    }

    def 'can run perModuleEmail'() {
        when:
        helper.perModuleEmail(false)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run perModuleEmail twice'() {
        when:
        helper.perModuleEmail(false)
        helper.perModuleEmail(true)

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run perModuleEmail for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.perModuleEmail(false)

        then:
        thrown(IllegalStateException)
    }

    def 'perModuleEmail constructs xml'() {
        when:
        def action = helper.perModuleEmail(false)
        action.execute(root)

        then:
        root.perModuleEmail.size() == 1
        root.perModuleEmail[0].value() == false
    }

    def 'can run archivingDisabled'() {
        when:
        helper.archivingDisabled(true)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run archivingDisabled twice'() {
        when:
        helper.archivingDisabled(true)
        helper.archivingDisabled(false)

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run archivingDisabled for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.archivingDisabled(false)

        then:
        thrown(IllegalStateException)
    }

    def 'archivingDisabled constructs xml'() {
        when:
        def action = helper.archivingDisabled(true)
        action.execute(root)

        then:
        root.archivingDisabled.size() == 1
        root.archivingDisabled[0].value() == true
    }

    def 'can run runHeadless'() {
        when:
        helper.runHeadless(true)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run runHeadless twice'() {
        when:
        helper.runHeadless(true)
        helper.runHeadless(false)

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run runHeadless for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.runHeadless(false)

        then:
        thrown(IllegalStateException)
    }

    def 'runHeadless constructs xml'() {
        when:
        def action = helper.runHeadless(true)
        action.execute(root)

        then:
        root.runHeadless.size() == 1
        root.runHeadless[0].value() == true
    }

    def 'cannot run localRepository for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.localRepository(MavenContext.LocalRepositoryLocation.LocalToExecutor)

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run localRepository with null argument'() {
        when:
        helper.localRepository(null)

        then:
        thrown(NullPointerException)
    }

    def 'localRepository constructs xml for LocalToExecutor'() {
        when:
        def action = helper.localRepository(MavenContext.LocalRepositoryLocation.LocalToExecutor)
        action.execute(root)

        then:
        root.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerExecutorLocalRepositoryLocator'
    }

    def 'localRepository constructs xml for LocalToWorkspace'() {
        when:
        def action = helper.localRepository(MavenContext.LocalRepositoryLocation.LocalToWorkspace)
        action.execute(root)

        then:
        root.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerJobLocalRepositoryLocator'
    }

    def 'cannot run preBuildSteps for freestyle jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.preBuildSteps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'can add preBuildSteps'() {
        when:
        def action = helper.preBuildSteps {
            shell("ls")
        }
        action.execute(root)

        then:
        root.prebuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        root.prebuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'cannot run postBuildSteps for freestyle jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.postBuildSteps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'can add postBuildSteps'() {
        when:
        def action = helper.postBuildSteps {
            shell("ls")
        }
        action.execute(root)

        then:
        root.postbuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        root.postbuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'can run mavenInstallation'() {
        when:
        helper.mavenInstallation('test')

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run mavenInstallation for free style jobs'() {
        setup:
        MavenHelper helper = new MavenHelper(mockActions, JobType.Freeform)

        when:
        helper.mavenInstallation('test')

        then:
        thrown(IllegalStateException)
    }

    def 'mavenInstallation constructs xml'() {
        when:
        def action = helper.mavenInstallation('test')
        action.execute(root)

        then:
        root.mavenName.size() == 1
        root.mavenName[0].value() == 'test'
    }
}
