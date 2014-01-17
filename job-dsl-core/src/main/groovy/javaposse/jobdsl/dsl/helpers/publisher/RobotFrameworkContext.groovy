package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical;

import com.google.common.base.Preconditions;

import javaposse.jobdsl.dsl.helpers.Context

/**
 * A Job DSL context for Robot Framework. Specifically, 
 * take a look at 
 * https://github.com/jenkinsci/robot-plugin/blob/master/src/main/java/hudson/plugins/robot/RobotPublisher.java
 * 
 * @author Behrooz Nobakht
 */
class RobotFrameworkContext implements Context {

	static String DEFAULT_OUTPUT_PATH = "target/robotframework-reports"
	static String DEFAULT_REPORT_FILE_NAME = "report.html"
	static String DEFAULT_OUTPUT_FILE_NAME = "output.xml"
	static String DEFAULT_LOG_FILE_NAME = "log.html"

	Double passThreshold
	Double unstableThreshold
	Boolean onlyCritical
	String outputPath
	String reportFileName
	String logFileName
	String outputFileName

	def checkAndCreate() {
		this.passThreshold = this.passThreshold > 100.0 ? 100.0 : this.passThreshold
		this.unstableThreshold = this.unstableThreshold < 0.0 ? 0.0 : this.unstableThreshold

		new RobotFrameworkConfiguration(
				passThreshold: this.passThreshold ?: 100.0,
				unstableThreshold: this.unstableThreshold ?: 0.0,
				onlyCritical: this.onlyCritical ?: false,
				outputPath: this.outputPath ?: DEFAULT_OUTPUT_PATH,
				logFileName: this.logFileName ?: DEFAULT_LOG_FILE_NAME,
				reportFileName: this.reportFileName ?: DEFAULT_REPORT_FILE_NAME,
				outputFileName: this.outputFileName ?: DEFAULT_OUTPUT_FILE_NAME
				)
	}

	@Canonical
	class RobotFrameworkConfiguration {
		Double passThreshold
		Double unstableThreshold
		Boolean onlyCritical
		String outputPath
		String reportFileName
		String logFileName
		String outputFileName
	}
}
