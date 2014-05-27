import javaposse.jobdsl.dsl.helpers.Context

class ArchiveXunitResultFileContext implements Context {
    String type
    String pattern = ''
    boolean skipNoTestFiles = false
    boolean failIfNotNew = true
    boolean deleteOutputFiles = true
    boolean stopProcessingIfError = true
    String styleSheet = ''

    public ArchiveXunitResultFileContext(String type) {
        this.type = type
    }

    void pattern(String pattern) {
        this.pattern = pattern
    }

    void skipNoTestFiles(boolean skipNoTestFiles = true) {
        this.skipNoTestFiles = skipNoTestFiles
    }

    void failIfNotNew(boolean failIfNotNew) {
        this.failIfNotNew = failIfNotNew
    }

    void deleteOutputFiles(boolean deleteOutputFiles) {
        this.deleteOutputFiles = deleteOutputFiles
    }

    void stopProcessingIfError(boolean stopProcessingIfError) {
        this.stopProcessingIfError = stopProcessingIfError
    }

    void styleSheet(String styleSheet) {
        this.styleSheet = styleSheet
    }
}
