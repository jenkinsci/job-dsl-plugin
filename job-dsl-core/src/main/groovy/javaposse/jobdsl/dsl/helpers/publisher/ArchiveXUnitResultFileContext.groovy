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

    /**
     * Specifies where to find test results.
     */
    void pattern(String pattern) {
        this.pattern = pattern
    }

    /**
     * If set, does not fail the build if no test results have been found. Defaults to {@code false}.
     */
    void skipNoTestFiles(boolean skipNoTestFiles = true) {
        this.skipNoTestFiles = skipNoTestFiles
    }

    /**
     * If set, fails the build if the test results have not been updated. Defaults to {@code true}.
     */
    void failIfNotNew(boolean failIfNotNew = true) {
        this.failIfNotNew = failIfNotNew
    }

    /**
     * If set, deletes temporary JUnit files. Defaults to {@code true}.
     */
    void deleteOutputFiles(boolean deleteOutputFiles = true) {
        this.deleteOutputFiles = deleteOutputFiles
    }

    /**
     * If set, fails the build on processing errors. Defaults to {@code true}.
     */
    void stopProcessingIfError(boolean stopProcessingIfError = true) {
        this.stopProcessingIfError = stopProcessingIfError
    }
}
