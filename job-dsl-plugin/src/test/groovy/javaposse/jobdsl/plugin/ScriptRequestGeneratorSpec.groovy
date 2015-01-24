package javaposse.jobdsl.plugin

import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.FreeStyleProject
import hudson.model.Label
import javaposse.jobdsl.dsl.ScriptRequest
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class ScriptRequestGeneratorSpec extends Specification {
    private static final String SCRIPT = 'my script'

    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'script text'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, false, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        !requests[0].ignoreExisting
    }

    def 'script text ignore existing'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, true, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].ignoreExisting
    }

    def 'script text with additional classpath entry'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('classes').mkdirs()
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, false, 'classes').toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 2
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        !requests[0].ignoreExisting
    }

    def 'script text with additional classpath entry with variable expansion'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('test/classes').mkdirs()
        EnvVars env = new EnvVars([FOO: 'test'])
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, false, '${FOO}/classes').toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 2
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'test/classes/')
        !requests[0].ignoreExisting
    }

    def 'script text with additional classpath entries'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('classes').mkdirs()
        build.workspace.child('output').mkdirs()
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                null, true, SCRIPT, false, 'classes\noutput'
        ).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'output/')
        !requests[0].ignoreExisting
    }

    def 'single target'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('test.groovy').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('test.groovy', false, null, false, null).toList()

        then:
        requests.size() == 1
        requests[0].location == 'test.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        !requests[0].ignoreExisting
    }

    def 'single target with variable'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('test.groovy').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars([FOO: 'test'])
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('${FOO}.groovy', false, null, false, null).toList()

        then:
        requests.size() == 1
        requests[0].location == 'test.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        !requests[0].ignoreExisting
    }

    def 'single target ignore existing'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('test.groovy').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('test.groovy', false, null, true, null).toList()

        then:
        requests.size() == 1
        requests[0].location == 'test.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].ignoreExisting
    }

    def 'multiple target'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('a.groovy').write(SCRIPT, 'UTF-8')
        build.workspace.child('b.groovy').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy\nb.groovy', false, null, false, null
        ).toList()

        then:
        requests.size() == 2
        requests[0].location == 'a.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        !requests[0].ignoreExisting
        requests[1].location == 'b.groovy'
        requests[1].body == null
        requests[1].urlRoots.length == 1
        requests[1].urlRoots[0].toString() == 'workspace://foo/'
        !requests[1].ignoreExisting
    }

    def 'multiple target with wildcard'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('a.groovy').write(SCRIPT, 'UTF-8')
        build.workspace.child('b.groovy').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('*.groovy', false, null, false, null).toList()

        then:
        requests.size() == 2
        requests[0].location == 'a.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        !requests[0].ignoreExisting
        requests[1].location == 'b.groovy'
        requests[1].body == null
        requests[1].urlRoots.length == 1
        requests[1].urlRoots[0].toString() == 'workspace://foo/'
        !requests[1].ignoreExisting
    }

    def 'multiple target with additional classpath entries'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('a.groovy').write(SCRIPT, 'UTF-8')
        build.workspace.child('b.groovy').write(SCRIPT, 'UTF-8')
        build.workspace.child('classes').mkdirs()
        build.workspace.child('output').mkdirs()
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy\nb.groovy', false, null, false, 'classes\noutput'
        ).toList()

        then:
        requests.size() == 2
        requests[0].location == 'a.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'output/')
        !requests[0].ignoreExisting
        requests[1].location == 'b.groovy'
        requests[1].body == null
        requests[1].urlRoots.length == 3
        requests[1].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'output/')
        !requests[1].ignoreExisting
    }

    def 'additional classpath entries with pattern'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('a.groovy').write(SCRIPT, 'UTF-8')
        build.workspace.child('lib/a.jar').write(SCRIPT, 'UTF-8')
        build.workspace.child('lib/b.jar').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy\nb.groovy', false, null, false, 'lib/*.jar'
        ).toList()

        then:
        requests.size() == 1
        requests[0].location == 'a.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'lib/a.jar')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'lib/b.jar')
        !requests[0].ignoreExisting
    }

    def 'additional classpath entries with pattern building on remote'() {
        setup:
        jenkinsRule.createSlave('label1', null)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('foo')
        job.assignedLabel = Label.get('label1')
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(job)
        build.workspace.child('a.groovy').write(SCRIPT, 'UTF-8')
        build.workspace.child('lib/a.jar').write(SCRIPT, 'UTF-8')
        build.workspace.child('lib/b.jar').write(SCRIPT, 'UTF-8')
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy\nb.groovy', false, null, false, 'lib/*.jar'
        ).toList()

        then:
        requests.size() == 1
        requests[0].location == 'a.groovy'
        requests[0].body == null
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace://foo/'
        URL tempDirUrl = new File(System.getProperty('java.io.tmpdir')).toURI().toURL()
        requests[0].urlRoots[1] =~ "${tempDirUrl}jobdsl.*\\.jar"
        requests[0].urlRoots[2] =~ "${tempDirUrl}jobdsl.*\\.jar"
        !requests[0].ignoreExisting
        new File(requests[0].urlRoots[1].toURI()).exists()
        new File(requests[0].urlRoots[2].toURI()).exists()

        when:
        generator.close()

        then:
        !(new File(requests[0].urlRoots[1].toURI()).exists())
        !(new File(requests[0].urlRoots[2].toURI()).exists())
    }
}
