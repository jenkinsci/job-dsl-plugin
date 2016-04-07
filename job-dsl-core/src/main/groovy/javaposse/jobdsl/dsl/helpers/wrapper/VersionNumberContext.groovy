package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class VersionNumberContext implements Context {

    String environmentVariableName = 'VERSION'
    String versionNumberString = ''
    String environmentPrefixVariable = ''
    boolean skipFailedBuilds = false
    boolean useAsBuildDisplayName = true
    String projectStartDate = new Date()
    String oBuildsToday = ''
    String oBuildsThisWeek = ''
    String oBuildsThisMonth = ''
    String oBuildsThisYear = ''
    String oBuildsAllTime = ''

    void environmentVariableName(String environmentVariableName) {
        this.environmentVariableName = environmentVariableName
    }

    void versionNumberString(String versionNumberString) {
        this.versionNumberString = versionNumberString
    }

    void environmentPrefixVariable(String environmentPrefixVariable) {
        this.environmentPrefixVariable = environmentPrefixVariable
    }

    void skipFailedBuilds(boolean skipFailedBuilds) {
        this.skipFailedBuilds = skipFailedBuilds
    }

    void useAsBuildDisplayName(boolean useAsBuildDisplayName) {
        this.useAsBuildDisplayName = useAsBuildDisplayName
    }

    void projectStartDate(String projectStartDate) {
        this.projectStartDate = projectStartDate
    }

    void oBuildsToday(String oBuildsToday) {
        this.oBuildsToday = oBuildsToday
    }

    void oBuildsThisWeek(String oBuildsThisWeek) {
        this.oBuildsThisWeek = oBuildsThisWeek
    }

    void oBuildsThisMonth(String oBuildsThisMonth) {
        this.oBuildsThisMonth = oBuildsThisMonth
    }

    void oBuildsThisYear(String oBuildsThisYear) {
        this.oBuildsThisYear = oBuildsThisYear
    }

    void oBuildsAllTime(String oBuildsAllTime) {
        this.oBuildsAllTime = oBuildsAllTime
    }

}
