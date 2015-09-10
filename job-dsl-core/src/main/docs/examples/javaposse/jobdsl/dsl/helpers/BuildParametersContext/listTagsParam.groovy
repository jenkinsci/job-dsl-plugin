job('example-1') {
    parameters {
        listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', /^mytagsfilterregex/, true)
    }
}

job('example-2') {
    parameters {
        listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags') {
            tagFilterRegex(/^mytagsfilterregex/)
            credentialsId('company-svn-server')
            sortNewestFirst()
        }
    }
}
