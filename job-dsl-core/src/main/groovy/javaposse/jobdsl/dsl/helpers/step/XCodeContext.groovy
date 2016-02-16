package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class XCodeContext implements Context {
    String profile

    /**
     * Credential ID from uploaded *.developerprofile.
     */
    void profile(String profile) {
        this.profile = profile
    }
}
