package javaposse.jobdsl.dsl;

import spock.lang.*

public class DslScriptLoaderTest extends Specification {
    def resourcesDir = new File("src/test/resources")
    JobManagement jm = new FileJobManagement(resourcesDir)

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
        ScriptRequest request = new ScriptRequest('simple.dsl', resourcesDir.toURL());

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm)

        then:
        jobs != null
        jobs.size() == 1
        jobs.iterator().next().jobName == 'test'
    }

    def 'run engine with reference to other class'() {
        setup:
        ScriptRequest request = new ScriptRequest('caller.dsl', resourcesDir.toURL());

        when:
        def jobs = DslScriptLoader.runDslEngine(request, jm)

        then:
        jobs != null
        jobs.size() == 2
        jobs.any { it.jobName == 'test'}
        jobs.any { it.jobName == 'test2'}

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
