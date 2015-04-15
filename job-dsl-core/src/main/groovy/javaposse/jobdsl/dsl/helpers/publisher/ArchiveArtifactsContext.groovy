package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchiveArtifactsContext implements Context {
    final List<String> patterns = []
    String excludesValue = null
    Boolean latestOnlyValue = false
    Boolean allowEmptyValue = null
    Boolean fingerprintValue = false
    Boolean onlyIfSuccessfulValue = false
    Boolean defaultExcludesValue = true

    void pattern(String glob) {
        patterns << glob
    }

    void exclude(String glob) {
        excludesValue = glob
    }

    void latestOnly(Boolean val = true) {
        latestOnlyValue = val
    }

    void allowEmpty(Boolean val = true) {
        allowEmptyValue = val
    }

    void fingerprint(Boolean val = true) {
        fingerprintValue = val
    }

    void onlyIfSuccessful(Boolean val = true) {
        onlyIfSuccessfulValue = val
    }

    void defaultExcludes(Boolean val = true) {
        defaultExcludesValue = val
    }
}
