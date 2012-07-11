package javaposse.jobdsl.dsl;

import groovy.xml.MarkupBuilder

/**
 * Testing JobManagement which will deal with a single template and single saved job. Useful for testing
 * since it can be prodded with the expected value.
 * @author jryan
 */
public class StringJobManagement extends AbstractJobManagement {
    /**
     * XML to return by default
     */
    String defaultXml

    Map<String,String> availableConfigs = [:]
    Map<String,String> savedConfigs = [:]

    public StringJobManagement(String defaultXml) {
        this.defaultXml = defaultXml
    }

    public StringJobManagement() {
        this.defaultXml = null
    }

    public StringJobManagement(Closure closure) {
        StringWriter writer = new StringWriter()
        def build = new MarkupBuilder(writer)
        closure.delegate = build
        defaultXml = writer.toString()
    }

    void addConfig(String jobName, String xml) {
        availableConfigs[jobName] = xml
    }

    String getConfig(String jobName) {
        if (availableConfigs.containsKey(jobName)) {
            return availableConfigs[jobName]
        } else if (defaultXml!=null) {
            return defaultXml
        } else {
            throw new JobConfigurationNotFoundException("No config found for ${jobName}")
        }
    }

    boolean createOrUpdateConfig(String jobName, String config) {
        savedConfigs[jobName] = config
        return false
    }
}

