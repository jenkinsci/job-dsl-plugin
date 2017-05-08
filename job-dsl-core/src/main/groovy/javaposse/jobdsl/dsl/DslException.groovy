package javaposse.jobdsl.dsl

class DslException extends RuntimeException {
    DslException(String message) {
        super(message)
    }

    /**
     * @since 1.36
     */
    DslException(String message, Throwable cause) {
        super(message, cause)
    }

    /**
     * @since 1.62
     */
    DslException(Throwable cause) {
        super(cause)
    }
}
