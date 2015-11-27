package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SubversionTagContext implements Context {
    String tagBaseUrl = "http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}"
    String tagComment = "Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}."
    String tagDeleteComment = 'Delete old tag by svn-tag Jenkins plugin.'

    /**
     * Specifies the URL that points the top level tag directory. Defaults to
     * {@code "http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}"}.
     */
    void tagBaseUrl(String tagBaseUrl) {
        this.tagBaseUrl = tagBaseUrl
    }

    /**
     * Sets the svn commit message. Defaults to {@code "Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}."}.
     */
    void tagComment(String tagComment) {
        this.tagComment = tagComment
    }

    /**
     * Sets the svn commit message. Defaults to {@code 'Delete old tag by svn-tag Jenkins plugin.'}.
     */
    void tagDeleteComment(String tagDeleteComment) {
        this.tagDeleteComment = tagDeleteComment
    }
}
