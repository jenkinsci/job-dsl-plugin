package javaposse.jobdsl.dsl

import static java.lang.Thread.currentThread
import static org.codehaus.groovy.runtime.StackTraceUtils.isApplicationClass

/**
 * Abstract version of JobManagement to minimize impact on future API changes
 */
abstract class AbstractJobManagement implements JobManagement {
    protected PrintStream out

    protected AbstractJobManagement(PrintStream out) {
        this.out = out
    }

    protected AbstractJobManagement() {
        this(System.out)
    }

    @Override
    PrintStream getOutputStream() {
        out
    }

    @Override
    void queueJob(String jobName) throws NameNotProvidedException {
        validateNameArg(jobName)
    }

    @Override
    InputStream streamFileInWorkspace(String filePath) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    String readFileInWorkspace(String filePath) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    String readFileInWorkspace(String jobName, String filePath) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    void logDeprecationWarning() {
        List<StackTraceElement> currentStackTrack = stackTrace
        String details = getSourceDetails(currentStackTrack)
        logWarning('%s is deprecated (%s)', currentStackTrack[0].methodName, details)
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
        String details = 'unknown source'
        StackTraceElement source = stackTrace.find { !it.className.startsWith('javaposse.jobdsl.') }
        if (source != null && source.fileName != null) {
            details = source.fileName.matches(/script\d+\.groovy/) ? 'DSL script' : source.fileName
            if (source.lineNumber > 0) {
                details += ", line ${source.lineNumber}"
            }
        }
        details
    }

    protected void logWarning(String message, Object... args) {
        outputStream.printf("Warning: $message\n", args)
    }
}
