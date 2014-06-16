package javaposse.jobdsl.plugin

class ExtensionPointException extends RuntimeException {
    ExtensionPointException(String message) {
        super(message)
    }

    ExtensionPointException(String message, Throwable cause) {
        super(message, cause)
    }
}
