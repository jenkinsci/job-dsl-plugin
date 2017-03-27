package javaposse.jobdsl.dsl

import static java.lang.Thread.currentThread
import static org.codehaus.groovy.runtime.StackTraceUtils.isApplicationClass

/**
 * Helper class for dealing with stack traces originating from DSL scripts and finding the DSL script source line that
 * led to a stack trace.
 *
 * @since 1.36
 */
class DslScriptHelper {
    private static final Set<String> CLASS_FILTER = [
            DslScriptHelper.name,
            JobManagement.name,
            AbstractJobManagement.name,
            'javaposse.jobdsl.plugin.InterruptibleJobManagement',
    ]

    private DslScriptHelper() {
    }

    static List<StackTraceElement> getStackTrace() {
        currentThread().stackTrace.findAll { StackTraceElement element ->
            isApplicationClass(element.className) &&
                    !(element.className in CLASS_FILTER) &&
                    !CLASS_FILTER.any { element.className.startsWith(it + '$') }
        }
    }

    static String getSourceDetails() {
        getSourceDetails(stackTrace)
    }

    static String getSourceDetails(StackTraceElement[] stackTrace) {
        getSourceDetails(stackTrace as List<StackTraceElement>)
    }

    static String getSourceDetails(List<StackTraceElement> stackTrace) {
        StackTraceElement source = stackTrace.find {
            isApplicationClass(it.className) && !it.className.startsWith('javaposse.jobdsl.') &&
                    !it.className.startsWith('org.kohsuke.groovy.sandbox.') &&
                    !it.className.startsWith('org.jenkinsci.plugins.scriptsecurity.sandbox.')
        }
        getSourceDetails(source?.fileName, source == null ? -1 : source.lineNumber)
    }

    static String getSourceDetails(String scriptName, int lineNumber) {
        String details = 'unknown source'
        if (scriptName != null) {
            details = scriptName.matches(/script\d+\.groovy/) ? 'DSL script' : scriptName
            if (lineNumber > 0) {
                details += ", line ${lineNumber}"
            }
        }
        details
    }
}
