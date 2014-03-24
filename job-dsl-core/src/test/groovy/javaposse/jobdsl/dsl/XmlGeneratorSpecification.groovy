package javaposse.jobdsl.dsl


import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual


abstract class XmlGeneratorSpecification extends Specification {
    void assertXmlEqual(String expectedXml, Node node) {
        XMLUnit.setIgnoreWhitespace true
        assertXMLEqual(expectedXml, XmlUtil.serialize(node))
    }
}
