package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper

class ArchiveXUnitContext implements Context {
    ArchiveXUnitThresholdContext failedThresholdsContext = new ArchiveXUnitThresholdContext()
    ArchiveXUnitThresholdContext skippedThresholdsContext = new ArchiveXUnitThresholdContext()
    ThresholdMode thresholdMode = ThresholdMode.NUMBER
    int timeMargin = 3000
    List<ArchiveXUnitResultFileContext> resultFiles = []

    void failedThresholds(Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, failedThresholdsContext)
    }

    void skippedThresholds(Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, skippedThresholdsContext)
    }

    void thresholdMode(ThresholdMode thresholdMode) {
        this.thresholdMode = thresholdMode
    }

    void timeMargin(int timeMargin) {
        this.timeMargin = timeMargin
    }

    void aUnit(Closure resultFileClosure) {
        addResultFile('AUnitJunitHudsonTestType', resultFileClosure)
    }

    void boostTest(Closure resultFileClosure) {
        addResultFile('BoostTestJunitHudsonTestType', resultFileClosure)
    }

    void cTest(Closure resultFileClosure) {
        addResultFile('CTestType', resultFileClosure)
    }

    void check(Closure resultFileClosure) {
        addResultFile('CheckType', resultFileClosure)
    }

    void cppTest(Closure resultFileClosure) {
        addResultFile('CppTestJunitHudsonTestType', resultFileClosure)
    }

    void cppUnit(Closure resultFileClosure) {
        addResultFile('CppUnitJunitHudsonTestType', resultFileClosure)
    }

    void embUnit(Closure resultFileClosure) {
        addResultFile('EmbUnitType', resultFileClosure)
    }

    void fpcUnit(Closure resultFileClosure) {
        addResultFile('FPCUnitJunitHudsonTestType', resultFileClosure)
    }

    void googleTest(Closure resultFileClosure) {
        addResultFile('GoogleTestType', resultFileClosure)
    }

    void jUnit(Closure resultFileClosure) {
        addResultFile('JUnitType', resultFileClosure)
    }

    void msTest(Closure resultFileClosure) {
        addResultFile('MSTestJunitHudsonTestType', resultFileClosure)
    }

    void mbUnit(Closure resultFileClosure) {
        addResultFile('MbUnitType', resultFileClosure)
    }

    void nUnit(Closure resultFileClosure) {
        addResultFile('NUnitJunitHudsonTestType', resultFileClosure)
    }

    void phpUnit(Closure resultFileClosure) {
        addResultFile('PHPUnitJunitHudsonTestType', resultFileClosure)
    }

    void qTestLib(Closure resultFileClosure) {
        addResultFile('QTestLibType', resultFileClosure)
    }

    void unitTest(Closure resultFileClosure) {
        addResultFile('UnitTestJunitHudsonTestType', resultFileClosure)
    }

    void valgrind(Closure resultFileClosure) {
        addResultFile('ValgrindJunitHudsonTestType', resultFileClosure)
    }

    void customTool(Closure resultFileClosure) {
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
