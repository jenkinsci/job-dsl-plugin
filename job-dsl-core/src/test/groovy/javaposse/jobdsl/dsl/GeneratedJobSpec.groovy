package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification

class GeneratedJobSpec extends Specification {
    def 'toString without template'() {
        when:
        def test = new FreeStyleJob(new MemoryJobManagement()).with { name = 'test'; it }
        GeneratedJob generatedJob = new GeneratedJob(null, test)

        then:
        generatedJob.toString() == "GeneratedJob{name='test'}"
    }

    def 'toString with template'() {
        when:
        def test = new FreeStyleJob(new MemoryJobManagement()).with { name = 'test'; it }
        GeneratedJob generatedJob = new GeneratedJob('foo', test)

        then:
        generatedJob.toString() == "GeneratedJob{name='test', template='foo'}"
    }
}
