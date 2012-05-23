package javaposse.jobdsl.dsl;

import groovy.xml.MarkupBuilder

/**
 * Testing JobManagement which will deal with a single template and single saved job. Useful for testing
 * since it can be prodded with the expected value.
 * @author jryan
 */
public class StringJobManagement extends AbstractJobManagement {
    /**
     * XML to always return
     */
    String xml

    /**
     * XML that was saved
     */
    String savedXml

    public StringJobManagement(String xml) {
        this.xml = xml
    }

    public StringJobManagement(Closure closure) {
        StringWriter writer = new StringWriter()
        def build = new MarkupBuilder(writer)
        closure.delegate = build
        this.xml = writer.toString()
    }

    String getConfig(String jobName) {
        xml
    }

    boolean createOrUpdateConfig(String jobName, String config) {
        savedXml = config
        return false
    }
}

