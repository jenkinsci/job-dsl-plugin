package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class ArchiveXUnitContext implements Context {
    ArchiveXUnitThresholdContext failedThresholdsContext = new ArchiveXUnitThresholdContext()
    ArchiveXUnitThresholdContext skippedThresholdsContext = new ArchiveXUnitThresholdContext()
    ThresholdMode thresholdMode = ThresholdMode.NUMBER
    int timeMargin = 3000
    List<ArchiveXUnitResultFileContext> resultFiles = []

    void failedThresholds(@DslContext(ArchiveXUnitThresholdContext) Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, failedThresholdsContext)
    }

    void skippedThresholds(@DslContext(ArchiveXUnitThresholdContext) Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, skippedThresholdsContext)
    }

    void thresholdMode(ThresholdMode thresholdMode) {
        this.thresholdMode = thresholdMode
    }

    void timeMargin(int timeMargin) {
        this.timeMargin = timeMargin
    }

    void aUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('AUnitJunitHudsonTestType', resultFileClosure)
    }

    void boostTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('BoostTestJunitHudsonTestType', resultFileClosure)
    }

    void cTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CTestType', resultFileClosure)
    }

    void check(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CheckType', resultFileClosure)
    }

    void cppTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CppTestJunitHudsonTestType', resultFileClosure)
    }

    void cppUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CppUnitJunitHudsonTestType', resultFileClosure)
    }

    void embUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('EmbUnitType', resultFileClosure)
    }

    void fpcUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('FPCUnitJunitHudsonTestType', resultFileClosure)
    }

    void googleTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('GoogleTestType', resultFileClosure)
    }

    void gtester(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('GTesterJunitHudsonTestType', resultFileClosure)
    }

    void jUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('JUnitType', resultFileClosure)
    }

    void msTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('MSTestJunitHudsonTestType', resultFileClosure)
    }

    void mbUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('MbUnitType', resultFileClosure)
    }

    void nUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('NUnitJunitHudsonTestType', resultFileClosure)
    }

    void phpUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('PHPUnitJunitHudsonTestType', resultFileClosure)
    }

    void qTestLib(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('QTestLibType', resultFileClosure)
    }

    void unitTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('UnitTestJunitHudsonTestType', resultFileClosure)
    }

    void valgrind(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('ValgrindJunitHudsonTestType', resultFileClosure)
    }

    void customTool(@DslContext(ArchiveXUnitCustomToolContext) Closure resultFileClosure) {
        ArchiveXUnitResultFileContext resultFileContext = new ArchiveXUnitCustomToolContext()
        ContextHelper.executeInContext(resultFileClosure, resultFileContext)

        resultFiles << resultFileContext
    }

    private void addResultFile(String type, Closure resultFileClosure) {
        ArchiveXUnitResultFileContext resultFileContext = new ArchiveXUnitResultFileContext(type)
        ContextHelper.executeInContext(resultFileClosure, resultFileContext)

        resultFiles << resultFileContext
    }

    static enum ThresholdMode {
        NUMBER(1),
        PERCENT(2)

        final int xmlValue

        ThresholdMode(int xmlValue) {
            this.xmlValue = xmlValue
        }
    }
}
