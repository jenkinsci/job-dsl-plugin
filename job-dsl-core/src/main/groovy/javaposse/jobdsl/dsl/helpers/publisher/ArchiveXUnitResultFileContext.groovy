package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchiveXUnitResultFileContext implements Context {
    final String type
    String pattern = ''
    boolean skipNoTestFiles = false
    boolean failIfNotNew = true
    boolean deleteOutputFiles = true
    boolean stopProcessingIfError = true

    ArchiveXUnitResultFileContext(String type) {
        this.type = type
    }

    void pattern(String pattern) {
        this.pattern = pattern
    }

    void skipNoTestFiles(boolean skipNoTestFiles = true) {
        this.skipNoTestFiles = skipNoTestFiles
    }

    void failIfNotNew(boolean failIfNotNew = true) {
        this.failIfNotNew = failIfNotNew
    }

    void deleteOutputFiles(boolean deleteOutputFiles = true) {
        this.deleteOutputFiles = deleteOutputFiles
    }

    void stopProcessingIfError(boolean stopProcessingIfError = true) {
        this.stopProcessingIfError = stopProcessingIfError
    }
}
