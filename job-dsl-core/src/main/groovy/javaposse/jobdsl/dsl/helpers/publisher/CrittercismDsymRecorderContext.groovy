package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CrittercismDsymRecorderContext implements Context {
    String apiKey = ''
    String appID = ''
    String filePath = ''

    void apiKey(String apiKey) {
        this.apiKey = apiKey
    }

    void appID(String appID) {
        this.appID = appID
    }

    void filePath(String filePath) {
        this.filePath = filePath
    }
}
