package javaposse.jobdsl.dsl

import spock.lang.*
import groovy.xml.MarkupBuilder

class NodeDelegateTest extends Specification {
    
    def "mutate config using configure block"() {
        setup:
        Node projectNode = new XmlParser().parse(new StringReader(minimalXml))
        
        when: NodeDelegate nd = new NodeDelegate(projectNode)
        then: noExceptionThrown()

    }

    def "mutate node using configure block"() {
        setup:
        Node projectNode = new XmlParser().parse(new StringReader(minimalXml))
        NodeDelegate nd = new NodeDelegate(projectNode)

        when: 
        nd.with { //configure block
            jdk ='JDK 6'
        }

        then:
        projectNode.jdk.text() == 'JDK 6'
    }

    def "load template from MarkupBuilder"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

    }

    def "load template from file"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using("config") // src/test/resources/config.xml

        then:
        noExceptionThrown()
    }

    def "configure block"() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)
        job.using("config")
        job.configure {
            description = 'Another description'
        }
    }
    def minimalXml = 
'''<project>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
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
