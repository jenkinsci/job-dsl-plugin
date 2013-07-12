package javaposse.jobdsl.plugin;

public class ExtensionPointException extends RuntimeException {
    public ExtensionPointException(String message) {
        super(message);
    }

    public ExtensionPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
