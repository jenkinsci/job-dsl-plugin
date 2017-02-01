package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

class DslScriptLoaderSpec extends Specification {
    private final resourcesDir = getClass().getResource('/simple.dsl')
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream()
    private final PrintStream ps = new PrintStream(baos)
    private final MemoryJobManagement jm = new MemoryJobManagement(ps)
    private DslScriptLoader dslScriptLoader = new DslScriptLoader(jm)

    private String getContent() {
        baos.toString()  // Could send ISO-8859-1
    }

    private static String loadScript(String scriptName) {
        DslScriptLoader.getResource('/' + scriptName).text
    }

    def 'load template from file'() {
        setup:
        Job job = new FreeStyleJob(jm, 'test')

        when:
        job.using('config') // src/test/resources/config.xml

        then:
        noExceptionThrown()
    }

    def 'configure block without template'() {
        setup:
        Job job = new FreeStyleJob(jm, 'test')

        when:
        job.configure {
            description = 'Another description'
        }

        then:
        noExceptionThrown()
    }

    def 'run engine with location'() {
        setup:
        ScriptRequest request = new ScriptRequest('simple.dsl', null, resourcesDir, false)

        when:
        def jobs = dslScriptLoader.runScripts([request]).jobs

        then:
        jobs != null
        jobs.size() == 1
        jobs.iterator().next().jobName == 'test'
    }

