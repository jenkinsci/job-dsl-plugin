package javaposse.jobdsl.dsl

import com.google.common.collect.Iterables
import spock.lang.Ignore
import spock.lang.Specification

class DslScriptLoaderTest extends Specification {
    private final resourcesDir = getClass().getResource('/simple.dsl')
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream()
    private final PrintStream ps = new PrintStream(baos)
    private final JobManagement jm = new StringJobManagement(ps)

    @Ignore
    def getContent() {
        baos.toString()  // Could send ISO-8859-1
    }

    def 'load template from file'() {
        setup:
        Job job = new Job(jm)

        when:
        job.using('config') // src/test/resources/config.xml

        then:
        noExceptionThrown()
    }

    def 'configure block without template'() {
        setup:
        Job job = new Job(jm)

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
        def scriptStr = '''job {
    name 'project-a'
}
job {
  name 'project-b'
}
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        JobParent jp = DslScriptLoader.runDslEngineForParent(request, jm)

        then:
        jp != null
        def jobs = jp.referencedJobs
        jobs.size() == 2
        def job = Iterables.get(jobs, 0)
        // If this one fails periodically, then it is because the referenced jobs are
        // Not in definition order, but rather in hash order. Hence, predictability.
        job.name == 'project-a'
        where:
          x << [1..25]
    }

    def 'run engine that uses static import'() {
        setup:
        def scriptStr = '''job(type: Maven) {
    name 'test'
}
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        JobParent jp = DslScriptLoader.runDslEngineForParent(request, jm)

        then:
        jp != null
        def jobs = jp.referencedJobs
        jobs.size() == 1
        def job = Iterables.get(jobs, 0)
        job.name == 'test'
        job.type == JobType.Maven
    }

    def 'run engine that uses static import for LocalRepositoryLocation'() {
        setup:
        def scriptStr = '''job(type: Maven) {
    name 'test'
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
        def scriptStr = '''job {
    name 'test'
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

    def 'use @Grab'() {
        setup:
        def scriptStr = '''@Grab(group='commons-lang', module='commons-lang', version='2.4')
import org.apache.commons.lang.WordUtils

println "Hello ${WordUtils.capitalize('world')}"
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        def results = content
        results != null
        results.contains('Hello World')
    }

    def 'jobs scheduled to build'() {
        setup:
        def scriptStr = '''
def jobA = job {
    name 'JobA'
}
queue jobA
queue 'JobB'
'''
        JobManagement jm = new StringJobManagement()
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        jm.jobScheduled.size() == 2
        jm.jobScheduled.contains('JobA')
        jm.jobScheduled.contains('JobB')
    }

    def 'files read through to JobManagement'() {
        setup:
        def scriptStr = '''
def jobA = job {
    name 'JobA'
}

def content = readFileFromWorkspace('foo.txt')
println content
'''
        StringJobManagement sm = new StringJobManagement(ps)
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
        StringJobManagement sm = new StringJobManagement(ps)
        sm.availableFiles['foo.txt'] = 'Bar bar, bar bar.'

        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir, false)

        when:
        DslScriptLoader.runDslEngine(request, sm)

        then:
        thrown(IOException)
    }

    def 'run engine with views'() {
        setup:
        def scriptStr = '''view {
    name 'view-a'
}
view(type: ListView) {
    name 'view-b'
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
        def scriptStr = '''folder {
    name 'folder-a'
}
folder {
    name 'folder-b'
}
'''

        when:
        def jobs = DslScriptLoader.runDslEngine(scriptStr, jm).jobs

        then:
        jobs.size() == 2
        jobs.any { it.jobName == 'folder-a' }
        jobs.any { it.jobName == 'folder-b' }
    }
}
