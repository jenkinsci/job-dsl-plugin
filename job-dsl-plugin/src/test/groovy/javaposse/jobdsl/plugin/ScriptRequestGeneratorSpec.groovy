package javaposse.jobdsl.plugin

import hudson.EnvVars
import hudson.model.AbstractBuild
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
        requests[0].urlRoots[1].toString() == 'workspace://foo/classes/'
        !requests[0].ignoreExisting
    }

    def 'script text with additional classpath entry with variable expansion'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
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
        requests[0].urlRoots[1].toString() == 'workspace://foo/test/classes/'
        !requests[0].ignoreExisting
    }

    def 'script text with additional classpath entries'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject('foo'))
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
        requests[0].urlRoots[1].toString() == 'workspace://foo/classes/'
        requests[0].urlRoots[2].toString() == 'workspace://foo/output/'
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
        requests[0].urlRoots[1].toString() == 'workspace://foo/classes/'
        requests[0].urlRoots[2].toString() == 'workspace://foo/output/'
        !requests[0].ignoreExisting
        requests[1].location == 'b.groovy'
        requests[1].body == null
        requests[1].urlRoots.length == 3
        requests[1].urlRoots[0].toString() == 'workspace://foo/'
        requests[1].urlRoots[1].toString() == 'workspace://foo/classes/'
        requests[1].urlRoots[2].toString() == 'workspace://foo/output/'
        !requests[1].ignoreExisting
    }
}
