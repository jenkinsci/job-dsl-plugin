package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SubversionTagContext implements Context {
    String baseUrl = "http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}"
    String comment = "Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}."
    String deleteComment = 'Delete old tag by svn-tag Jenkins plugin.'

    /**
     * Specifies the URL that points the top level tag directory. Defaults to
     * {@code "http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}"}.
     */
    void baseUrl(String baseUrl) {
        this.baseUrl = baseUrl
    }

    /**
     * Sets the svn commit message. Defaults to {@code "Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}."}.
     */
    void comment(String comment) {
        this.comment = comment
    }

    /**
     * Sets the svn commit message. Defaults to {@code 'Delete old tag by svn-tag Jenkins plugin.'}.
     */
    void deleteComment(String deleteComment) {
        this.deleteComment = deleteComment
    }
}
