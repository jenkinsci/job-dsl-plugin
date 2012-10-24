package javaposse.jobdsl.dsl;

import spock.lang.*
import groovy.xml.MarkupBuilder
import static org.custommonkey.xmlunit.XMLAssert.*
import static org.custommonkey.xmlunit.XMLUnit.*
import org.custommonkey.xmlunit.XMLAssert

import java.io.File;

public class DslScriptLoaderTest extends Specification {
    def 'load template from MarkupBuilder'() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        // TODO
    }

    def 'load template from file'() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using('config') // src/test/resources/config.xml

        then:
        noExceptionThrown()
    }

    def 'configure block without template'() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.configure {
            description = 'Another description'
        }

        then:
        noExceptionThrown()
        // TODO
        //job.xml
    }

}
