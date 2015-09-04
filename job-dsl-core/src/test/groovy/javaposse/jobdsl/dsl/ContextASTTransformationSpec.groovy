package javaposse.jobdsl.dsl

import spock.lang.Specification

import java.lang.annotation.Annotation

class ContextASTTransformationSpec extends Specification {
    def 'DelegatesTo annotation is present'() {
        when:
        Annotation[][] parameterAnnotations = JobParent.getMethod('folder', String, Closure).parameterAnnotations

        then:
        parameterAnnotations[1].length == 1
        parameterAnnotations[1][0] instanceof DelegatesTo
        with((DelegatesTo) parameterAnnotations[1][0]) {
            value() == Folder
            strategy() == Closure.DELEGATE_FIRST
        }
    }
}
