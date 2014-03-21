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
        view.view{
        }

        then:
        String root = view.getXml()
        root.toString().contains ('hudson.model.ListView')
    }
    def 'check nesting name'() {
        when:
        view.view{ 
               name('asd123')
        }

        then:
        String root = view.getXml()
        root.toString().contains ('asd123')
    }
    def 'check nested has job'() {
        when:
        view.view{ 
               name('asd123')
               jobs {
                   name('theJobName')
               }
        }

        then:
        String root = view.getXml()
        root.toString().contains ('theJobName')
    }
    def 'check nestedviewOwner'() {
        when:
        view.view{ 
               name('asd123')
               jobs {
                   name('theJobName')
               }
        }

        then:
        String root = view.getXml()
        root.toString().contains ("owner")
        root.toString().contains ("class='hudson")
    }
}
