package javaposse.jobdsl.dsl.helpers.scm

enum SvnDepth {
    INFINITY('infinity'),
    IMMEDIATES('immediates'),
    EMPTY('empty'),
    FILES('files'),
    AS_IT_IS('unknown')

    final String xmlValue

    SvnDepth(String xmlValue) {
        this.xmlValue = xmlValue
    }
}
