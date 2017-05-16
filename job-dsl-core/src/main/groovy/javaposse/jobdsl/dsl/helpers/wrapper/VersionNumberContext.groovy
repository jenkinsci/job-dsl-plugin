package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class VersionNumberContext implements Context {

    String format
    String nameVariable
    String prefixVariable
    boolean skipFailedBuilds
    boolean displayBuildName
    String startDate
    String buildsToday
    String buildsThisWeek
    String buildsThisMonth
    String buildsThisYear
    String buildsAllTime

    /**
     * @param format Version Number Format String
     * @param nameVariable The version number will be stored in the environment variable specified here.
     */
    VersionNumberContext(String format, String nameVariable) {
        this.format = format
        this.nameVariable = nameVariable
    }

    /**
     * @param prefixVariable The prefix variable name is the environment variable specified here
     * to allow using the same build numbers for all the release tags.
     */
    void prefixVariable(String prefixVariable) {
        this.prefixVariable = prefixVariable
    }

    /**
     * @param skipFailedBuilds Don't increment builds today / this month / this year / all time after a failed build.
     */
    void skipFailedBuilds(boolean skipFailedBuilds = true) {
        this.skipFailedBuilds = skipFailedBuilds
    }

    /**
     * @param displayBuildName Use the formatted version number for build display name.
     */
    void displayBuildName(boolean displayBuildName = true) {
        this.displayBuildName = displayBuildName
    }

    /**
     * @param startDate The date the project began, in the format yyyy-MM-dd. This is used in calculating the
     *                  number of months and years since the beginning of the project.
     */
    void startDate(String startDate) {
        this.startDate = startDate
    }

    /**
     * @param buildsToday Number of builds today
     */
    void buildsToday(String buildsToday) {
        this.buildsToday = buildsToday
    }

    /**
     * @param buildsThisWeek Number of builds this week
     */
    void buildsThisWeek(String buildsThisWeek) {
        this.buildsThisWeek = buildsThisWeek
    }

    /**
     * @param buildsThisMonth Number of builds this month
     */
    void buildsThisMonth(String buildsThisMonth) {
        this.buildsThisMonth = buildsThisMonth
    }

    /**
     * @param buildsThisYear Number of builds this year
     */
    void buildsThisYear(String buildsThisYear) {
        this.buildsThisYear = buildsThisYear
    }

    /**
     * @param buildsAllTime Number of builds since the start of the project
     */
    void buildsAllTime(String buildsAllTime) {
        this.buildsAllTime = buildsAllTime
    }
}
