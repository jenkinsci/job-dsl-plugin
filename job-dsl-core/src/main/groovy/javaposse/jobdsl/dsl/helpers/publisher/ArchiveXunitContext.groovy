package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class ArchiveXunitContext implements Context {
    static enum ThresholdMode {
        NUMBER(1),
        PERCENT(2)

        public final int xmlValue

        ThresholdMode(int xmlValue) {
            this.xmlValue = xmlValue
        }
    }

    ThresholdContext failedThresholdsContext = new ThresholdContext()
    ThresholdContext skippedThresholdsContext = new ThresholdContext()
    ThresholdMode thresholdMode = ThresholdMode.NUMBER
    int timeMargin = 3000
    def resultFiles = []

    void failedThresholds(Closure thresholdsClosure) {
        AbstractContextHelper.executeInContext(thresholdsClosure, failedThresholdsContext)
    }

    void skippedThresholds(Closure thresholdsClosure) {
        AbstractContextHelper.executeInContext(thresholdsClosure, skippedThresholdsContext)
    }

    void thresholdMode(String thresholdMode) {
        this.thresholdMode = ThresholdMode."${thresholdMode.toUpperCase()}"
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

    void customTool(Closure resultFileClosure) {
        addResultFile('CustomType', resultFileClosure)
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

    private void addResultFile(String type, Closure resultFileClosure) {
        ResultFileContext resultFileContext = new ResultFileContext()
        AbstractContextHelper.executeInContext(resultFileClosure, resultFileContext)

        resultFileContext.type(type)
        resultFiles << resultFileContext
    }

    Node createXunitNode() {
        NodeBuilder nodeBuilder = NodeBuilder.newInstance()

        Node xunitNode = nodeBuilder.'xunit' {
            types {
                resultFiles.each { ResultFileContext resultFile ->
                    "${resultFile.type}" {
                        pattern resultFile.pattern
                        skipNoTestFiles resultFile.skipNoTestFiles ? 'true' : 'false'
                        failIfNotNew resultFile.failIfNotNew ? 'true' : 'false'
                        deleteOutputFiles resultFile.deleteOutputFiles ? 'true' : 'false'
                        stopProcessingIfError resultFile.stopProcessingIfError ? 'true' : 'false'
                        if (resultFile.type == 'CustomType') {  // Only valid for CustomType
                            customXSL resultFile.styleSheet
                        }
                    }
                }
            }
            thresholds {
                'org.jenkinsci.plugins.xunit.threshold.FailedThreshold' {
                    unstableThreshold failedThresholdsContext.unstable
                    unstableNewThreshold failedThresholdsContext.unstableNew
                    failureThreshold failedThresholdsContext.failure
                    failureNewThreshold failedThresholdsContext.failureNew
                }
                'org.jenkinsci.plugins.xunit.threshold.SkippedThreshold' {
                    unstableThreshold skippedThresholdsContext.unstable
                    unstableNewThreshold skippedThresholdsContext.unstableNew
                    failureThreshold skippedThresholdsContext.failure
                    failureNewThreshold skippedThresholdsContext.failureNew
                }
            }
            thresholdMode thresholdMode.xmlValue
            extraConfiguration {
                testTimeMargin timeMargin
            }
        }
        return xunitNode
    }

    class ThresholdContext implements Context {
        int unstable = 0
        int unstableNew = 0
        int failure = 0
        int failureNew = 0

        void unstable(int unstable) {
            this.unstable = unstable
        }

        void unstableNew(int unstableNew) {
            this.unstableNew = unstableNew
        }

        void failure(int failure) {
            this.failure = failure
        }

        void failureNew(int failureNew) {
            this.failureNew = failureNew
        }
    }

    class ResultFileContext implements Context {
        String type
        String pattern = ''
        boolean skipNoTestFiles = false
        boolean failIfNotNew = true
        boolean deleteOutputFiles = true
        boolean stopProcessingIfError = true
        String styleSheet = ''

        void type(String type) {
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
}