package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class JobTemplateMissingException extends RuntimeException {
    private JobTemplateMissingException(String templateName) {
        super("The template job with name ${templateName} does not exist.")
    }
}
