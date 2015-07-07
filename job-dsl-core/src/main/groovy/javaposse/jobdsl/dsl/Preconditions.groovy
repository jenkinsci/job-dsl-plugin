package javaposse.jobdsl.dsl

/**
 * Provides methods for argument validation to be used in DSL methods.
 *
 * @since 1.36
 */
class Preconditions {
    private Preconditions() {
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the
     * calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will
     *     be converted to a string using {@link String#valueOf(Object)}
     * @throws DslScriptException if {@code expression} is false
     */
    static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new DslScriptException(String.valueOf(errorMessage))
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will
     *     be converted to a string using {@link String#valueOf(Object)}
     * @throws DslScriptException if {@code expression} is false
     */
    static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new DslScriptException(String.valueOf(errorMessage))
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will
     *     be converted to a string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws DslScriptException if {@code reference} is null
     */
    static void checkNotNull(Object reference, Object errorMessage) {
        if (reference == null) {
            throw new DslScriptException(String.valueOf(errorMessage))
        }
    }

    /**
     * Ensures that a string passed as a parameter is not {@code null} or an empty string.
     *
     * @param string a string
     * @param errorMessage the exception message to use if the check fails; will
     *     be converted to a string using {@link String#valueOf(Object)}
     * @throws DslScriptException if {@code string} is {@code null} or an empty string
     */
    static void checkNotNullOrEmpty(String string, Object errorMessage) {
        if (!string) {
            throw new DslScriptException(String.valueOf(errorMessage))
        }
    }
}
