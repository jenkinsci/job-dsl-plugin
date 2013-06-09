package javaposse.jobdsl.dsl

import com.google.common.collect.Iterables;
import spock.lang.*

public class DslScriptLoaderTest extends Specification {
    def resourcesDir = new File("src/test/resources")
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    JobManagement jm = new FileJobManagement(resourcesDir, null, ps)

    @Ignore
    def getContent() {
        return baos.toString()  // Could send ISO-8859-1
    }

    def 'load template from MarkupBuilder'() {
        setup:
        Job job = new Job(jm)

        // TODO
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
        // TODO
        //job.xml
    }

    def 'run engine'() {
        setup:
        ScriptRequest request = new ScriptRequest('simple.dsl', null, resourcesDir.toURL(), false);

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm)

        then:
        jobs != null
        jobs.size() == 1
        jobs.iterator().next().jobName == 'test'
    }

    def 'run engine with reference to other class'() {
        setup:
        ScriptRequest request = new ScriptRequest('caller.dsl', null, resourcesDir.toURL(), false);

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm)

        then:
        jobs != null
        jobs.size() == 2
        jobs.any { it.jobName == 'test'}
        jobs.any { it.jobName == 'test2'}

    }

    def 'run engine that uses static import'() {
        setup:
        def scriptStr = '''job(type: Maven) {
    name 'test'
}
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir.toURL(), false)

        when:
        JobParent jp = DslScriptLoader.runDslEngineForParent(request, jm)

        then:
        jp != null
        def jobs = jp.getReferencedJobs()
        jobs.size() == 1
        def job = Iterables.get(jobs, 0)
        job.name == 'test'
        job.type == JobType.Maven
    }

    def 'run engine with reference to other class from a string'() {
        setup:
        def scriptStr = '''job {
    name 'test'
}

Callee.makeJob(this, 'test2')
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir.toURL(), false)

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm)

        then:
        jobs != null
        jobs.size() == 2
        jobs.any { it.jobName == 'test'}
        jobs.any { it.jobName == 'test2'}

    }


    def 'use @Grab'() {
        setup:
        def scriptStr = '''@Grab(group='commons-lang', module='commons-lang', version='2.4')
import org.apache.commons.lang.WordUtils
println "Hello ${WordUtils.capitalize('world')}"
'''
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir.toURL(), false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        def results = getContent()
        results != null
        results.contains("Hello World")
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
        jm = new StringJobManagement();
        ScriptRequest request = new ScriptRequest(null, scriptStr, resourcesDir.toURL(), false)

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        jm.jobScheduled.size() == 2
        jm.jobScheduled.contains('JobA')
        jm.jobScheduled.contains('JobB')
    }
//
//    def 'Able to run engine for string'() {
//        setup:
//        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
//
//        when:
//        Set<GeneratedJob> results = DslScriptLoader.runDslShell(sampleDsl, jm)
//
//    }

}
