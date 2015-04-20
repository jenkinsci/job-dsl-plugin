package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context

/**
 * A Job DSL context for Robot Framework. Specifically,
 * take a look at
 * https://github.com/jenkinsci/robot-plugin/blob/master/src/main/java/hudson/plugins/robot/RobotPublisher.java
 */
class RobotFrameworkContext implements Context {
    static final String DEFAULT_OUTPUT_PATH = 'target/robotframework-reports'
    static final String DEFAULT_REPORT_FILE_NAME = 'report.html'
    static final String DEFAULT_OUTPUT_FILE_NAME = 'output.xml'
    static final String DEFAULT_LOG_FILE_NAME = 'log.html'

    double passThreshold = 100.0
    double unstableThreshold = 0.0
    boolean onlyCritical = false
    boolean disableArchiveOutput = false
    String outputPath = DEFAULT_OUTPUT_PATH
    String reportFileName = DEFAULT_REPORT_FILE_NAME
    String logFileName = DEFAULT_LOG_FILE_NAME
    String outputFileName = DEFAULT_OUTPUT_FILE_NAME
    List<String> otherFiles = []

    void passThreshold(double passThreshold) {
        Preconditions.checkArgument(passThreshold >= 0.0 && passThreshold <= 100.0,
            'passThreshold should be a floating point in range (0, 100)')
        this.passThreshold = passThreshold
    }

    void unstableThreshold(double unstableThreshold) {
        Preconditions.checkArgument(unstableThreshold >= 0.0 && unstableThreshold <= 100.0,
            'unstableThreshold should be a floating point in range (0, 100)')
        this.unstableThreshold = unstableThreshold
    }

    void onlyCritical(boolean onlyCritical = true) {
        Preconditions.checkNotNull(onlyCritical, 'onlyCritical cannot be null')
        this.onlyCritical = onlyCritical
    }

    void outputPath(String outputPath) {
        Preconditions.checkNotNull(outputPath, 'outputPath cannot be null')
        this.outputPath = outputPath
    }

    void reportFileName(String reportFileName) {
        Preconditions.checkNotNull(reportFileName, 'reportFileName cannot be null')
        this.reportFileName = reportFileName
    }

    void logFileName(String logFileName) {
        Preconditions.checkNotNull(logFileName, 'logFileName cannot be null')
        this.logFileName = logFileName
    }

    void outputFileName(String outputFileName) {
        Preconditions.checkNotNull(outputFileName, 'outputFileName cannot be null')
        this.outputFileName = outputFileName
    }

    void disableArchiveOutput(boolean disableArchiveOutput = true) {
        this.disableArchiveOutput = disableArchiveOutput
    }

    void otherFiles(String... files) {
        for (String file : files) {
            Preconditions.checkNotNull(file, 'file cannot be null')
        }
        this.otherFiles.addAll(files)
    }

}
