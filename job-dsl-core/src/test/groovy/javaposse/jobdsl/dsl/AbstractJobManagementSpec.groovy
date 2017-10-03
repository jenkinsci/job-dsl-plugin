package javaposse.jobdsl.dsl

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
        buffer.toString().trim() == 'Warning: (DSL script, line 1) testMethod is deprecated'
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
        buffer.toString().trim() == 'Warning: (deprecation.groovy, line 1) testMethod is deprecated'
    }

    def 'deprecation warning with custom subject in DSL script'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        GroovyClassLoader classLoader = new GroovyClassLoader()
        Class scriptClass = classLoader.parseClass(this.class.getResourceAsStream('/deprecation-subject.groovy').text)
        Script script = createScript(scriptClass, new Binding([jm: jobManagement]))

        when:
        script.run()

        then:
        buffer.toString().trim() == 'Warning: (DSL script, line 3) foo is deprecated'
    }

    def 'deprecation warning with custom subject  in source file'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        URL[] roots = [this.class.getResource('/deprecation-subject.groovy')]
        GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine(roots)

        when:
        groovyScriptEngine.run('deprecation-subject.groovy', new Binding([jm: jobManagement]))

        then:
        buffer.toString().trim() == 'Warning: (deprecation-subject.groovy, line 3) foo is deprecated'
    }

    def 'custom deprecation warning in DSL script'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        when:
        jobManagement.logDeprecationWarning('foo', 'script123123123.groovy', 12)

        then:
        buffer.toString().trim() == 'Warning: (DSL script, line 12) foo is deprecated'
    }

    def 'custom deprecation warning in source file'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        when:
        jobManagement.logDeprecationWarning('foo', 'test.groovy', 12)

        then:
        buffer.toString().trim() == 'Warning: (test.groovy, line 12) foo is deprecated'
    }

    def 'deprecation warning is logged once only'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        when:
        jobManagement.logDeprecationWarning('foo', 'script123123123.groovy', 12)
        jobManagement.logDeprecationWarning('foo', 'script123123123.groovy', 12)

        then:
        buffer.toString().trim() == 'Warning: (DSL script, line 12) foo is deprecated'
    }

    def 'JENKINS-44158 name may not end with trailing slash'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        when:
        jobManagement.validateNameArg('foo/')

        then:
        Exception e = thrown(DslException)
        e.message == 'name must not contain trailing slash: foo/'
    }

    static class TestJobManagement extends MockJobManagement {
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
        boolean createOrUpdateConfig(Item item, boolean ignoreExisting) throws NameNotProvidedException {
            throw new UnsupportedOperationException()
        }

        @Override
        void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
            throw new UnsupportedOperationException()
        }

        @Override
        InputStream streamFileInWorkspace(String filePath) throws IOException {
            throw new UnsupportedOperationException()
        }

        @Override
        String readFileInWorkspace(String filePath) throws IOException {
            throw new UnsupportedOperationException()
        }

        void testMethod() {
            logDeprecationWarning()
        }

        void testMethodWithCustomSubject() {
            logDeprecationWarning('foo')
        }
    }
}
