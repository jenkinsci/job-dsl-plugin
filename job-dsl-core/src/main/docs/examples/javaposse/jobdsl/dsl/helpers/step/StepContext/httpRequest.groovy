job('example') {
    steps {
        httpRequest('http://www.example.com') {
            httpMode('POST')
            authentication('Credentials')
            returnCodeBuildRelevant()
            logResponseBody()
        }
    }
}
