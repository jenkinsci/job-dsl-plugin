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

    @SuppressWarnings('ChangeToOperator')
    def 'test compare'() {
        when:
        GeneratedJob job1 = new GeneratedJob('235421345', 'foo')
        GeneratedJob job2 = new GeneratedJob('235421345', 'new name')

        then:
        job1.compareTo(job1) == 0
        job1.compareTo(job2) < 0
        job2.compareTo(job1) > 0
    }
}
