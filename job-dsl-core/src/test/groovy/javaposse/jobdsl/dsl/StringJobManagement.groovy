package javaposse.jobdsl.dsl

import groovy.xml.MarkupBuilder

/**
 * Testing JobManagement which will deal with a single template and single saved job. Useful for testing
 * since it can be prodded with the expected value.
 * @author jryan
 */
class StringJobManagement extends AbstractJobManagement {
    /**
     * XML to return by default
     */
    String defaultXml = null

    Map<String,String> availableConfigs = [:]
    Map<String,String> savedConfigs = [:]
    Map<String,String> availableFiles = [:]

    Map<String,String> params = [:]
    List<String> jobScheduled = []

    StringJobManagement(PrintStream out) {
        super(out)
    }

    StringJobManagement(String defaultXml) {
        this.defaultXml = defaultXml
    }

    StringJobManagement() {
    }

    StringJobManagement(Closure closure) {
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
        } else if (defaultXml != null) {
            return defaultXml
        } else {
            throw new JobConfigurationNotFoundException("No config found for ${jobName}")
        }
    }

    @Override
    boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException {
        validateUpdateArgs(jobName, config)

        savedConfigs[jobName] = config
        false
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        throw new UnsupportedOperationException()
    }

    @Override
    Map<String, String> getParameters() {
        params
    }

    @Override
    void queueJob(String jobName) throws NameNotProvidedException {
        jobScheduled << jobName
    }

    @Override
    InputStream streamFileInWorkspace(String filePath) {
        String body = availableFiles.get(filePath)
        if (body == null) {
            throw new FileNotFoundException(filePath)
        }
        new InputStreamReader(new StringReader(body))
    }

    @Override
    String readFileInWorkspace(String filePath) {
        String body = availableFiles.get(filePath)
        if (body == null) {
            throw new FileNotFoundException(filePath)
        }
        body
    }

    @Override
    void requireMinimumPluginVersion(String pluginShortName, String version) {
    }
}

