package javaposse.jobdsl.dsl.helpers.publisher

class ArchiveXUnitCustomToolContext extends ArchiveXUnitResultFileContext {
    String stylesheet = ''

    ArchiveXUnitCustomToolContext() {
        super('CustomType')
    }

    void stylesheet(String stylesheet) {
        this.stylesheet = stylesheet
    }
}
