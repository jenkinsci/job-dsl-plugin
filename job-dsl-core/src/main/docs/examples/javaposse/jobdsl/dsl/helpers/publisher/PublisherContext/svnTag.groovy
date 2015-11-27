job('example') {
    publishers {
        svnTag {
            tagBaseUrl("http://subversion_host/project/tags/last-successful/\${env['JOB_NAME']}")
            tagComment("Tagged by Jenkins svn-tag plugin. Build:\${env['BUILD_TAG']}.")
            tagDeleteComment('Delete old tag by svn-tag Jenkins plugin.')
        }
    }
}
