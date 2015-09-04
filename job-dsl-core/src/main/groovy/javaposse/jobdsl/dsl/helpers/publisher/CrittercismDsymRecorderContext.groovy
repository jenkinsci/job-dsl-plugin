package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CrittercismDsymRecorderContext implements Context {
    String apiKey
    String appID
    String filePath

    /**
     * Sets the Crittercism API key.
     *
     * For security reasons, do not use a hard-coded API key. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void apiKey(String apiKey) {
        this.apiKey = apiKey
    }

    /**
     * Set the Crittercism app ID.
     */
    void appID(String appID) {
        this.appID = appID
    }

    /**
     * Specifies the path to the dSYM file.
     */
    void filePath(String filePath) {
        this.filePath = filePath
    }
}
