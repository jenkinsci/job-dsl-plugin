package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

class RobotFrameworkContext extends AbstractContext {
    double passThreshold = 100.0
    double unstableThreshold = 0.0
    boolean onlyCritical = false
    boolean disableArchiveOutput = false
    String outputPath = 'target/robotframework-reports'
    String reportFileName = 'report.html'
    String logFileName = 'log.html'
    String outputFileName = 'output.xml'
    List<String> otherFiles = []

    RobotFrameworkContext(JobManagement jobManagement) {
        super(jobManagement)
    }

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

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'robot', minimumVersion = '1.4.3')
    void disableArchiveOutput(boolean disableArchiveOutput = true) {
        this.disableArchiveOutput = disableArchiveOutput
    }

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'robot', minimumVersion = '1.2.1')
    void otherFiles(String... files) {
        for (String file : files) {
            Preconditions.checkNotNull(file, 'file cannot be null')
        }
        this.otherFiles.addAll(files)
    }
}
