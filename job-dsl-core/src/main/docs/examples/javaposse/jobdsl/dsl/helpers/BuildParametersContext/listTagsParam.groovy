job('example') {
    parameters {
        listTagsParam('myParameterName', "http://kenai.com/svn/myProject/tags", /^mytagsfilterregex/, true, true)
    }
}
