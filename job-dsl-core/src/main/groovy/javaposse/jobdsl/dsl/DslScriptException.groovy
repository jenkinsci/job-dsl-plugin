package javaposse.jobdsl.dsl

import static javaposse.jobdsl.dsl.DslScriptHelper.getSourceDetails

/**
 * Indicates a DSL script problem on user-level. The problem will be logged to the build log with a pointer to the
 * source location in the DSL script, but without a stack trace.
 *
 * @since 1.36
 */
class DslScriptException extends DslException {
    DslScriptException(String message) {
        super(message)
    }

    DslScriptException(String message, Throwable cause) {
        super(message, cause)
    }

    @Override
    String getMessage() {
        "(${getSourceDetails(cause ? cause.stackTrace : stackTrace)}) ${super.message}"
    }
}
