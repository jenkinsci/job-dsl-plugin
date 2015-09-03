package javaposse.jobdsl.dsl.helpers.publisher

class ArchiveXUnitCustomToolContext extends ArchiveXUnitResultFileContext {
    String stylesheet = ''

    ArchiveXUnitCustomToolContext() {
        super('CustomType')
    }

    /**
     * Sets the path to a stylesheet for transforming custom reports.
     */
    void stylesheet(String stylesheet) {
        this.stylesheet = stylesheet
    }
}
