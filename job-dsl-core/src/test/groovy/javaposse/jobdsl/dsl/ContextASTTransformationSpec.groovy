package javaposse.jobdsl.dsl

import spock.lang.Specification

import java.lang.annotation.Annotation

class ContextASTTransformationSpec extends Specification {
    def 'DelegatesTo annotation is present'() {
        when:
        Annotation[][] parameterAnnotations = JobParent.getMethod('folder', Closure).parameterAnnotations

        then:
        parameterAnnotations[0].length == 1
        parameterAnnotations[0][0] instanceof DelegatesTo
        with((DelegatesTo) parameterAnnotations[0][0]) {
            value() == Folder
            strategy() == Closure.DELEGATE_FIRST
        }
    }
}
