package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
public class JobTemplateMissingException extends Exception {
    private JobTemplateMissingException(String templateName) {
        super("The template job with name " + templateName + " does not exist.")
    }
}
