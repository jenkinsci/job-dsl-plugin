package javaposse.jobdsl.dsl

/**
 * Abstract base class providing common functionality for all {@link JobManagement} implementations.
 */
abstract class AbstractJobManagement implements JobManagement {
    final PrintStream outputStream
    private String lastWarning

    protected AbstractJobManagement(PrintStream out) {
        this.outputStream = out
    }

    @Override
    boolean createOrUpdateConfig(String path, String config, boolean ignoreExisting) {
        Item item = new Item(this) {
            @Override
            String getName() {
                path
            }

            @Override
            String getXml() {
                config
            }

            @Override
            Node getNode() {
                throw new UnsupportedOperationException()
            }
        }
        createOrUpdateConfig(item, ignoreExisting)
    }

    @Override
    void logDeprecationWarning() {
        List<StackTraceElement> currentStackTrack = DslScriptHelper.stackTrace
        String details = DslScriptHelper.getSourceDetails(currentStackTrack)
        logDeprecationWarning(currentStackTrack[0].methodName, details)
    }

    @Override
    void logDeprecationWarning(String subject) {
        logDeprecationWarning(subject, DslScriptHelper.sourceDetails)
    }

    @Override
    void logDeprecationWarning(String subject, String scriptName, int lineNumber) {
        logDeprecationWarning(subject, DslScriptHelper.getSourceDetails(scriptName, lineNumber))
    }

    protected void logDeprecationWarning(String subject, String details) {
        logWarning("${subject} is deprecated", details)
    }

    protected static void validateUpdateArgs(String jobName, String config) {
        validateNameArg(jobName)
        validateConfigArg(config)
    }

    protected static void validateConfigArg(String config) {
        if (config == null || config.empty) {
            throw new ConfigurationMissingException()
        }
    }

    protected static void validateNameArg(String name) {
        if (name == null || name.empty) {
            throw new NameNotProvidedException()
        }
    }

    /**
     * @deprecated use {@link DslScriptHelper#getStackTrace()} instead
     */
    @Deprecated
    protected static List<StackTraceElement> getStackTrace() {
        DslScriptHelper.stackTrace
    }

    /**
     * @deprecated use {@link DslScriptHelper#getSourceDetails(java.util.List)} instead
     */
    @Deprecated
    protected static String getSourceDetails(List<StackTraceElement> stackTrace) {
        DslScriptHelper.getSourceDetails(stackTrace)
    }

    /**
     * @deprecated use {@link DslScriptHelper#getSourceDetails(java.lang.String, int)} instead
     */
    @Deprecated
    protected static String getSourceDetails(String scriptName, int lineNumber) {
        DslScriptHelper.getSourceDetails(scriptName, lineNumber)
    }

    /**
     * @deprecated use {@link #logWarning(java.lang.String)} instead
     */
    @Deprecated
    protected void logWarning(String message, Object... args) {
        outputStream.printf("Warning: $message\n", args)
    }

    /**
     * @since 1.36
     */
    protected void logWarning(String message, String details = DslScriptHelper.getSourceDetails()) {
        String warning = "Warning: ($details) $message"
        if (warning != lastWarning) {
            outputStream.println(warning)
            lastWarning = warning
        }
    }
}
