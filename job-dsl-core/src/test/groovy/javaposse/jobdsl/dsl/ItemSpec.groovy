package javaposse.jobdsl.dsl

import spock.lang.Specification

class ItemSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = new TestItem(jobManagement)

    def 'name'() {
        when:
        item.name('test')

        then:
        item.name == 'test'
        1 * jobManagement.logDeprecationWarning()
    }

    def 'set name twice '() {
        when:
        item.name('foo')
        item.name('bar')

        then:
        thrown(IllegalStateException)
    }

    class TestItem extends Item {
        protected TestItem(JobManagement jobManagement) {
            super(jobManagement)
        }

        @Override
        Node getNode() {
            throw new UnsupportedOperationException()
        }
    }
}
