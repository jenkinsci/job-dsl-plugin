package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import spock.lang.Specification

class AbstractExtensibleContextSpec extends Specification {
    def 'conversion to named node'() {
        Node node = new Node(null, 'org.example.CustomType', [foo: 'bar'])
        node.appendNode('test', 'value')

        when:
        Node namedNode = AbstractExtensibleContext.toNamedNode('example', node)

        then:
        with(namedNode) {
            name() == 'example'
            attributes().size() == 2
            attribute('class') == 'org.example.CustomType'
            attribute('foo') == 'bar'
            children().size() == 1
            test[0].text() == 'value'
        }
    }
}
