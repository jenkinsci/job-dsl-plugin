package javaposse.jobdsl.dsl

import spock.lang.Specification

class GeneratedUserContentSpec extends Specification {
    def 'test toString'() {
        when:
        GeneratedUserContent generatedUserContent = new GeneratedUserContent('test')

        then:
        generatedUserContent.toString() == "GeneratedUserContent{path='test'}"
    }

    @SuppressWarnings('ChangeToOperator')
    def 'test compare'() {
        when:
        GeneratedUserContent userContent1 = new GeneratedUserContent('foo')
        GeneratedUserContent userContent2 = new GeneratedUserContent('other')

        then:
        userContent1.compareTo(userContent1) == 0
        userContent1.compareTo(userContent2) < 0
        userContent2.compareTo(userContent1) > 0
    }
}
