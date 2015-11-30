job('example') {
    publishers {
        svnTag {
            baseUrl("http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}")
            comment("Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}.")
            deleteComment('Delete old tag by svn-tag Jenkins plugin.')
        }
    }
}
