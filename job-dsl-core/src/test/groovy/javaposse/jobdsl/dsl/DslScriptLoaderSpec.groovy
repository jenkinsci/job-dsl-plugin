package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Ignore
import spock.lang.Specification

class DslScriptLoaderSpec extends Specification {
    private final resourcesDir = getClass().getResource('/simple.dsl')
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream()
    private final PrintStream ps = new PrintStream(baos)
    private final MemoryJobManagement jm = new MemoryJobManagement(ps)

    @Ignore
    def getContent() {
        baos.toString()  // Could send ISO-8859-1
    }

    def 'load template from file'() {
        setup:
        Job job = new FreeStyleJob(jm)

        when:
        job.using('config') // src/test/resources/config.xml

        then:
        noExceptionThrown()
    }

    def 'configure block without template'() {
        setup:
        Job job = new FreeStyleJob(jm)

        when:
        job.configure {
            description = 'Another description'
        }

        then:
        noExceptionThrown()
    }

    def 'run engine'() {
        setup:
        ScriptRequest request = new ScriptRequest('simple.dsl', null, resourcesDir, false)

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm).jobs

        then:
        jobs != null
        jobs.size() == 1
        jobs.iterator().next().jobName == 'test'
    }

    def 'run engine with reference to other class'() {
        setup:
        ScriptRequest request = new ScriptRequest('caller.dsl', null, resourcesDir, false)

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm).jobs

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
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        JobParent jp = DslScriptLoader.runDslEngineForParent(request, jm)

        then:
        jp != null
        def jobs = jp.referencedJobs
        jobs.size() == 2
        def job = jobs.first()
        // If this one fails periodically, then it is because the referenced jobs are
        // Not in definition order, but rather in hash order. Hence, predictability.
        job.name == 'project-a'

        where:
        x << [1..25]
    }

    def 'run engine renaming existing jobs'() {
        setup:
        def scriptStr = '''job('5-project') {
    previousNames '\\\\d-project'
}
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)
        MemoryJobManagement jm = Spy(MemoryJobManagement)
        jm.availableFiles['4-project'] = ''

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        1 * jm.renameJobMatching(/\d-project/, '5-project')

    }

    def 'run engine that uses static import for LocalRepositoryLocation'() {
        setup:
        def scriptStr = '''mavenJob('test') {
    localRepository LocalToExecutor
}
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        JobParent jp = DslScriptLoader.runDslEngineForParent(request, jm)

        then:
        jp != null
        def jobs = jp.referencedJobs
        jobs.size() == 1
    }

    def 'run engine with reference to other class from a string'() {
        setup:
        def scriptStr = '''job('test') {
}

Callee.makeJob(this, 'test2')
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm).jobs

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
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

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

        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, sm)

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

        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, sm)

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
        def views = DslScriptLoader.runDslEngine(scriptStr, jm).views

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
        def jobs = DslScriptLoader.runDslEngine(scriptStr, jm).jobs

        then:
        jobs.size() == 2
        jobs.any { it.jobName == 'folder-a' }
        jobs.any { it.jobName == 'folder-b' }
    }

    def 'script name which is not a valid class name'() {
        setup:
        ScriptRequest request = new ScriptRequest('test-script.dsl', null, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        noExceptionThrown()
        baos.toString() =~ /support for arbitrary names is deprecated/
        baos.toString() =~ /test-script\.dsl/
    }

    def 'script with method'() {
        setup:
        ScriptRequest request = new ScriptRequest(
                null, DslScriptLoaderSpec.getResource('/test-script-with-method.dsl').text, resourcesDir, false
        )

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        noExceptionThrown()
    }

    def 'generate config files'() {
        setup:
        ScriptRequest request = new ScriptRequest('configfiles.dsl', null, resourcesDir, false)

        when:
        List<GeneratedConfigFile> files = DslScriptLoader.runDslEngine(request, jm).configFiles.toList()

        then:
        files.size() == 1
        files[0].name == 'foo'
    }

    def 'generate user contents'() {
        setup:
        ScriptRequest request = new ScriptRequest('userContent.dsl', null, resourcesDir, false)

        when:
        List<GeneratedUserContent> userContents = DslScriptLoader.runDslEngine(request, jm).userContents.toList()

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
        ScriptRequest request = new ScriptRequest(null, script, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine request, jm

        then:
        thrown UnsupportedOperationException
    }

    def 'DslException on compilation error'() {
        setup:
        ScriptRequest request = new ScriptRequest(null, 'import foo.bar', resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        thrown(DslException)
    }

    def 'DslScriptException on MissingMethodException'() {
        setup:
        ScriptRequest request = new ScriptRequest(null, 'foo("bar")', resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ /\(script, line 1\) .+/
    }

    def 'DslScriptException on MissingPropertyException'() {
        setup:
        ScriptRequest request = new ScriptRequest(null, 'job("foo").bar = 1', resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

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
        ScriptRequest request = new ScriptRequest(null, script, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        Exception e = thrown(DslScriptException)
        e.message == '(script, line 4) Can only use "using" once'
    }
}
