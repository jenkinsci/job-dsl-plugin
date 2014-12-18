package javaposse.jobdsl.dsl

import spock.lang.Specification

class GeneratedJobSpec extends Specification {
    def 'toString without template'() {
        when:
        GeneratedJob generatedJob = new GeneratedJob(null, 'test')

        then:
        generatedJob.toString() == "GeneratedJob{name='test'}"
    }

    def 'toString with template'() {
        when:
        GeneratedJob generatedJob = new GeneratedJob('foo', 'test')

        then:
        generatedJob.toString() == "GeneratedJob{name='test', template='foo'}"
    }
}
