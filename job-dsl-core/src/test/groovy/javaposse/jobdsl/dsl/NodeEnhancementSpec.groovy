package javaposse.jobdsl.dsl

import spock.lang.Specification

class NodeEnhancementSpec extends Specification {
    private final Node root = new XmlParser().parse(getClass().getResourceAsStream('/job.xml'))

    def 'lookup up nodes with attribute without attributes'() {
        when:
        Node trigger = execute {
            it / triggers
        }

        then:
        trigger != null
        trigger.attributes()['class'] == 'vector'
        root.triggers.size() == 1
        root.triggers[0] == trigger

    }

    def 'lookup up nodes with attribute with attributes'() {
        when:
        Node trigger = execute {
            it / triggers(class: 'vector')
        }

        then:
        trigger != null
        trigger.attributes()['class'] == 'vector'
        root.triggers.size() == 1
        root.triggers[0] == trigger
    }

    def 'lookup up nodes with attribute with different attributes'() {
        when:
        Node trigger = execute {
            it / triggers(class: 'arraylist')
        }

        then:
        trigger != null
        trigger.attributes()['class'] == 'arraylist'
        root.triggers.size() == 2
        root.triggers[1] == trigger
        root.triggers[0].attributes()['class'] == 'vector'
    }

    private Node execute(Closure closure) {
        closure.delegate = new MissingPropertyToStringDelegate(root)

        use(NodeEnhancement) {
            closure.call(root)
        }
    }
}
