package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.WithXmlAction

class ArchivePerformanceContext implements Context {

    List<WithXmlAction> withXmlActions = []

    int errorFailedThreshold = 0
    int errorUnstableThreshold = 0
    String errorUnstableResponseTimeThreshold = ''
    float relativeFailedThresholdPositive = 0.0f
    float relativeFailedThresholdNegative = 0.0f
    float relativeUnstableThresholdPositive = 0.0f
    float relativeUnstableThresholdNegative = 0.0f
    int nthBuildNumber = 0
    boolean modeRelativeThresholds = false
    String configType = 'ART'
    boolean modeOfThreshold = false
    boolean compareBuildPrevious = false
    boolean modePerformancePerTestCase = false

    void compareBuildPrevious(boolean compareBuildPrevious) {
        this.compareBuildPrevious = compareBuildPrevious
    }

    void configType(String configType) {
        this.configType = configType
    }

    void errorFailedThreshold(int errorFailedThreshold) {
        this.errorFailedThreshold = errorFailedThreshold
    }

    void errorUnstableResponseTimeThreshold(String errorUnstableResponseTimeThreshold) {
        this.errorUnstableResponseTimeThreshold = errorUnstableResponseTimeThreshold
    }

    void errorUnstableThreshold(int errorUnstableThreshold) {
        this.errorUnstableThreshold = errorUnstableThreshold
    }

    void modeOfThreshold(boolean modeOfThreshold) {
        this.modeOfThreshold = modeOfThreshold
    }

    void modePerformancePerTestCase(boolean modePerformancePerTestCase) {
        this.modePerformancePerTestCase = modePerformancePerTestCase
    }

    void modeRelativeThresholds(boolean modeRelativeThresholds) {
        this.modeRelativeThresholds = modeRelativeThresholds
    }

    void nthBuildNumber(int nthBuildNumber) {
        this.nthBuildNumber = nthBuildNumber
    }

    void relativeFailedThresholdNegative(float relativeFailedThresholdNegative) {
        this.relativeFailedThresholdNegative = relativeFailedThresholdNegative
    }

    void relativeFailedThresholdPositive(float relativeFailedThresholdPositive) {
        this.relativeFailedThresholdPositive = relativeFailedThresholdPositive
    }

    void relativeUnstableThresholdNegative(float relativeUnstableThresholdNegative) {
        this.relativeUnstableThresholdNegative = relativeUnstableThresholdNegative
    }

    void relativeUnstableThresholdPositive(float relativeUnstableThresholdPositive) {
        this.relativeUnstableThresholdPositive = relativeUnstableThresholdPositive
    }

    void parsers(@DslContext(ArchivePerformanceParserContext) Closure closure) {
        ArchivePerformanceParserContext context = new ArchivePerformanceParserContext()
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.parserNodes.each {
                project / 'parsers' << it
            }
        }
    }
}
