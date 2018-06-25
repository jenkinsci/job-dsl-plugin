package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class ArchiveXUnitContext extends AbstractContext {
    ArchiveXUnitThresholdContext failedThresholdsContext = new ArchiveXUnitThresholdContext()
    ArchiveXUnitThresholdContext skippedThresholdsContext = new ArchiveXUnitThresholdContext()
    ThresholdMode thresholdMode = ThresholdMode.NUMBER
    int timeMargin = 3000
    List<ArchiveXUnitResultFileContext> resultFiles = []

    ArchiveXUnitContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies thresholds for failed tests.
     */
    void failedThresholds(@DslContext(ArchiveXUnitThresholdContext) Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, failedThresholdsContext)
    }

    /**
     * Specifies thresholds for skipped tests.
     */
    void skippedThresholds(@DslContext(ArchiveXUnitThresholdContext) Closure thresholdsClosure) {
        ContextHelper.executeInContext(thresholdsClosure, skippedThresholdsContext)
    }

    /**
     * Specifies whether thresholds are given as total number or percentages. Defaults to
     * {@code ThresholdMode.NUMBER}.
     */
    void thresholdMode(ThresholdMode thresholdMode) {
        this.thresholdMode = thresholdMode
    }

    /**
     * Specifies the time span in milliseconds used to consider reports as not updated. Defaults to 3 seconds.
     */
    void timeMargin(int timeMargin) {
        this.timeMargin = timeMargin
    }

    /**
     * Adds an AUnit report.
     */
    void aUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('AUnitJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a Boost test report.
     */
    void boostTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('BoostTestJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a CTest report.
     */
    void cTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CTestType', resultFileClosure)
    }

    /**
     * Adds a Check report.
     */
    void check(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CheckType', resultFileClosure)
    }

    /**
     * Adds a CppTest report.
     */
    void cppTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CppTestJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a CppUnit report.
     */
    void cppUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('CppUnitJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds an embUnit report.
     */
    void embUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('EmbUnitType', resultFileClosure)
    }

    /**
     * Adds a FPCUnit report.
     */
    void fpcUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('FPCUnitJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a Google Test report.
     */
    void googleTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('GoogleTestType', resultFileClosure)
    }

    /**
     * Adds a gtester report.
     *
     * @since 1.36
     */
    @RequiresPlugin(id = 'xunit', minimumVersion = '1.95')
    void gtester(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('GTesterJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a JUnit report.
     */
    void jUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('JUnitType', resultFileClosure)
    }

    /**
     * Adds a MSTest report.
     */
    void msTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('MSTestJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a MbUnit report.
     */
    void mbUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('MbUnitType', resultFileClosure)
    }

    /**
     * Adds a NUnit report.
     */
    void nUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('NUnitJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a NUnit3 report.
     *
     * @since 1.70
     */
    @RequiresPlugin(id = 'xunit', minimumVersion = '1.104')
    void nUnit3(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('NUnit3TestType', resultFileClosure)
    }

    /**
     * Adds an PHPUnit report.
     */
    void phpUnit(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('PHPUnitJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds an QTestLib report.
     */
    void qTestLib(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('QTestLibType', resultFileClosure)
    }

    /**
     * Adds an unittest report.
     */
    void unitTest(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('UnitTestJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds a Valgrind report.
     */
    void valgrind(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('ValgrindJunitHudsonTestType', resultFileClosure)
    }

    /**
     * Adds an XUnit.NET v2 report.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'xunit', minimumVersion = '1.93')
    void xUnitDotNET(@DslContext(ArchiveXUnitResultFileContext) Closure resultFileClosure) {
        addResultFile('XUnitDotNetTestType', resultFileClosure)
    }

    /**
     * Adds a report from a custom tool.
     */
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
