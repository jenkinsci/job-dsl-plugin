// define the build name based on the build number and an environment variable
job('example') {
    wrappers {
        buildName('#${BUILD_NUMBER} on ${ENV,var="BRANCH"}')
    }
}
