package javaposse.jobdsl.dsl

class JobTemplateMissingException extends RuntimeException {
    private JobTemplateMissingException(String templateName) {
        super("The template job with name ${templateName} does not exist.")
    }
}
