package javaposse.jobdsl.dsl

import hudson.util.VersionNumber
import spock.lang.Specification

import static org.codehaus.groovy.runtime.InvokerHelper.createScript

class AbstractJobManagementSpec extends Specification {
    def 'deprecation warning in DSL script'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        GroovyClassLoader classLoader = new GroovyClassLoader()
        Class scriptClass = classLoader.parseClass(this.class.getResourceAsStream('/deprecation.groovy').text)
        Script script = createScript(scriptClass, new Binding([jm: jobManagement]))

        when:
        script.run()

        then:
        buffer.toString().trim() == 'Warning: testMethod is deprecated (DSL script, line 1)'
    }

    def 'deprecation warning in source file'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        URL[] roots = [this.class.getResource('/deprecation.groovy')]
        GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine(roots)

        when:
        groovyScriptEngine.run('deprecation.groovy', new Binding([jm: jobManagement]))

        then:
        buffer.toString().trim() == 'Warning: testMethod is deprecated (deprecation.groovy, line 1)'
    }

    def 'plugin version is always null'() {
        setup:
        AbstractJobManagement jobManagement = new TestJobManagement()

        when:
        VersionNumber version = jobManagement.getPluginVersion('foo')

        then:
        version == null
    }

    def 'vSphere cloud hash is always null'() {
        setup:
        AbstractJobManagement jobManagement = new TestJobManagement()

        when:
        Integer hash = jobManagement.getVSphereCloudHash('foo')

        then:
        hash == null
    }

    def 'reading files from workspace is not supported'() {
        setup:
        AbstractJobManagement jobManagement = new TestJobManagement()

        when:
        jobManagement.readFileInWorkspace('test.txt')

        then:
        thrown(UnsupportedOperationException)

        when:
        jobManagement.streamFileInWorkspace('test.txt')

        then:
        thrown(UnsupportedOperationException)

        when:
        jobManagement.readFileInWorkspace('my-job', 'test.txt')

        then:
        thrown(UnsupportedOperationException)
    }

    static class TestJobManagement extends AbstractJobManagement {
        TestJobManagement() {
            super()
        }

        TestJobManagement(PrintStream out) {
            super(out)
        }

        @Override
        String getConfig(String jobName) {
            throw new UnsupportedOperationException()
        }

        @Override
        boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting) {
            throw new UnsupportedOperationException()
        }

        @Override
        void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
            throw new UnsupportedOperationException()
        }

        @Override
        void requireMinimumPluginVersion(String pluginShortName, String version) {
            throw new UnsupportedOperationException()
        }

        void testMethod() {
            logDeprecationWarning()
        }
    }
}
