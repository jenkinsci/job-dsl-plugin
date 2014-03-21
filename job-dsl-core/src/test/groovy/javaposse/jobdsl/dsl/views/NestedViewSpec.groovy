package javaposse.jobdsl.dsl.views

import spock.lang.Specification

import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ALL
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.DISABLED
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ENABLED
import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class NestedViewSpec extends Specification {
    NestedView view = new NestedView()

    def 'check listview nesting'() {
        when:
        view.listView('n'){
        }

        then:
        String root = view.getXml()
        root.toString().contains ('hudson.model.ListView')
    }
    def 'check nesting name'() {
        when:
        view.listView('asd123'){ 
        }

        then:
        String root = view.getXml()
        root.toString().contains ('asd123')
    }
    def 'check nested view works'() {
        when:
        view.listView('n'){
               jobs {
                   name('theJobName')
               }
        }

        then:
        String root = view.getXml()
        root.toString().contains ('theJobName')
    }
    def 'check nestedview has owner filled'() {
        when:
        view.listView('n'){ 
               name('asd123')
        }

        then:
        Node root = view.getNode()
        root.views[0].value()[0].owner[0].@class == ("hudson.plugins.nested_view.NestedView")
    }
}
