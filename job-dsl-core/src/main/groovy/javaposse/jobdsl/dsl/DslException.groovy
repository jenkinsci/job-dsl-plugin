package javaposse.jobdsl.dsl

class DslException extends RuntimeException {
    DslException(String message) {
        super(message)
    }

    DslException(String message, Throwable cause) {
        super(message, cause)
    }
}
