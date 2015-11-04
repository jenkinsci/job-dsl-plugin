package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

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

    /**
     * Specifies the threshold for marking a build as passed. Defaults to {@code 100}.
     */
    void passThreshold(double passThreshold) {
        Preconditions.checkArgument(passThreshold >= 0.0 && passThreshold <= 100.0,
            'passThreshold should be a floating point in range (0, 100)')
        this.passThreshold = passThreshold
    }

    /**
     * Specifies the threshold for marking a build as unstable. Defaults to {@code 0}.
     */
    void unstableThreshold(double unstableThreshold) {
        Preconditions.checkArgument(unstableThreshold >= 0.0 && unstableThreshold <= 100.0,
            'unstableThreshold should be a floating point in range (0, 100)')
        this.unstableThreshold = unstableThreshold
    }

    /**
     * Applies thresholds for critical tests only.
     */
    void onlyCritical(boolean onlyCritical = true) {
        this.onlyCritical = onlyCritical
    }

    /**
     * Sets the path to directory containing robot XML and HTML files relative to the build workspace. Defaults to
     * {@code 'target/robotframework-reports'}.
     */
    void outputPath(String outputPath) {
        Preconditions.checkNotNull(outputPath, 'outputPath cannot be null')
        this.outputPath = outputPath
    }

    /**
     * Sets the name of the HTML file containing the robot test report. Defaults to {@code 'report.html'}.
     */
    void reportFileName(String reportFileName) {
        Preconditions.checkNotNull(reportFileName, 'reportFileName cannot be null')
        this.reportFileName = reportFileName
    }

    /**
     * Sets the name of the HTML file containing the detailed robot test log. Defaults to {@code 'log.html'}.
     */
    void logFileName(String logFileName) {
        Preconditions.checkNotNull(logFileName, 'logFileName cannot be null')
        this.logFileName = logFileName
    }

    /**
     * Sets the name of the xml file containing the robot output. Defaults to {@code 'output.xml'}.
     */
    void outputFileName(String outputFileName) {
        Preconditions.checkNotNull(outputFileName, 'outputFileName cannot be null')
        this.outputFileName = outputFileName
    }

    /**
     * Disables archiving of output XML file to Jenkins master. Defaults to {@code false}.
     *
     * @since 1.33
     */
    void disableArchiveOutput(boolean disableArchiveOutput = true) {
        this.disableArchiveOutput = disableArchiveOutput
    }

    /**
     * Specifies a list of robot related artifacts to be saved.
     *
     * @since 1.33
     */
    void otherFiles(String... files) {
        for (String file : files) {
            Preconditions.checkNotNull(file, 'file cannot be null')
        }
        this.otherFiles.addAll(files)
    }
}
