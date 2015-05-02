package javaposse.jobdsl.dsl

import static java.lang.Thread.currentThread
import static org.codehaus.groovy.runtime.StackTraceUtils.isApplicationClass

/**
 * Abstract base class providing common functionality for all {@link JobManagement} implementations.
 */
abstract class AbstractJobManagement implements JobManagement {
    final PrintStream outputStream

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
        List<StackTraceElement> currentStackTrack = stackTrace
        String details = getSourceDetails(currentStackTrack)
        logDeprecationWarning(currentStackTrack[0].methodName, details)
    }

    @Override
    void logDeprecationWarning(String subject) {
        logDeprecationWarning(subject, getSourceDetails(stackTrace))
    }

    @Override
    void logDeprecationWarning(String subject, String scriptName, int lineNumber) {
        logDeprecationWarning(subject, getSourceDetails(scriptName, lineNumber))
    }

    protected void logDeprecationWarning(String subject, String details) {
        logWarning('%s is deprecated (%s)', subject, details)
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

    protected static List<StackTraceElement> getStackTrace() {
        List<StackTraceElement> result = currentThread().stackTrace.findAll { isApplicationClass(it.className) }
        result[4..-1]
    }

    protected static String getSourceDetails(List<StackTraceElement> stackTrace) {
        StackTraceElement source = stackTrace.find { !it.className.startsWith('javaposse.jobdsl.') }
        getSourceDetails(source?.fileName, source == null ? -1 : source.lineNumber)
    }

    protected static String getSourceDetails(String scriptName, int lineNumber) {
        String details = 'unknown source'
        if (scriptName != null) {
            details = scriptName.matches(/script\d+\.groovy/) ? 'DSL script' : scriptName
            if (lineNumber > 0) {
                details += ", line ${lineNumber}"
            }
        }
        details
    }

    protected void logWarning(String message, Object... args) {
        outputStream.printf("Warning: $message\n", args)
    }
}