    def 'run engine'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('simple.dsl'), resourcesDir, false, 'simple.dsl')

        when:
        def jobs = dslScriptLoader.runScripts([request]).jobs

        then:
        jobs != null
        jobs.size() == 1
        jobs.iterator().next().jobName == 'test'
    }

    def 'run engine for single script'() {
        when:
        def jobs = dslScriptLoader.runScript(loadScript('simple.dsl')).jobs

        then:
        jobs != null
        jobs.size() == 1
        jobs.iterator().next().jobName == 'test'
    }

    def 'run engine with multiple scripts'() {
        setup:
        ScriptRequest request1 = new ScriptRequest(loadScript('simple.dsl'), resourcesDir, false, 'simple.dsl')
        ScriptRequest request2 = new ScriptRequest(loadScript('simple2.dsl'), resourcesDir, false, 'simple2.dsl')

        when:
        def jobs = dslScriptLoader.runScripts([request1, request2]).jobs

        then:
        jobs != null
        jobs.size() == 2
        jobs.find { it.jobName == 'test' }
        jobs.find { it.jobName == 'test2' }
    }

    def 'run engine with reference to other class'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('caller.dsl'), resourcesDir, false, 'caller.dsl')

        when:
        def jobs = dslScriptLoader.runScripts([request]).jobs

        then:
        jobs != null
        jobs.size() == 2
        jobs.any { it.jobName == 'test' }
        jobs.any { it.jobName == 'test2' }

    }

    def 'run engine with dependent jobs'() {
        setup:
        def scriptStr = '''job('project-a') {
}
job('project-b') {
}
'''
        ScriptRequest request = new ScriptRequest(scriptStr, resourcesDir, false)

        when:
        GeneratedItems generatedItems = dslScriptLoader.runScripts([request])

        then:
        generatedItems != null
        def jobs = generatedItems.jobs
        jobs.size() == 2
        def job = jobs.first()
        // If this one fails periodically, then it is because the referenced jobs are
        // Not in definition order, but rather in hash order. Hence, predictability.
        job.jobName == 'project-a'

        where:
        x << [1..25]
    }

    def 'run engine renaming existing jobs'() {
        setup:
        def scriptStr = '''job('5-project') {
    previousNames '\\\\d-project'
}
'''
        ScriptRequest request = new ScriptRequest(scriptStr, resourcesDir, false)
        MemoryJobManagement jm = Spy(MemoryJobManagement)
        dslScriptLoader = new DslScriptLoader(jm)
        jm.availableFiles['4-project'] = ''

        when:
        dslScriptLoader.runScripts([request])

        then:
        1 * jm.renameJobMatching(/\d-project/, '5-project')

    }

    def 'run engine with reference to other class from a string'() {
        setup:
        def scriptStr = '''job('test') {
}

Callee.makeJob(this, 'test2')
'''
        ScriptRequest request = new ScriptRequest(scriptStr, resourcesDir, false)

        when:
        def jobs = dslScriptLoader.runScripts([request]).jobs

        then:
        jobs != null
        jobs.size() == 2
        jobs.any { it.jobName == 'test' }
        jobs.any { it.jobName == 'test2' }
    }

    def 'jobs scheduled to build'() {
        setup:
        def scriptStr = '''
def jobA = job('JobA') {
}
queue jobA
queue 'JobB'
'''
        ScriptRequest request = new ScriptRequest(scriptStr, resourcesDir, false)

        when:
        dslScriptLoader.runScripts([request])

        then:
        jm.scheduledJobs.size() == 2
        jm.scheduledJobs.contains('JobA')
        jm.scheduledJobs.contains('JobB')
    }

    def 'files read through to JobManagement'() {
        setup:
        def scriptStr = '''
def jobA = job('JobA') {
}

def content = readFileFromWorkspace('foo.txt')
println content
'''
        MemoryJobManagement sm = new MemoryJobManagement(ps)
        sm.availableFiles['foo.txt'] = 'Bar bar, bar bar.'

        ScriptRequest request = new ScriptRequest(scriptStr, resourcesDir, false)

        when:
        new DslScriptLoader(sm).runScripts([request])

        then:
        noExceptionThrown()
        content.contains('bar')
        content.count('bar') == 3
    }

    def 'read nonexistant file'() {
        setup:
        def scriptStr = '''
readFileFromWorkspace('bar.txt')
'''
        MemoryJobManagement sm = new MemoryJobManagement(ps)
        sm.availableFiles['foo.txt'] = 'Bar bar, bar bar.'

        ScriptRequest request = new ScriptRequest(scriptStr, resourcesDir, false)

        when:
        new DslScriptLoader(sm).runScripts([request])

        then:
        thrown(IOException)
    }

    def 'run engine with views'() {
        setup:
        def scriptStr = '''listView('view-a') {
}
listView('view-b') {
}
'''

        when:
        def views = new DslScriptLoader(jm).runScripts([new ScriptRequest(scriptStr)]).views

        then:
        views.size() == 2
        views.any { it.name == 'view-a' }
        views.any { it.name == 'view-b' }
    }

    def 'run engine with folders'() {
        setup:
        def scriptStr = '''folder('folder-a') {
}
folder('folder-b') {
}
'''

        when:
        def jobs = new DslScriptLoader(jm).runScripts([new ScriptRequest(scriptStr)]).jobs

        then:
        jobs.size() == 2
        jobs.any { it.jobName == 'folder-a' }
        jobs.any { it.jobName == 'folder-b' }
    }

    def 'JENKINS-32941'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('JENKINS_32941.groovy'), resourcesDir, false)

        when:
        dslScriptLoader.runScripts([request])

        then:
        XMLUnit.compareXML(getClass().getResource('/JENKINS_32941.xml').text, jm.savedConfigs['example']).similar()
    }

    def 'script name which is not a valid class name'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('test-script.dsl'), resourcesDir, false, 'test-script.dsl')

        when:
        dslScriptLoader.runScripts([request])

        then:
        Exception e = thrown(DslException)
        e.message =~ /invalid script name/
        e.message =~ /test-script\.dsl/
    }

    def 'JENKINS-41612 Windows paths'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('test-script.dsl'), resourcesDir, false, 'foo\\bar.dsl')

        when:
        dslScriptLoader.runScripts([request])

        then:
        noExceptionThrown()
    }

    def 'JENKINS-41612 Unix paths'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('test-script.dsl'), resourcesDir, false, 'foo/bar.dsl')

        when:
        dslScriptLoader.runScripts([request])

        then:
        noExceptionThrown()
    }

    def 'JENKINS-32628 script name which collides with package name'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('java.dsl'), resourcesDir, false, 'java.dsl')

        when:
        dslScriptLoader.runScripts([request])

        then:
        content =~ /identical to a package name/
        content =~ /java\.dsl/
    }

    def 'script in directory'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('foo/test.dsl'), resourcesDir, false, 'foo/test.dsl')

        when:
        dslScriptLoader.runScripts([request])

        then:
        noExceptionThrown()
    }

    def 'script with method'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('test-script-with-method.dsl'), resourcesDir, false
        )

        when:
        dslScriptLoader.runScripts([request])

        then:
        noExceptionThrown()
    }

    def 'generate config files'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('configfiles.dsl'), resourcesDir, false, 'configfiles.dsl')

        when:
        List<GeneratedConfigFile> files = dslScriptLoader.runScripts([request]).configFiles.toList()

        then:
        files.size() == 1
        files[0].name == 'foo'
    }

    def 'generate user contents'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('userContent.dsl'), resourcesDir, false, 'userContent.dsl')

        when:
        List<GeneratedUserContent> userContents = dslScriptLoader.runScripts([request]).userContents.toList()

        then:
        userContents.size() == 1
        userContents[0].path == 'foo.txt'
    }

    def 'getProperties throws exception'() { // JENKINS-22708
        setup:
        String script = '''
            job('Test') {
                configure { root ->
                    (properties / 'hudson.plugins.disk__usage.DiskUsageProperty').@plugin="disk-usage@0.23"
                }
            }
        '''
        ScriptRequest request = new ScriptRequest(script, resourcesDir, false)

        when:
        new DslScriptLoader(jm).runScripts([request])

        then:
        thrown UnsupportedOperationException
    }

    def 'DslException on compilation error'() {
        setup:
        ScriptRequest request = new ScriptRequest('import foo.bar', resourcesDir, false)

        when:
        dslScriptLoader.runScripts([request])

        then:
        thrown(DslException)
    }

    def 'DslScriptException on MissingMethodException'() {
        setup:
        ScriptRequest request = new ScriptRequest('foo("bar")', resourcesDir, false)

        when:
        dslScriptLoader.runScripts([request])

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ /\(script, line 1\) .+/
    }

    def 'DslScriptException on MissingPropertyException'() {
        setup:
        ScriptRequest request = new ScriptRequest('job("foo").bar = 1', resourcesDir, false)

        when:
        dslScriptLoader.runScripts([request])

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ /\(script, line 1\) .+/
    }

    def 'DslScriptException is passed through'() {
        setup:
        String script = '''
job('foo') {
    using('one')
    using('two')
}
'''
        ScriptRequest request = new ScriptRequest(script, resourcesDir, false)

        when:
        dslScriptLoader.runScripts([request])

        then:
        Exception e = thrown(DslScriptException)
        e.message == '(script, line 4) Can only use "using" once'
    }

    def '__FILE__ is set'() {
        setup:
        ScriptRequest request = new ScriptRequest(loadScript('file.dsl'), resourcesDir, false, 'test/file.dsl')

        when:
        dslScriptLoader.runScripts([request])

        then:
        content.contains('Script: test/file.dsl')
    }
}
