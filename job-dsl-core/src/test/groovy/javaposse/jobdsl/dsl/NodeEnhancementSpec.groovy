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

    def 'left shift clones node'() {
        setup:
        Node node = new NodeBuilder().'test' {
            foo('bar')
        }

        when:
        execute {
            it << node
        }
        node.appendNode('baz')

        then:
        root.test.size() == 1
        root.test[0].foo[0].value() == 'bar'
        root.test.baz.size() == 0
    }

    def 'div clones children when replacing'() {
        setup:
        Node node = new NodeBuilder().'actions' {
            foo('bar')
        }

        when:
        Node result = execute {
            it / node
        }
        node.appendNode('baz')

        then:
        root.actions.size() == 1
        root.actions[0].foo[0].value() == 'bar'
        root.actions.baz.size() == 0
        result != node
        result == root.actions[0]
    }

    def 'div clones node when appending'() {
        setup:
        Node node = new NodeBuilder().'test' {
            foo('bar')
        }

        when:
        Node result = execute {
            it / node
        }
        node.appendNode('baz')

        then:
        root.test.size() == 1
        root.test[0].foo[0].value() == 'bar'
        root.test.baz.size() == 0
        result != node
        result == root.test[0]
    }

    def 'div handles node with null attributes'() {
        setup:
        Node node = new Node(null, 'test', null)

        when:
        execute {
            it / node
        }

        then:
        root.test.size() == 1
    }

    private Node execute(Closure closure) {
        closure.delegate = new MissingPropertyToStringDelegate(root)

        use(NodeEnhancement) {
            closure.call(root)
        }
    }
}
