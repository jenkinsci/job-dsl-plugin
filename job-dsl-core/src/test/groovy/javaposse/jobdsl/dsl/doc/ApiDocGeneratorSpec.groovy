package javaposse.jobdsl.dsl.doc

import groovy.json.JsonBuilder
import spock.lang.Specification

class ApiDocGeneratorSpec extends Specification {
    private final ApiDocGenerator apiDocGenerator = new ApiDocGenerator()

    void 'generateApi - success'() {
        when:
        JsonBuilder builder = apiDocGenerator.generateApi('1.0')

        then:
        with(builder.content) {
            version == '1.0'
            root.contextClass == 'javaposse.jobdsl.dsl.DslFactory'
            with(contexts['javaposse.jobdsl.dsl.jobs.FreeStyleJob']) {
                type == 'javaposse.jobdsl.dsl.jobs.FreeStyleJob'
                methods.find { it.name == 'scm' }
            }
        }
    }
}
