package javaposse.jobdsl.dsl

import spock.lang.*
import groovy.xml.MarkupBuilder
import static org.custommonkey.xmlunit.XMLAssert.*
import static org.custommonkey.xmlunit.XMLUnit.*
import org.custommonkey.xmlunit.XMLAssert

class NodeDelegateTest extends Specification {

    NodeDelegate nd
    def setup() {
        nd = new NodeDelegate(minimalXml)
    }

    def 'use empty configure block'() {
        when: nd.with { }
        then: noExceptionThrown()
    }

    def 'add node using equals notation'() {
        when: nd.with {  elementEquals = 'VALUE1' }
        then: nd.node.elementEquals.text() == 'VALUE1'
    }

    def 'simple node onto root'() {
        when: nd.with { 'KEY0' }
        then: nd.node.KEY0 != null
    }

    def 'add node using space/method'() {
        when: nd.with {  elementSpace 'VALUE3' }
        then: nd.node.elementSpace.text() == 'VALUE3'
    }

    def 'add empty node using empty notation'() {
        when: nd.with {  elementEmpty }
        then: nd.node.elementEmpty != null
    }

    def 'add node with string name'() {
        // when: nd.with {  'elementString' = 'VALUE4' } // Invalid Groovy
        when: nd.with {  'elementString' 'VALUE4' }
        then: nd.node.elementString.text() == 'VALUE4'
    }

    def 'add node using native boolean'() {
        when: nd.with {  elementBoolean = false }
        then: nd.node.elementBoolean.text() == 'false'
    }

    def 'add node using closure notation to add child'() {
        when: nd.with {  elementClosure { 'VALUE2' } } // <-- VALUE will resolved as a new node underneath
        then: nd.node.elementClosure.VALUE2 != null
    }

    def 'add nested nodes using configure block'() {
        when: nd.with {
            emptyElement {
                println "${delegate}"
                elementAdded 'VALUE3'
            }
        }

        then:
        nd.node.emptyElement.elementAdded.text() == 'VALUE3'
    }
//
//    def 'add nested nodes to a non-existent parent'() {
//        when: nd.with {
//            emptyParent {
//                elementChild 'VALUE3'
//            }
//        }
//        then: nd.node.elementEmpty.elementAdded.text() == 'VALUE3'
//    }

    def 'append node to root, plus notation explicit method'() {
        when: nd.with {
            it + description('Another description') // How is this not automatically updating?
        }
        then: nd.node.description.size() == 2
    }

    def 'append node with attributes'() {
        when:
            // TODO need to detect Map as an argument
            nd.with { scm(class: "hudson.plugins.git.GitSCM") }
        then:
            nd.node.scm != null
            nd.node.scm[0].attribute('class') == "hudson.plugins.git.GitSCM"
    }

//    def 'append node to root, plus plus notation'() {
//        when: nd.with {  keepDependencies { false } ++ }
//        then: nd.node.keepDependencies.text() == 'VALUE2' // TOOD look for multiple values
//    }

    def 'append node to existing parent, plus notation'() {
        when:
        nd.with {
            'properties' + prop {
                key 'KEY2'
                value 'VALUE2'
            }
        }
        then: nd.node.properties[0].size() == 2
    }

//    def 'append node to existing parent, plus plus notation'() {
//        when:
//        nd.with {
//            properties {
//                prop {
//                    key 'KEY2'
//                    value 'VALUE2'
//                } ++
//            }
//        }
//        then: nd.node.properties.size() == 3
//    }

//    def 'append node to existing parent, plus plus notation on non-existent sub-element'() {
//        when:
//        nd.with {
//            properties.prop {
//                    key 'KEY2'
//                    value 'VALUE2'
//            } ++
//        }
//        then: nd.node.properties.size() == 3
//    }

    def 'update existing node on root'() {
        when: nd.with {  description = 'VALUE1' }
        then: nd.node.description.text() == 'VALUE1'
    }

    def 'update existing node via closure value'() {
        when: nd.with { 'properties' { prop {  key = 'KEY2' } } }
        then: nd.node.'properties'[0].prop[0].key[0].text() == 'KEY2'
    }

    def 'update existing node via nexted closures value with attributes'() {
        when: nd.with {
            triggers(class: 'vector') {
                'hudson.triggers.SCMTrigger' {
                    spec = '*/5 * * * *'
                }
            }
        }
        then: nd.node.triggers[0].'hudson.triggers.SCMTrigger'[0].spec.text() == '*/5 * * * *'
    }

    // // More tests to write
    // assertXmlValid from job.xml on empty job
    // assertXmlValid from job.xml on minimal job
    // configure block without a template
    // Change description
    // Test adding node
    // Test updating existing node
    // Test delete node

    def minimalXml =
'''<project>
    <actions/>
    <description>Test Description</description>
    <keepDependencies>false</keepDependencies>
    <properties>
        <prop><key>KEY1</key><value>VALUE1</value></prop>
    </properties>
    <triggers class="vector">
        <hudson.triggers.SCMTrigger>
            <spec>*/10 * * * *</spec>
        </hudson.triggers.SCMTrigger>
    </triggers>
</project>
'''

// Pretty MarkupBuilder syntax
//        def xml = new MarkupBuilder()
//        JobManagement jm = new StringJobManagement({
//            project() {
//                description('Description')
//                jdk('JDK 6')
//                triggers(class:"vector") {
//                    'hudson.triggers.SCMTrigger'() {
//                        spec('*/10 * * * *')
//                    }
//                }
//            }
//        })

// XmlParser produces Node, use XmlNodePrinter(identation control) or XmlUtil.serialize
// XmlSlurper produces GPath, XmlUtil.serialize
// DOMBuilder
}
