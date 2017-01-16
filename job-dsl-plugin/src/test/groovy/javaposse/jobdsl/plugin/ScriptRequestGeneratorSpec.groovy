package javaposse.jobdsl.plugin

import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.FreeStyleProject
import hudson.model.Label
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.ScriptRequest
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static javaposse.jobdsl.plugin.ScriptRequestGenerator.getAbsolutePath

@Unroll
class ScriptRequestGeneratorSpec extends Specification {
    private static final String SCRIPT = 'my script'
    private static final String SCRIPT_A = 'my script A'
    private static final String SCRIPT_B = 'my script B'

    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    @Shared
    AbstractBuild build

    @Shared
    AbstractBuild remoteBuild

    def setupSpec() {
        build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
        build.workspace.child('classes').mkdirs()
        build.workspace.child('test/classes').mkdirs()
        build.workspace.child('output').mkdirs()
        build.workspace.child('a.groovy').write(SCRIPT_A, 'UTF-8')
        build.workspace.child('b.groovy').write(SCRIPT_B, 'UTF-8')
        build.workspace.child('lib/a.jar').write(SCRIPT, 'UTF-8')
        build.workspace.child('lib/b.jar').write(SCRIPT, 'UTF-8')

        jenkinsRule.createSlave('label1', null)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('remote-foo')
        job.assignedLabel = Label.get('label1')
        remoteBuild = jenkinsRule.buildAndAssertSuccess(job)
        remoteBuild.workspace.child('a.groovy').write(SCRIPT_A, 'UTF-8')
        remoteBuild.workspace.child('lib/a.jar').write(SCRIPT, 'UTF-8')
        remoteBuild.workspace.child('lib/b.jar').write(SCRIPT, 'UTF-8')
    }

    def 'script text'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, false, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        !requests[0].ignoreExisting
        requests[0].scriptPath == null
    }

    def 'script text ignore existing'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, true, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].ignoreExisting
        requests[0].scriptPath == null
    }

    def 'script text with additional classpath entry'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, false, 'classes').toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 2
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        !requests[0].ignoreExisting
        requests[0].scriptPath == null
    }

    def 'script text with additional classpath entry with variable expansion'() {
        setup:
        EnvVars env = new EnvVars([FOO: 'test'])
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(null, true, SCRIPT, false, '${FOO}/classes').toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 2
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'test/classes/')
        !requests[0].ignoreExisting
        requests[0].scriptPath == null
    }

    def 'script text with additional classpath entries'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                null, true, SCRIPT, false, 'classes\noutput'
        ).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'output/')
        !requests[0].ignoreExisting
        requests[0].scriptPath == null
    }

    def 'single target'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('a.groovy', false, null, false, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
    }

    def 'single target with variable'() {
        setup:
        EnvVars env = new EnvVars([FOO: 'a'])
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('${FOO}.groovy', false, null, false, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
    }

    def 'single target ignore existing'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('a.groovy', false, null, true, null).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
    }

    def 'multiple target'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy\nb.groovy', false, null, false, null
        ).toList()

        then:
        requests.size() == 2
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
        requests[1].location == null
        requests[1].body == SCRIPT_B
        requests[1].urlRoots.length == 1
        requests[1].urlRoots[0].toString() == 'workspace:/'
        !requests[1].ignoreExisting
        requests[1].scriptPath == getAbsolutePath(build.workspace.child('b.groovy'))
    }

    def 'multiple target with wildcard'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('*.groovy', false, null, false, null).toList()

        then:
        requests.size() == 2
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 1
        requests[0].urlRoots[0].toString() == 'workspace:/'
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
        requests[1].location == null
        requests[1].body == SCRIPT_B
        requests[1].urlRoots.length == 1
        requests[1].urlRoots[0].toString() == 'workspace:/'
        !requests[1].ignoreExisting
        requests[1].scriptPath == getAbsolutePath(build.workspace.child('b.groovy'))
    }

    def 'multiple target with additional classpath entries'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy\nb.groovy', false, null, false, 'classes\noutput'
        ).toList()

        then:
        requests.size() == 2
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'output/')
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
        requests[1].location == null
        requests[1].body == SCRIPT_B
        requests[1].urlRoots.length == 3
        requests[1].urlRoots[0].toString() == 'workspace:/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'classes/')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'output/')
        !requests[1].ignoreExisting
        requests[1].scriptPath == getAbsolutePath(build.workspace.child('b.groovy'))
    }

    def 'additional classpath entries with pattern'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy', false, null, false, 'lib/*.jar'
        ).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace:/'
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'lib/a.jar')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'lib/b.jar')
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(build.workspace.child('a.groovy'))
    }

    def 'additional classpath entries with pattern building on remote'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(remoteBuild.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                'a.groovy', false, null, false, 'lib/*.jar'
        ).toList()

        then:
        requests.size() == 1
        requests[0].location == null
        requests[0].body == SCRIPT_A
        requests[0].urlRoots.length == 3
        requests[0].urlRoots[0].toString() == 'workspace:/'
        URL tempDirUrl = new File(System.getProperty('java.io.tmpdir')).toURI().toURL()
        requests[0].urlRoots[1] =~ "${tempDirUrl}jobdsl.*\\.jar"
        requests[0].urlRoots[2] =~ "${tempDirUrl}jobdsl.*\\.jar"
        !requests[0].ignoreExisting
        requests[0].scriptPath == getAbsolutePath(remoteBuild.workspace.child('a.groovy'))
        new File(requests[0].urlRoots[1].toURI()).exists()
        new File(requests[0].urlRoots[2].toURI()).exists()

        when:
        generator.close()

        then:
        !(new File(requests[0].urlRoots[1].toURI()).exists())
        !(new File(requests[0].urlRoots[2].toURI()).exists())
    }

    def 'multiple additional classpath entries with indentation #additionalClasspath'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests(
                null, true, SCRIPT, false, additionalClasspath
        ).toList()

        then:
        requests.size() == 1
        requests[0].urlRoots[1] == new URL(build.workspace.toURI().toURL(), 'lib/a.jar')
        requests[0].urlRoots[2] == new URL(build.workspace.toURI().toURL(), 'lib/b.jar')

        where:
        additionalClasspath << ['lib/a.jar\n\tlib/b.jar', 'lib/a.jar\nlib/b.jar\t', 'lib/a.jar   \n   lib/b.jar']
    }

    def 'file not found'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        generator.getScriptRequests('x.groovy', false, null, false, null).toList()

        then:
        Exception e = thrown(DslException)
        e.message == 'no Job DSL script(s) found at x.groovy'
    }

    def 'ignore file not found'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('x.groovy', false, null, false, true, null).toList()

        then:
        requests.empty
    }

    def 'pattern does not match anything'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        generator.getScriptRequests('*.foo', false, null, false, null).toList()

        then:
        Exception e = thrown(DslException)
        e.message == 'no Job DSL script(s) found at *.foo'
    }

    def 'ignore empty wildcared'() {
        setup:
        EnvVars env = new EnvVars()
        ScriptRequestGenerator generator = new ScriptRequestGenerator(build.workspace, env)

        when:
        List<ScriptRequest> requests = generator.getScriptRequests('*.foo', false, null, false, true, null).toList()

        then:
        requests.empty
    }
}
